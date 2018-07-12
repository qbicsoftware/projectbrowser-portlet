package life.qbic.portal.portlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Layout;
import com.vaadin.ui.HorizontalLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entry point for portlet projectbrowser-portlet. This class derives from {@link QBiCPortletUI}, which is found in the {@code portal-utils-lib} library.
 * 
 * @see https://github.com/qbicsoftware/portal-utils-lib
 */
@Theme("mytheme")
@SuppressWarnings("serial")
@Widgetset("life.qbic.portal.portlet.AppWidgetSet")
public class ProjectBrowserPortlet extends QBiCPortletUI {

    private static final Logger LOG = LogManager.getLogger(ProjectBrowserPortlet.class);

    @Override
    protected Layout getPortletContent(final VaadinRequest request) {
        LOG.info("Generating content for {}", ProjectBrowserPortlet.class);
        
        // TODO: generate content for your portlet
        //       this method returns any non-null layout to avoid a NullPointerException later on
        return new HorizontalLayout();
    }    
}