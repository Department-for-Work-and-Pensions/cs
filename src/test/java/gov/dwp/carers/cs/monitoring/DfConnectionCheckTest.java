package gov.dwp.carers.cs.monitoring;

import gov.dwp.carers.CADSHealthCheck;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by peterwhitehead on 07/07/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class DfConnectionCheckTest {

    private DfConnectionCheck dfConnectionCheck;

    @Mock
    private Environment environment;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void setUp() throws Exception {
        dfConnectionCheck = new DfConnectionCheck("cs", "3.00", environment, restTemplate);

    }

    @Test
    public void testCheck() throws Exception {
        whenGetPropertyCalledReturn("df.url");
        whenRestTemplateCalledReturn(HttpStatus.OK);
        thenCheckShouldReturn(CADSHealthCheck.Result.healthy());
    }

    @Test
    public void testCheckUnhealthy() throws Exception {
        whenGetPropertyCalledReturn("df.url");
        whenRestTemplateCalledReturn(HttpStatus.SERVICE_UNAVAILABLE);
        thenCheckShouldReturn(CADSHealthCheck.Result.unhealthy("df ping failed: 503."));
    }

    @Test
    public void testCheckUnhealthyException() throws Exception {
        whenGetPropertyCalledReturn("df.url");
        whenRestTemplateThrowsException();
        thenCheckShouldReturn(CADSHealthCheck.Result.unhealthy(new RestClientException("test")).toString());
    }

    private void whenGetPropertyCalledReturn(final String property) {
        when(environment.getProperty(property)).thenReturn("ping");
    }

    private void whenRestTemplateCalledReturn(final HttpStatus status) {
        final ResponseEntity<String> response = new ResponseEntity("", status);
        when(restTemplate.getForEntity(anyString(), Mockito.<Class<String>> any())).thenReturn(response);
    }

    private void whenRestTemplateThrowsException() {
        when(restTemplate.getForEntity(anyString(), Mockito.<Class<String>> any())).thenThrow(new RestClientException("test"));
    }

    private void thenCheckShouldReturn(final CADSHealthCheck.Result result) {
        assertThat(dfConnectionCheck.check(), is(result));
    }

    private void thenCheckShouldReturn(final String result) {
        assertThat(dfConnectionCheck.check().toString(), is(result));
    }
}