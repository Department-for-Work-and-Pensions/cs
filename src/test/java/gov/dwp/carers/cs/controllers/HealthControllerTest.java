package gov.dwp.carers.cs.controllers;

import gov.dwp.carers.monitor.MonitorRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@TestPropertySource(value="classpath:test.application.properties")
@RunWith(MockitoJUnitRunner.class)
public class HealthControllerTest {
    @Mock
    private MonitorRegistration monitorRegistration;

    private HealthController healthController;

    @Before
    public void setUp() {
        healthController = new HealthController(monitorRegistration);
    }

    @Test
    public void testPing() {
        assertThat(healthController.ping(), is(""));
    }

    @Test
    public void testSuccessfulHealthCheck() throws IOException {
        final String result = "<pre>[ {\n  \"application name\" : \"null\",\n  \"version\" : \"null\",\n  \"name\" : \"cs-queue-health\",\n  \"Result\" : {\n    \"isHealthy\" : \"true\",\n    \"message\" : \"\",\n    \"error\" : \"\"\n  }\n} ]</pre>";
        whenRunningHealthReports(result);
        thenHealthReportShouldContain(result);
    }

    @Test
    public void testHealthCheckReturnsNoResults() throws IOException {
        whenRunningHealthReports(null);
        thenHealthReportShouldContain("Failed health check.");
    }

    @Test
    public void testHealthCheckThrowsException() throws IOException {
        whenRunningHealthReportsThrow();
        thenHealthReportShouldContain("Failed health check.");
    }

    private void whenRunningHealthReports(final String result) throws IOException {
        when(monitorRegistration.retrievePrintFriendlyHealthCheck()).thenReturn(result);
    }

    private void whenRunningHealthReportsThrow() throws IOException {
        when(monitorRegistration.retrievePrintFriendlyHealthCheck()).thenThrow(new NullPointerException("null"));
    }

    private void thenHealthReportShouldContain(final String result) {
        final String health = healthController.health();
        assertThat(health, is(result));
    }
}
