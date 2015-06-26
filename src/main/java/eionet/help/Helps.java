package eionet.help;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Vector;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


import eionet.help.util.SQLGenerator;
import eionet.help.util.Util;

/**
 *
 */
public class Helps {

    public static final String RESOURCE_BUNDLE_NAME = "help";
    /**
     * Conventional location of data sources in JNDI.
     */
    static final String JDBC_SUBCONTEXT = "jdbc";
    /**
     * Tomcat puts its stuff under java:comp/env.
     */
    public static final String TOMCAT_CONTEXT = "java:comp/env/";
    /**
     * Location of data source if not configured for something else.
     */
    static final String DATASOURCE_NAME = "jdbc/helpdb";

    private static Hashtable helps = null;
    private static Hashtable<Object, Object> props = null;

    public Helps() {
    }

    /**
     * @param screenID
     * @param areaID
     * @return
     */
    public static String get(String screenID, String areaID) {
        return get(screenID, areaID, null);
    }

    /**
     * @param screenID
     * @param areaID
     * @param lang
     * @return
     */
    public static String get(String screenID, String areaID, String lang) {
        if (screenID == null || areaID == null) {
            return null;
        }
        if (helps == null) {
            try {
                load();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        if (helps == null) {
            return "";
        }
        Screen screen = (Screen) helps.get(screenID);
        if (screen == null) {
            return "";
        }
        Area area = screen.getArea(areaID);
        if (area == null) {
            return "";
        } else {
            return area.getHTML(lang);
        }
    }

    /**
     * @param screenID
     * @param areaID
     * @return
     */
    public static String getPopupWidth(String screenID, String areaID) {
        return getPopupWidth(screenID, areaID, null);
    }

    /**
     * @param screenID
     * @param areaID
     * @param lang
     * @return
     */
    public static String getPopupWidth(String screenID, String areaID, String lang) {
        if (screenID == null || areaID == null) {
            return null;
        }
        if (helps == null) {
            try {
                load();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        if (helps == null) {
            return "";
        }
        Screen screen = (Screen) helps.get(screenID);
        if (screen == null) {
            return "";
        }
        Area area = screen.getArea(areaID);
        if (area == null) {
            return "";
        } else {
            String w = area.getPopupWidth();
            return w != null ? w : "";
        }
    }

    /**
     * @param screenID
     * @param areaID
     * @return
     */
    public static String getPopupLength(String screenID, String areaID) {
        return getPopupLength(screenID, areaID, null);
    }

    /**
     * @param screenID
     * @param areaID
     * @param lang
     * @return
     */
    public static String getPopupLength(String screenID, String areaID, String lang) {
        if (screenID == null || areaID == null) {
            return null;
        }
        if (helps == null) {
            try {
                load();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        if (helps == null) {
            return "";
        }
        Screen screen = (Screen) helps.get(screenID);
        if (screen == null) {
            return "";
        }
        Area area = screen.getArea(areaID);
        if (area == null) {
            return "";
        } else {
            String h = area.getPopupLength();
            return h != null ? h : "";
        }
    }

    /**
     * @return
     * @throws HelpException
     */
    public static Hashtable getHelps() throws HelpException {
        if (helps == null) {
            load();
        }
        return helps;
    }

    /**
     * Load the help texts in the <code>helps</code> member variable.
     * @throws HelpException
     */
    public static void load() throws HelpException {
        Statement stmtScreens;
        PreparedStatement pstmtScreenAreas;
        ResultSet rsScreens;
        Exception exception;
        helps = new Hashtable();
        StringBuffer buf = new StringBuffer("select * from HLP_SCREEN");
        Connection conn = null;
        stmtScreens = null;
        pstmtScreenAreas = null;
        rsScreens = null;
        ResultSet rsScreenAreas = null;
        try {
            conn = HelpsDB.getConnection();
            stmtScreens = conn.createStatement();
            rsScreens = stmtScreens.executeQuery(buf.toString());
            buf = new StringBuffer("select * from HLP_AREA where SCREEN_ID=? order by AREA_ID asc");
            pstmtScreenAreas = conn.prepareStatement(buf.toString());
            Screen screen;
            for (; rsScreens.next(); helps.put(screen.getID(), screen)) {
                String screenID = rsScreens.getString("SCREEN_ID");
                screen = new Screen(screenID);
                screen.setDescription(rsScreens.getString("DESCRIPTION"));
                pstmtScreenAreas.setString(1, screenID);
                rsScreenAreas = pstmtScreenAreas.executeQuery();
                Area area = null;
                for (; rsScreenAreas.next(); area.setHTML(rsScreenAreas.getString("HTML"), rsScreenAreas.getString("LANGUAGE"))) {
                    String areaID = rsScreenAreas.getString("AREA_ID");
                    if (area != null && areaID.equals(area.getID())) {
                        continue;
                    }
                    if (area != null) {
                        screen.addArea(area);
                    }
                    area = new Area(rsScreenAreas.getString("SCREEN_ID"), areaID);
                    area.setDescription(rsScreenAreas.getString("DESCRIPTION"));
                    area.setPopupWidth(rsScreenAreas.getString("POPUP_WIDTH"));
                    area.setPopupLength(rsScreenAreas.getString("POPUP_LENGTH"));
                    area.setMd5(rsScreenAreas.getString("MD5"));
                }

                if (area != null) {
                    screen.addArea(area);
                }
            }

        } catch (SQLException sqle) {
            throw new HelpException("DB error: " + sqle.toString());
        } finally {
            HelpsDB.closeResultSet(rsScreenAreas);
            HelpsDB.closeResultSet(rsScreens);
            HelpsDB.closeStatement(pstmtScreenAreas);
            HelpsDB.closeStatement(pstmtScreenAreas);
            HelpsDB.closeConnection();
        }
    }

    /**
     * @param id
     * @param descr
     * @throws HelpException
     */
    public static void createScreen(String id, String descr) throws HelpException {
        Exception exception;
        SQLGenerator gen = new SQLGenerator();
        gen.setTable("HLP_SCREEN");
        gen.setField("SCREEN_ID", id);
        gen.setField("DESCRIPTION", descr);
        Connection conn = null;
        try {
            conn = HelpsDB.getConnection();
            conn.createStatement().executeUpdate(gen.insertStatement());
        } catch (SQLException sqle) {
            throw new HelpException("DB error: " + sqle.toString());
        } finally {
            HelpsDB.closeConnection();
        }
    }

    /**
     * @param screenID
     * @param id
     * @param descr
     * @throws HelpException
     */
    public static void createArea(String screenID, String id, String descr) throws HelpException {
        Exception exception;
        SQLGenerator gen = new SQLGenerator();
        gen.setTable("HLP_AREA");
        gen.setField("SCREEN_ID", screenID);
        gen.setField("AREA_ID", id);
        gen.setField("DESCRIPTION", descr);
        Connection conn = null;
        try {
            conn = HelpsDB.getConnection();
            conn.createStatement().executeUpdate(gen.insertStatement());
        } catch (SQLException sqle) {
            throw new HelpException("DB error: " + sqle.toString());
        } finally {
            HelpsDB.closeConnection();
        }
    }

    /**
     * @param ids
     * @throws HelpException
     */
    public static void removeScreens(Vector ids) throws HelpException {
        Exception exception;
        if (ids == null || ids.size() == 0) {
            return;
        }
        Connection conn = null;
        try {
            conn = HelpsDB.getConnection();
            Statement stmt = conn.createStatement();
            for (int i = 0; i < ids.size(); i++) {
                String id = (String) ids.get(i);
                stmt.executeUpdate("delete from HLP_SCREEN where SCREEN_ID=" + Util.strLiteral(id));
                stmt.executeUpdate("delete from HLP_AREA where SCREEN_ID=" + Util.strLiteral(id));
            }

        } catch (SQLException sqle) {
            throw new HelpException("DB error: " + sqle.toString());
        } finally {
            HelpsDB.closeConnection();
        }
    }

    /**
     * @param screenID
     * @param ids
     * @throws HelpException
     */
    public static void removeAreas(String screenID, Vector ids) throws HelpException {
        Exception exception;
        if (ids == null || ids.size() == 0) {
            return;
        }
        Connection conn = null;
        try {
            conn = HelpsDB.getConnection();
            for (int i = 0; i < ids.size(); i++) {
                String id = (String) ids.get(i);
                conn.createStatement().executeUpdate(
                        "delete from HLP_AREA where SCREEN_ID=" + Util.strLiteral(screenID) + " and AREA_ID="
                        + Util.strLiteral(id));
            }

        } catch (SQLException sqle) {
            throw new HelpException("DB error: " + sqle.toString());
        } finally {
            HelpsDB.closeConnection();
        }
    }

    /**
     * @param screenID
     * @param areaID
     * @param text
     * @throws HelpException
     */
    public static void editHtml(String screenID, String areaID, String text) throws HelpException {
        Exception exception;
        StringBuffer buf =
            (new StringBuffer()).append("update HLP_AREA set HTML=").append(Util.strLiteral(text)).append(" where ")
            .append("SCREEN_ID=").append(Util.strLiteral(screenID)).append(" and ").append("AREA_ID=")
            .append(Util.strLiteral(areaID)).append(" and ").append("LANGUAGE=").append(Util.strLiteral(""));
        Connection conn = null;
        try {
            conn = HelpsDB.getConnection();
            conn.createStatement().executeUpdate(buf.toString());
        } catch (SQLException sqle) {
            throw new HelpException("DB error: " + sqle.toString());
        } finally {
            HelpsDB.closeConnection();
        }
    }

    /**
     *
     * @param screenID
     * @param areaID
     * @param html
     * @param popupWidth
     * @param popupLength
     * @throws HelpException
     */
    public static void editArea(String screenID, String areaID, String html, String popupWidth, String popupLength)
    throws HelpException {
        editArea(screenID, areaID, html, popupWidth, popupLength, null);
    }

    /**
     *
     * @param screenID
     * @param areaID
     * @param html
     * @param popupWidth
     * @param popupLength
     * @param description
     * @throws HelpException
     */
    public static void editArea(String screenID, String areaID, String html, String popupWidth, String popupLength,
            String description) throws HelpException {
        Exception exception;
        SQLGenerator gen = new SQLGenerator();
        gen.setTable("HLP_AREA");
        if (!Util.nullString(html)) {
            gen.setField("HTML", html);
        }
        if (!Util.nullString(popupWidth)) {
            gen.setField("POPUP_WIDTH", popupWidth);
        }
        if (!Util.nullString(popupLength)) {
            gen.setField("POPUP_LENGTH", popupLength);
        }
        if (description != null) {
            gen.setField("DESCRIPTION", description);
        }

        if (Util.nullString(gen.getValues())) {
            return;
        }

        StringBuffer buf =
            (new StringBuffer(gen.updateStatement())).append(" where ").append("SCREEN_ID=").append(Util.strLiteral(screenID))
            .append(" and ").append("AREA_ID=").append(Util.strLiteral(areaID)).append(" and ").append("LANGUAGE=")
            .append(Util.strLiteral(""));
        Connection conn = null;
        try {
            conn = HelpsDB.getConnection();
            conn.createStatement().executeUpdate(buf.toString());
        } catch (SQLException sqle) {
            throw new HelpException("DB error: " + sqle.toString());
        } finally {
            HelpsDB.closeConnection();
        }
    }

    /**
     * Invalidate cached pages.
     */
    public static void reset() {
        helps = null;
    }

    /**
     * Reset properties for unit tests.
     */
    static void resetProps() {
        reset();
        props = null;
    }

    /**
     * Load properties into the props member variable.
     *
     * @throws HelpException if the properties file is missing.
     */
    public static Hashtable<Object, Object> getProperties() throws HelpException {
        if (props == null) {
            props = new Hashtable<Object, Object>();
            Context initContext = null;
            try {
                initContext = new InitialContext();
                if (initContext != null) {
                    // Load from JNDI. Tomcat puts its stuff under java:comp/env:
                    for (Enumeration<Binding> e = initContext.listBindings(TOMCAT_CONTEXT + RESOURCE_BUNDLE_NAME); e.hasMoreElements();) {
                        Binding binding = e.nextElement();
                        props.put(binding.getName(), binding.getObject());
                    }
                }
            } catch (NamingException mre) {
                // throw new HelpException("JNDI not configured properly");
            }
            try {
                // Also add the JDBC subcontext as that is a convention.
                if (initContext != null) {
                    for (Enumeration<Binding> e = initContext.listBindings(TOMCAT_CONTEXT + JDBC_SUBCONTEXT); e.hasMoreElements();) {
                        Binding binding = e.nextElement();
                        props.put(JDBC_SUBCONTEXT + "/" + binding.getName(), binding.getObject());
                    }
                }
            } catch (NamingException mre) {
                // throw new HelpException("JNDI not configured properly");
            }

            // Load from properties file
            if (props.size() == 0 || props.containsKey("propertiesfile")) {
                try {
                    Properties fileProps = new Properties();
                    InputStream inStream = null;

                    if (props.containsKey("propertiesfile")) {
                        try {
                            inStream = new FileInputStream((String) props.get("propertiesfile"));
                        } catch (Exception e) {
                            throw new HelpException("Properties file not found");
                        }
                    } else {
                        inStream = Helps.class.getResourceAsStream("/" + RESOURCE_BUNDLE_NAME + ".properties");
                        if (inStream == null) {
                            throw new HelpException("Properties file is not found in the classpath");
                        }
                    }
                    fileProps.load(inStream);
                    inStream.close();
                    props.putAll(fileProps);
                } catch (IOException mre) {
                    throw new HelpException("Properties file is not readable");
                }
            }
        }
        return props;
    }

}
