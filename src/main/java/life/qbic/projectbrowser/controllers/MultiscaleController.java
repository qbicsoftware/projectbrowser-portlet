/*******************************************************************************
 * QBiC Project qNavigator enables users to manage their projects. Copyright (C) "2016” Christopher
 * Mohr, David Wojnar, Andreas Friedrich
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package life.qbic.projectbrowser.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import life.qbic.projectbrowser.model.notes.Note;
import life.qbic.projectbrowser.model.notes.Notes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import life.qbic.projectbrowser.helpers.HistoryReader;
import life.qbic.openbis.openbisclient.OpenBisClient;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Experiment;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Sample;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.SampleFetchOption;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.SearchCriteria;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.SearchCriteria.MatchClause;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.SearchCriteria.MatchClauseAttribute;
import ch.systemsx.cisd.openbis.generic.shared.dto.EventPE.EntityType;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

public class MultiscaleController implements Serializable {

  private static final Logger LOG = LogManager.getLogger(MultiscaleController.class);


  private OpenBisClient openbis;
  private JAXBElement<Notes> jaxbelem;
  private String user;
  private String currentId;
  private String currentCode;

  public MultiscaleController(OpenBisClient openbis, String user) {
    this.openbis = openbis;
    this.user = user;
  }

  /**
   * 
   */
  private static final long serialVersionUID = -8194363636454560096L;

  public boolean isReady() {
    return jaxbelem != null && jaxbelem.getValue() != null;
  }

  public List<Note> getNotes() {
    return jaxbelem.getValue().getNote();
  }

  public boolean update(String id, EntityType type) {
    jaxbelem = null;
    String xml = null;
    if (type.equals(EntityType.EXPERIMENT)) {
      Experiment e = openbis.getExperimentById2(id).get(0);
      xml = e.getProperties().get("Q_NOTES");
      currentCode = e.getCode();
    } else {
      EnumSet<SampleFetchOption> fetchOptions = EnumSet.of(SampleFetchOption.PROPERTIES);
      SearchCriteria sc = new SearchCriteria();
      sc.addMatchClause(MatchClause.createAttributeMatch(MatchClauseAttribute.CODE, id));
      List<Sample> samples = openbis.getOpenbisInfoService()
          .searchForSamplesOnBehalfOfUser(openbis.getSessionToken(), sc, fetchOptions, "admin");
      if (samples != null && samples.size() == 1) {
        Sample sample = samples.get(0);
        currentCode = sample.getCode();
        xml = sample.getProperties().get("Q_NOTES");
      }
    }
    try {
      if (xml != null) {
        jaxbelem = HistoryReader.parseNotes(xml);
      } else {
        jaxbelem = new JAXBElement<Notes>(new QName(""), Notes.class, new Notes());
      }
      currentId = id;
      return true;
    } catch (IndexOutOfBoundsException | JAXBException | NullPointerException e) {
      currentId = null;
      currentCode = null;
      LOG.error("Error parsing XML");
      e.printStackTrace();
    }
    return false;
  }

//  public BeanItemContainer<Note> getContainer() {
//    BeanItemContainer<Note> container = new BeanItemContainer<Note>(Note.class);
//    container.addAll(getNotes());
//    return container;
//  }

  public String getUser() {
    // TODO Auto-generated method stub
    return user;
  }

  public boolean addNote(Note note) {
    if (currentId != null) {
      jaxbelem.getValue().getNote().add(note);
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("id", currentId);
      params.put("user", note.getUsername());
      params.put("comment", note.getComment());
      params.put("time", note.getTime());
      openbis.ingest("DSS1", "add-to-xml-note", params);
      return true;
    }
    return false;
  }

  public String getcurrentCode() {
    return currentCode;
  }

  public List<String> getSearchResults(String samplecode) {
    EnumSet<SampleFetchOption> fetchOptions = EnumSet.of(SampleFetchOption.PROPERTIES);
    SearchCriteria sc = new SearchCriteria();
    sc.addMatchClause(
        MatchClause.createAttributeMatch(MatchClauseAttribute.CODE, samplecode + "*"));
    List<Sample> samples = openbis.getOpenbisInfoService()
        .searchForSamplesOnBehalfOfUser(openbis.getSessionToken(), sc, fetchOptions, user);
    List<String> ret = new ArrayList<String>(samples.size());
    for (Sample sample : samples) {
      ret.add(sample.getCode());
    }
    return ret;
  }

  public String getLiferayUser(String userID) {
    Company company = null;
    long companyId = 1;
    String userString = "";
    try {
      String webId = PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID);
      company = CompanyLocalServiceUtil.getCompanyByWebId(webId);
      companyId = company.getCompanyId();
      LOG.debug(
          String.format("Using webId %s and companyId %d to get Portal User", webId, companyId));
    } catch (PortalException | SystemException e) {
      LOG
          .error("liferay error, could not retrieve companyId. Trying default companyId, which is "
              + companyId, e.getStackTrace());
    }

    User user = null;
    try {
      user = UserLocalServiceUtil.getUserByScreenName(companyId, userID);
    } catch (PortalException | SystemException e) {
    }

    if (user == null) {
      LOG.warn(String.format("Openbis user %s appears to not exist in Portal", userID));
      userString = userID;
    } else {
      String fullname = user.getFullName();
      String email = user.getEmailAddress();
      userString += ("<a href=\"mailto:");
      userString += (email);
      userString += ("\" style=\"color: #0068AA; text-decoration: none\">");
      userString += (fullname);
      userString += ("</a>");
    }
    return userString;
  }
}
