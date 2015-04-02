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
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.Test;
import org.h2.jdbcx.JdbcDataSource;


import static org.junit.Assert.assertEquals;

/**
 * Let the module connect via JNDI. Check that it doesn't fall back to loading
 * from the properties file.
 */
public class JNDITest {

    private static JdbcDataSource dataSource = null;

    @BeforeClass
    public static void loadDB() throws Exception {
        Liquibase liquibase = null;
        Connection conn = createDataSource().getConnection();
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

    /**
     * Construct DataSource.
     */
    private static JdbcDataSource createDataSource() throws Exception {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:file:target/jndidb");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");
        return dataSource;
    }

    @Before
    public void setUpIC() throws Exception {
        JNDISupport.setUpCore();
    }   

    @After
    public void cleanUpIC() throws Exception {
        JNDISupport.cleanUp();
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

    /**
     * Lookup data source under jdbc/helpdb.
     */
    @Test
    public void useDirectReference() throws Exception {
        JNDISupport.addSubCtxToTomcat("jdbc");
        JNDISupport.addPropToTomcat(Helps.DATASOURCE_NAME, dataSource);
        Connection conn = HelpsDB.getConnection();
        IDataSet dataSet = readDataSet();
        cleanlyInsert(dataSet, conn);

        String html = Helps.get("jndi", "announcements", "en");
        assertEquals("We now have JNDI unit tests", html);
    }

    @Test
    public void useIndirectReference() throws Exception {
        JNDISupport.addSubCtxToTomcat("jdbc");
        JNDISupport.addPropToTomcat("jdbc/roddb", dataSource);
        JNDISupport.addSubCtxToTomcat(Helps.RESOURCE_BUNDLE_NAME);
        JNDISupport.addPropToTomcat(Helps.RESOURCE_BUNDLE_NAME + "/jndiname", "jdbc/roddb");
        Connection conn = HelpsDB.getConnection();
        IDataSet dataSet = readDataSet();
        cleanlyInsert(dataSet, conn);

        String html = Helps.get("jndi", "announcements", "en");
        assertEquals("We now have JNDI unit tests", html);
    }
}
