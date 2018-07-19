package life.qbic.projectbrowser.helpers;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import life.qbic.portal.portlet.ProjectBrowserPortlet;

public class ProjectSummaryReadyRunnable implements Runnable {
  private SummaryFetcher fetcher;
  private Window loadingWindow;
  private String project;

  public ProjectSummaryReadyRunnable(SummaryFetcher fetcher, Window window, String projectCode) {
    this.fetcher = fetcher;
    this.loadingWindow = window;
    this.project = projectCode;
  }

  @Override
  public void run() {
    if (loadingWindow.getParent() != null)
      loadingWindow.close();
    if (!fetcher.wasSuccessful()) {
      Utils.Notification("Summary failed.","Summary can't be shown for this project.","error");
    } else {
      // loading finished, remove loading window if user didn't close it already

      // show results in new window
      Window subWindow = new Window(" Summary for project " + project);
      subWindow.setContent(fetcher.getWindowContent());
      // Center it in the browser window
      subWindow.center();
      subWindow.setModal(true);
      subWindow.setIcon(FontAwesome.LIST);
      subWindow.setHeight("75%");
      subWindow.setResizable(false);

      ProjectBrowserPortlet ui = (ProjectBrowserPortlet) UI.getCurrent();
      ui.addWindow(subWindow);
    }
  }
}
