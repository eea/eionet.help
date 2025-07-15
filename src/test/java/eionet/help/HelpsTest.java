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

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class HelpsTest {

    @BeforeClass
    public static void loadDB() throws Exception {
        Connection conn = HelpsDB.getConnection();
        Liquibase liquibase = null;
        try {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
            liquibase = new Liquibase("db-struct.xml", new FileSystemResourceAccessor("."), database);
            liquibase.update("test");
            conn.close();
            conn = null;
        } catch (LiquibaseException e) {
            throw new DatabaseException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.close();
                } catch (SQLException e) {
                    //nothing to do
                }
            }
        }
    }

    @AfterClass
    public static void cleanUp() {
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
        IDataSet dataSet = readDataSet("testdata.xml");
        cleanlyInsert(dataSet, conn);
    }


    private IDataSet readDataSet(String sourceFile) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(sourceFile);
        return new FlatXmlDataSetBuilder().build(is);
    }

    private void cleanlyInsert(IDataSet dataSet, Connection conn) throws Exception {
        IDatabaseTester databaseTester = new DefaultDatabaseTester(new DatabaseConnection(conn));
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void loadProps() throws Exception {
        Hashtable<Object, Object> rb = Helps.getProperties();
    }

    @Test
    public void loadData() throws Exception {
        String html = Helps.get("nr1", "announcements", "en");
        assertEquals("We now have unit tests", html);
    }

}
