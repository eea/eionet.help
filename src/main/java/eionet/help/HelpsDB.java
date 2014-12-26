package eionet.help;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 *
 */
public class HelpsDB {

    /**
     *
     */
    public HelpsDB() {
    }

    /**
     * @return
     * @throws HelpException
     */
    public static Connection getConnection() throws HelpException {
        try {
            if (conn == null || conn.isClosed()) {
                ResourceBundle props = Helps.getProperties();
                Class.forName(props.getString("db.driver"));
                conn =
                    DriverManager.getConnection(props.getString("db.url"), props.getString("db.user"),
                            props.getString("db.pwd"));
            }
        } catch (SQLException sqle) {
            throw new HelpException("DB error: " + sqle.toString());
        } catch (ClassNotFoundException cnfe) {
            throw new HelpException("DB driver problem: " + cnfe.toString());
        }
        return conn;
    }

    /**
     *
     */
    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * @param rs
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * @param stmt
     */
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * @param rs
     * @param stmt
     */
    public static void closeConnection(ResultSet rs, Statement stmt) {
        closeResultSet(rs);
        closeStatement(stmt);
        closeConnection();
    }

    private static Connection conn = null;

}
