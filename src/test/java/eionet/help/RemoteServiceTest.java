package eionet.help;

import eionet.acl.AppUser;
import eionet.acl.SignOnException;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.Rule;

import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class RemoteServiceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void authenticationFailure() throws Exception {
        AppUser u = new AppUser();
        exception.expect(SignOnException.class);
        exception.expectMessage("Not authenticated");
        u.authenticate("test", "failure");
    }

    @Test
    public void instantiation() throws Exception {
        AppUser u = new AppUser();
        u.authenticateForTest("test");
        RemoteService r = new RemoteService(u);
        assertNotNull(r);
    }
}
