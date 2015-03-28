package eionet.help;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import javax.sql.DataSource;

/**
 *
 */
public class HelpsDB {

    private static Connection conn = null;

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
                Hashtable<Object, Object> props = Helps.getProperties();
                if (props.containsKey("help/jndiname")) {
                    String jndiName = (String) props.get("help/jndiname");
                    DataSource dataSource = (DataSource) props.get(jndiName);
                    conn = dataSource.getConnection();
                } else {
                    if (props.containsKey("jdbc/helpdb")) {
                        DataSource dataSource = (DataSource) props.get("jdbc/helpdb");
                        conn = dataSource.getConnection();
                    } else {
                        Class.forName((String)props.get("db.driver"));
                        conn = DriverManager.getConnection((String)props.get("db.url"),
                                (String)props.get("db.user"),
                                (String)props.get("db.pwd"));
                    }
                }
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

}
