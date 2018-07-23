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

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import life.qbic.projectbrowser.components.CustomVisibilityComponent;
import life.qbic.projectbrowser.helpers.VisibilityChangeListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import life.qbic.projectbrowser.model.ProjectBean;

public class Utils {

    private static final Logger LOG = LogManager.getLogger(Utils.class);

    private static final Set<String> EXCLUSION_LIST;

    static {
        EXCLUSION_LIST = new HashSet<>();
        EXCLUSION_LIST.add("samples");
        EXCLUSION_LIST.add("properties");
        EXCLUSION_LIST.add("controlledVocabularies");
        EXCLUSION_LIST.add("typeLabels");
        EXCLUSION_LIST.add("containsData");
        EXCLUSION_LIST.add("parents");
        EXCLUSION_LIST.add("datasets");
        EXCLUSION_LIST.add("isSelected");
        EXCLUSION_LIST.add("parent");
        EXCLUSION_LIST.add("root");
        EXCLUSION_LIST.add("children");
        EXCLUSION_LIST.add("dssPath");
        EXCLUSION_LIST.add("experiments");
    }


    /**
     * Checks if a String can be parsed to an Integer
     *
     * @param s a String
     * @return true, if the String can be parsed to an Integer successfully, false otherwise
     */
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Parses a whole String list to integers and returns them in another list.
     *
     * @param strings List of Strings
     * @return list of integer representations of the input list
     */
    public static List<Integer> strArrToInt(List<String> strings) {
        List<Integer> res = new ArrayList<Integer>();
        for (String s : strings) {
            res.add(Integer.parseInt(s));
        }
        return res;
    }

    /**
     * Maps an integer to a char representation. This can be used for computing the checksum.
     *
     * @param i number to be mapped
     * @return char representing the input number
     */
    public static char mapToChar(int i) {
        i += 48;
        if (i > 57) {
            i += 7;
        }
        return (char) i;
    }

    /**
     * Checks which of two Strings can be parsed to a larger Integer and returns it.
     *
     * @param a a String
     * @param b another String
     * @return the String that represents the larger number.
     */
    public static String max(String a, String b) {
        int a1 = Integer.parseInt(a);
        int b1 = Integer.parseInt(b);
        if (Math.max(a1, b1) == a1) {
            return a;
        } else {
            return b;
        }
    }

    /**
     * Creates a string with leading zeroes from a number
     *
     * @param id number
     * @param length of the final string
     * @return the completed String with leading zeroes
     */
    public static String createCountString(int id, int length) {
        String res = Integer.toString(id);
        while (res.length() < length) {
            res = "0" + res;
        }
        return res;
    }

    /**
     * Increments the value of an upper case char. When at "X" restarts with "A".
     *
     * @param c the char to be incremented
     * @return the next letter in the alphabet relative to the input char
     */
    public static char incrementUppercase(char c) {
        if (c == 'X') {
            return 'A';
        } else {
            int charValue = c;
            return (char) (charValue + 1);
        }
    }

    public static StreamResource getTSVStream(final String content, String id) {
        StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
            private static final long serialVersionUID = 946357391804404061L;

            @Override
            public InputStream getStream() {
                try {
                    InputStream is = new ByteArrayInputStream(content.getBytes());
                    return is;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            // remove slashes and get rid of leading underscore afterwards
        }, String.format("%s.tsv", id));
        return resource;
    }

    // TODO fix and test
    public static String containerToString(final BeanItemContainer container) {
        return containerToString(container, new AtomicBoolean(false));
    }

    /**
     * Converts an item container into an exportable string (tab-separated format). It is possible to stop the execution of this method by changing the
     * value of the {@code stopProcessing} parameter to {@code true}. This can be done from another thread, for instance.
     *
     * @param container the container to export.
     * @param stopProcessing boolean that indicates whether this method should stop processing elements. If this method stops processing elements, the
     * return value should be discarded, since it will not be complete and/or will be improperly formatted.
     * @return the exportable string.
     */
    public static String containerToString(final BeanItemContainer container, final AtomicBoolean stopProcessing) {
        final StringBuilder stringValue = new StringBuilder();

        final Collection<?> propertyIDs = container.getContainerPropertyIds();

        for (final Object o : propertyIDs) {
            if (stopProcessing.get()) {

            }
            final String propertyAsString = o.toString();
            if (!EXCLUSION_LIST.contains(propertyAsString)) {
                stringValue.append(propertyAsString.replaceAll("project", "sub-project").replace("space", "project")).append('\t');
            }
        }
        stringValue.append('\n');

        for (final Object id : container.getItemIds()) {
            final Item item = container.getItem(id);

            for (final Object propertyId : propertyIDs) {
                // Could be extended to an exclusion list if we don't want to show further columns
                if (!EXCLUSION_LIST.contains(propertyId.toString())) {
                    final Property prop = item.getItemProperty(propertyId);
                    if (prop.getValue() == null) {
                        stringValue.append("-\t");
                    } else {
                        stringValue.append(prop.toString()).append('\t');
                    }
                }
            }
            stringValue.append('\n');
        }
        return stringValue.toString();
    }

    public static void printMapContent(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public static String getTime() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZZ");
        return ft.format(dNow);
    }

    public static HorizontalLayout questionize(Component c, final String info, final String header) {
        final HorizontalLayout res = new HorizontalLayout();
        res.setSpacing(true);
        if (c instanceof CustomVisibilityComponent) {
            CustomVisibilityComponent custom = (CustomVisibilityComponent) c;
            c = custom.getInnerComponent();
            custom.addListener(new VisibilityChangeListener() {

                @Override
                public void setVisible(boolean b) {
                    res.setVisible(b);
                }
            });
        }

        res.setVisible(c.isVisible());
        res.setCaption(c.getCaption());
        c.setCaption(null);
        res.addComponent(c);

        PopupView pv = new PopupView(new Content() {

            @Override
            public Component getPopupComponent() {
                Label l = new Label(info, ContentMode.HTML);
                l.setCaption(header);
                l.setIcon(FontAwesome.INFO);
                l.setWidth("250px");
                l.addStyleName("info");
                return new VerticalLayout(l);
            }

            @Override
            public String getMinimizedValueAsHTML() {
                return "[?]";
            }
        });
        pv.setHideOnMouseOut(false);

        res.addComponent(pv);

        return res;
    }

    public static void Notification(String title, String description, String type) {
        Notification notify = new Notification(title, description);
        notify.setPosition(Position.TOP_CENTER);
        if (type.equals("error")) {
            notify.setDelayMsec(16000);
            notify.setIcon(FontAwesome.FROWN_O);
            notify.setStyleName(ValoTheme.NOTIFICATION_ERROR + " " + ValoTheme.NOTIFICATION_CLOSABLE);
        } else if (type.equals("success")) {
            notify.setDelayMsec(8000);
            notify.setIcon(FontAwesome.SMILE_O);
            notify.setStyleName(ValoTheme.NOTIFICATION_SUCCESS + " " + ValoTheme.NOTIFICATION_CLOSABLE);
        } else {
            notify.setDelayMsec(8000);
            notify.setIcon(FontAwesome.COMMENT);
            notify.setStyleName(ValoTheme.NOTIFICATION_TRAY + " " + ValoTheme.NOTIFICATION_CLOSABLE);
        }
        notify.show(Page.getCurrent());
    }

    public static Panel createInfoBox(String caption, String description) {
        Panel panel = new Panel(caption);
        panel.setIcon(FontAwesome.INFO);
        panel.setStyleName(ValoTheme.PANEL_BORDERLESS);
        HorizontalLayout layout = new HorizontalLayout();
        Label label = new Label();
        label.setValue(description);
        layout.addComponent(label);

        panel.setContent(layout);
        return panel;
    }

    public static String usernameToFullName(String username) {
        Company company = null;

        String res = username;
        long companyId = 1;
        try {
            String webId = PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID);
            company = CompanyLocalServiceUtil.getCompanyByWebId(webId);
            companyId = company.getCompanyId();
        } catch (PortalException | SystemException e) {
            LOG
                .error("liferay error, could not retrieve companyId. Trying default companyId, which is "
                    + companyId, e.getStackTrace());
        }
        User user = null;
        try {
            user = UserLocalServiceUtil.getUserByScreenName(companyId, username);
        } catch (PortalException | SystemException e) {
            LOG.warn("got this error while trying to fetch full name of user:");
            LOG.warn(e.getMessage());
            LOG.info("returning username instead.");
        }
        if (user == null) {
            LOG.warn(String.format("Openbis user %s appears to not exist in Portal", username));
        } else {
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            res = firstName + " " + lastName;
        }
        return res;
    }

    public void generateProjectReport(ProjectBean projectBean) {
        Writer report = null;
        try {
            report = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream("/tmp/report.tex"), "utf-8"));

            // write tex file header
            report.write("\\documentclass[ngerman]{scrartcl} \n");
            report.write("\\begin{document} \n");
            report.write(String.format("\\section{Project Report %s} \n", projectBean.getCode()));

            report.write("\\subsection{General Information} \n");
            report.write("");

            report.write("\\end{document}");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
