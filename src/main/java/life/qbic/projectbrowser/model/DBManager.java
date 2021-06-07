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
package life.qbic.projectbrowser.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import life.qbic.projectbrowser.model.userdb.Person;


public class DBManager {
  private DBConfig config;

  private static final Logger LOG = LogManager.getLogger(DBManager.class);

  public DBManager(DBConfig config) {
    this.config = config;
  }

  private void logout(Connection conn) {
    try {
      conn.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private Connection login() {

    String DB_URL = "jdbc:mariadb://" + config.getHostname() + ":" + config.getPort() + "/"
        + config.getSql_database();

    Connection conn = null;

    try {
      Class.forName("org.mariadb.jdbc.Driver");
      conn = DriverManager.getConnection(DB_URL, config.getUsername(), config.getPassword());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return conn;
  }

  public String getInvestigatorForProject(String projectIdentifier) {
    String details = getPersonDetailsForProject(projectIdentifier, "PI");
    return details.split("<br>")[0].trim().replace("<p>", "");
  }

  public List<Person> getPersonWithAffiliations(Integer personID) {
    List<Person> res = new ArrayList<Person>();
    String lnk = "person_affiliation";
    String sql = "SELECT person.*, affiliation.*, " + lnk + ".* FROM person, affiliation, " + lnk
        + " WHERE person.id = " + Integer.toString(personID) + " AND person.id = " + lnk
        + ".person_id and affiliation.id = " + lnk + ".affiliation_id";
    Connection conn = login();
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      ResultSet rs = statement.executeQuery();
      while (rs.next()) {
        int id = rs.getInt("id");
        String user_id = rs.getString("user_id");
        String title = rs.getString("title");
        String first = rs.getString("first_name");
        String last = rs.getString("last_name");
        String eMail = rs.getString("email");

        int affiliationID = rs.getInt("affiliation.id");

        String organization = rs.getString("organization");
        String address_addition = rs.getString("address_addition");
        String affiliation = organization;

        res.add(new Person(id, user_id, title, first, last, eMail, affiliationID, affiliation));
      }
      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    logout(conn);
    return res;
  }

  public String getPersonDetailsForProject(String projectIdentifier, String role) {
    String sql =
        "SELECT projects_persons.*, projects.* FROM projects_persons, projects WHERE projects.openbis_project_identifier = ?"
            + " AND projects.id = projects_persons.project_id AND projects_persons.project_role = ?";

    int id = -1;

    List<Person> personWithAffiliations = new ArrayList<Person>();

    Connection conn = login();
    try (PreparedStatement statement = conn.prepareStatement(sql)) {
      statement.setString(1, projectIdentifier);
      statement.setString(2, role);

      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        id = rs.getInt("person_id");
      }

      personWithAffiliations = getPersonWithAffiliations(id);
    } catch (SQLException e) {
      e.printStackTrace();
      logout(conn);
      // LOG.debug("Project not associated with Investigator. PI will be set to 'Unknown'");
    }

    String details = "";
    if (personWithAffiliations.size() > 0) {
      Person p = personWithAffiliations.get(0);
      String institute = p.getOneAffiliation();

      String title = "";

      if (p.getTitle() != null && !p.getTitle().equals("None")) {
        title = p.getTitle();
      }

      details = String.format("<p>%s %s %s <br> %s <br><br> %s</p>", title, p.getFirst(),
          p.getLast(), institute, p.geteMail());
      // TODO is address important?
    }

    logout(conn);
    return details;
  }

  public boolean changeLongProjectDescription(String projectIdentifier, String description) {
    LOG.info("Adding/Updating long description of project " + projectIdentifier);
    boolean saved = saveOldDescription(projectIdentifier);
    if (!saved)
      LOG.warn("Could not save old project description to database!");
    String sql = "UPDATE projects SET long_description = ? WHERE openbis_project_identifier = ?";
    Connection conn = login();
    PreparedStatement statement = null;
    int res = -1;
    try {
      statement = conn.prepareStatement(sql);
      statement.setString(1, description);
      statement.setString(2, projectIdentifier);
      statement.execute();
      res = statement.getUpdateCount();
      LOG.info("Successful.");
    } catch (SQLException e) {
      LOG.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res != -1;
  }

  private boolean saveOldDescription(String projectIdentifier) {
    String sql = "SELECT * from projects WHERE openbis_project_identifier = ?";
    int id = -1;
    String oldDescription = "";
    String oldTitle = "";
    Connection conn = login();
    PreparedStatement statement = null;
    try {
      statement = conn.prepareStatement(sql);
      statement.setString(1, projectIdentifier);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        id = rs.getInt("id");
        oldDescription = rs.getString("long_description");
        oldTitle = rs.getString("short_title");
      }
    } catch (SQLException e) {
      LOG.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    Date date = new Date();
    Timestamp timestamp = new Timestamp(date.getTime());
    sql =
        "INSERT INTO projects_history (project_id, timestamp, long_description, short_title) VALUES(?, ?, ?, ?)";
    statement = null;
    int res = -1;
    try {
      statement = conn.prepareStatement(sql);
      statement.setInt(1, id);
      statement.setTimestamp(2, timestamp);
      statement.setString(3, oldDescription);
      statement.setString(4, oldTitle);
      statement.execute();
      res = statement.getUpdateCount();
      LOG.info("Successful.");
    } catch (SQLException e) {
      LOG.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    } finally {
      endQuery(conn, statement);
    }
    return res != -1;
  }

  private void endQuery(Connection c, PreparedStatement p) {
    if (p != null)
      try {
        p.close();
      } catch (Exception e) {
        LOG.error("PreparedStatement close problem");
      }
    if (c != null)
      try {
        logout(c);
      } catch (Exception e) {
        LOG.error("Database Connection close problem");
      }
  }

  public String getLongProjectDescription(String projectIdentifier) {
    String sql = "SELECT long_description from projects WHERE openbis_project_identifier = ?";
    String res = "";
    Connection conn = login();
    try {
      PreparedStatement statement = conn.prepareStatement(sql);
      statement.setString(1, projectIdentifier);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        res = rs.getString(1);
      }
    } catch (SQLException e) {
      LOG.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    logout(conn);
    return res;
  }

  public String getProjectName(String projectIdentifier) {
    String sql = "SELECT short_title from projects WHERE openbis_project_identifier = ?";
    String res = "";
    Connection conn = login();
    try {
      PreparedStatement statement = conn.prepareStatement(sql);
      statement.setString(1, projectIdentifier);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        res = rs.getString(1);
      }
    } catch (SQLException e) {
      LOG.error("SQL operation unsuccessful: " + e.getMessage());
      e.printStackTrace();
    }
    logout(conn);
    return res;
  }

}
