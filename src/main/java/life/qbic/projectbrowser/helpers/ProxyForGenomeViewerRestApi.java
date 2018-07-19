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
package life.qbic.projectbrowser.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.util.PortalUtil;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinPortletService;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import life.qbic.portal.utils.ConfigurationManagerFactory;
import life.qbic.portal.utils.PortalUtils;

public class ProxyForGenomeViewerRestApi implements RequestHandler {

  /**
   * 
   */
  private static final long serialVersionUID = 3929423697741337860L;
  private long session;

  public ProxyForGenomeViewerRestApi() {
    Random rand = new Random();
    rand.setSeed(
        System.nanoTime() + Long.parseLong(PortalUtils.getUser().getScreenName(), 36));
    session = rand.nextLong();
    UI.getCurrent().getSession().setAttribute("gv-restapi-session", session);
  }

  @Override
  public boolean handleRequest(VaadinSession session, VaadinRequest request,
      VaadinResponse response) throws IOException {
    if (UI.getCurrent() != null && UI.getCurrent().getPage() != null
        && UI.getCurrent().getPage().getUriFragment() != null) {
      System.out.println(UI.getCurrent().getPage().getUriFragment());
    }
    System.out.println("is handling request");
    System.out.println(request.getPathInfo());
    System.out.println(request.getContextPath());
    PortletRequest portletRequest = VaadinPortletService.getCurrentPortletRequest();
    Map<String, String[]> para = request.getParameterMap();
    Set<Entry<String, String[]>> s = para.entrySet();
    Iterator<Entry<String, String[]>> it = s.iterator();
    while (it.hasNext()) {
      Entry<String, String[]> en = it.next();
      System.out.println("Key: " + en.getKey());
      System.out.println("Value:");
      for (int i = 0; i < en.getValue().length; i++) {
        System.out.println(en.getValue()[i]);
      }
      System.out.println("Next");
    }
    Enumeration<String> enu = portletRequest.getParameterNames();
    while (enu.hasMoreElements()) {
      String su = enu.nextElement();
      System.out.println(su + " " + portletRequest.getParameter(su));
    }
    enu = portletRequest.getPropertyNames();
    while (enu.hasMoreElements()) {
      String su = enu.nextElement();
      System.out.println(su + " " + portletRequest.getProperty(su));
    }
    HttpServletRequest httpRequest = PortalUtil.getHttpServletRequest(portletRequest);
    System.out.println(httpRequest.getPathInfo());
    enu = httpRequest.getParameterNames();
    while (enu.hasMoreElements()) {
      String su = enu.nextElement();
      System.out.println(su + " " + httpRequest.getParameter(su));
    }
    System.out.println(httpRequest.getQueryString());
    enu = PortalUtil.getOriginalServletRequest(httpRequest).getParameterNames();
    System.out.println(PortalUtil.getOriginalServletRequest(httpRequest).getPathInfo());
    while (enu.hasMoreElements()) {
      String su = enu.nextElement();
      System.out
          .println(su + " " + PortalUtil.getOriginalServletRequest(httpRequest).getParameter(su));
    }


    if (!String.valueOf(session).equals(request.getPathInfo())) {
      return false;
    }

    String fileId = request.getParameter("fileId");
    String filepaths = request.getParameter("filepath");
    String removeZeroGenotypes = request.getParameter("removeZeroGenotypes");
    String region = request.getParameter("region");
    String interval = request.getParameter("interval");
    String histogram = request.getParameter("histogram");

    URL u;
    try {
      StringBuilder sb = new StringBuilder();
      sb.append(ConfigurationManagerFactory.getInstance().getGenomeViewerRestApiUrl());
      sb.append(fileId);
      sb.append("/fetch?filepaths=");
      sb.append(filepaths);
      sb.append("&region=");
      sb.append(region);
      if (interval != null) {
        sb.append("&interval=");
        sb.append(interval);
      }
      if (histogram != null) {
        sb.append("&histogram=");
        sb.append(histogram);
      }
      if (removeZeroGenotypes != null) {
        sb.append("");
      }
      u = new URL(sb.toString());
      // u = new
      // URL("http://localhost:7777/vizrest/rest/data/QBAMS001AB.bam/fetch?filepaths=/store/1/0EEF79A2-8140-4FC7-BA67-E51908FE4619/f0/91/36/20141116104428925-3161/original/QBAMS001AB.bam&removeZeroGenotypes=false&region=9:117163200-117164799,9:117164800-117166399,9:117166400-117167999,9:117168000-117169599&interval=1600&h");
      URLConnection urlConnection = u.openConnection();

      urlConnection.setUseCaches(false);
      urlConnection.setDoOutput(false);
      urlConnection.connect();
      InputStream is = urlConnection.getInputStream();
      response.setContentType("application/json");
      response.setHeader("Content-Type", "application/json");
      OutputStream out = response.getOutputStream();
      byte[] buffer = new byte[com.vaadin.server.Constants.DEFAULT_BUFFER_SIZE];
      while (true) {
        int readCount = is.read(buffer);
        if (readCount < 0) {
          break;
        }
        out.write(buffer, 0, readCount);
      }

    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return true;
  }
}
