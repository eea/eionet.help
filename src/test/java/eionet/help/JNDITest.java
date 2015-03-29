package eionet.help;

import eionet.acl.AppUser;
import eionet.acl.SignOnException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.Liquibase;
import liquibase.resource.FileSystemResourceAccessor;
import org.apache.log4j.PropertyConfigurator;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.operation.DatabaseOperation;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.Test;
import org.h2.jdbcx.JdbcDataSource;
import javax.naming.Context;
import javax.naming.InitialContext;


import static org.junit.Assert.assertEquals;

/**
 * Let the module connect via JNDI. Check that it doesn't fall back to loading
 * from the properties file.
 */
public class JNDITest {

    @BeforeClass
    public static void loadDB() throws Exception {
        Liquibase liquibase = null;
        Connection conn = createDataSourceInIC().getConnection();
        try {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
            liquibase = new Liquibase("db-struct.xml", new FileSystemResourceAccessor(), database);
            liquibase.update("test");
        } catch (LiquibaseException e) {
            throw new DatabaseException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    //nothing to do
                }
            }
        }
    }

    private static JdbcDataSource createDataSourceInIC() throws Exception {
        // Construct DataSource
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:file:target/jndidb");
        ds.setUser("sa");
        ds.setPassword("sa");
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");

        Context ic = new InitialContext();
        ic.createSubcontext("java:");
        ic.createSubcontext("java:comp");
        ic.createSubcontext("java:comp/env");
        ic.createSubcontext("java:comp/env/jdbc");
        ic.bind("java:comp/env/" + Helps.DATASOURCE_NAME, ds);
        return ds;
    }

    @AfterClass
    public static void cleanUpIC() throws Exception {
        Context ic = new InitialContext();
        ic.unbind("java:comp/env/" + Helps.DATASOURCE_NAME);
        ic.destroySubcontext("java:comp/env/jdbc");
        ic.destroySubcontext("java:comp/env");
        ic.destroySubcontext("java:comp");
        ic.destroySubcontext("java:");
        Helps.resetProps();
    }   

    /**
     * Initialize the logging system. It is used by dbunit.
     */
    @BeforeClass
    public static void setupLogger() throws Exception {
        Properties logProperties = new Properties();
        logProperties.setProperty("log4j.rootCategory", "DEBUG, CONSOLE");
        logProperties.setProperty("log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender");
        logProperties.setProperty("log4j.appender.CONSOLE.Threshold", "ERROR");
        logProperties.setProperty("log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout");
        logProperties.setProperty("log4j.appender.CONSOLE.layout.ConversionPattern", "- %m%n");
        PropertyConfigurator.configure(logProperties);
    }

    @Before
    public void importDataSet() throws Exception {
        Connection conn = HelpsDB.getConnection();
        IDataSet dataSet = readDataSet();
        cleanlyInsert(dataSet, conn);
    }


    private IDataSet readDataSet() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("testdata-for-jndi.xml");
        return new FlatXmlDataSetBuilder().build(is);
    }

    private void cleanlyInsert(IDataSet dataSet, Connection conn) throws Exception {
        IDatabaseTester databaseTester = new DefaultDatabaseTester(new DatabaseConnection(conn));
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
    }

    @Test
    public void getJNDIScreen() throws Exception {
        String html = Helps.get("jndi", "announcements", "en");
        assertEquals("We now have JNDI unit tests", html);
    }

}
