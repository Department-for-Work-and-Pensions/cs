package gov.dwp.carers.cs.monitoring;

import gov.dwp.carers.CADSHealthCheck;
import gov.dwp.carers.cs.service.database.DatabaseClaimService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by peterwhitehead on 08/09/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class DBHealthCheckTest {

    private DBHealthCheck dbHealthCheck;

    @Mock
    private DatabaseClaimService databaseClaimService;

    @Before
    public void setUp() throws Exception {
        dbHealthCheck = new DBHealthCheck("cs", "3.00", databaseClaimService);
    }

    @Test
    public void testCheck() throws Exception {
        whenDatabaseHealthCalledReturn();
        thenCheckShouldReturn(CADSHealthCheck.Result.healthy());
    }

    @Test
    public void testCheckUnhealthyException() throws Exception {
        whenDatabaseHealthThrowsException();
        thenCheckShouldReturn(CADSHealthCheck.Result.unhealthy(new SQLException("test")).toString());
    }

    private void whenDatabaseHealthCalledReturn() throws SQLException {
        when(databaseClaimService.health()).thenReturn(true);
    }

    private void whenDatabaseHealthThrowsException() throws SQLException {
        when(databaseClaimService.health()).thenThrow(new SQLException("test"));
    }

    private void thenCheckShouldReturn(final CADSHealthCheck.Result result) {
        assertThat(dbHealthCheck.check(), is(result));
    }

    private void thenCheckShouldReturn(final String result) {
        assertThat(dbHealthCheck.check().toString(), is(result));
    }
}