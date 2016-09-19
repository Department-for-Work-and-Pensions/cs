package gov.dwp.carers.cs.service.messaging;

import gov.dwp.carers.cs.MessageDistributionException;
import gov.dwp.carers.cs.model.ClaimStatus;
import gov.dwp.carers.cs.model.ClaimSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by peterwhitehead on 12/09/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class DfStatusesImplTest {
    private List<ClaimSummary> claimSummaries;
    private List<ClaimStatus> claimStatuses;

    private DfStatusesImpl dfStatusesImpl;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void setUp() throws Exception {
        dfStatusesImpl = new DfStatusesImpl(restTemplate, Boolean.TRUE, "http://localhost:9013");
    }

    @Test
    public void testSubmitMessage() throws Exception {
        givenClaimsNeedStatuses("2");
        whenRestTemplateReturn(HttpStatus.OK);
        thenMessageReturnedShouldBe("2");
    }

    @Test(expected = MessageDistributionException.class)
    public void testSubmitMessageRestTemplateThrowsRestClientException() throws Exception {
        givenClaimsNeedStatuses("1");
        whenRestTemplateThrowsException(new RestClientException("test"));
        thenSubmitMessage();
    }

    @Test(expected = MessageDistributionException.class)
    public void testSubmitMessageRestTemplateThrowsRuntimeException() throws Exception {
        givenClaimsNeedStatuses("2");
        whenRestTemplateThrowsException(new RuntimeException("test"));
        thenSubmitMessage();
    }

    @Test(expected = MessageDistributionException.class)
    public void testSubmitMessageTimeout() throws Exception {
        givenClaimsNeedStatuses("3");
        whenRestTemplateReturn(HttpStatus.REQUEST_TIMEOUT);
        thenSubmitMessage();
    }

    @Test(expected = MessageDistributionException.class)
    public void testSubmitMessageServiceUnavailable() throws Exception {
        givenClaimsNeedStatuses("1");
        whenRestTemplateReturn(HttpStatus.SERVICE_UNAVAILABLE);
        thenSubmitMessage();
    }

    @Test(expected = MessageDistributionException.class)
    public void testSubmitMessageBadRequest() throws Exception {
        givenClaimsNeedStatuses("2");
        whenRestTemplateReturn(HttpStatus.BAD_REQUEST);
        thenSubmitMessage();
    }

    @Test(expected = MessageDistributionException.class)
    public void testSubmitMessageOtherStatus() throws Exception {
        givenClaimsNeedStatuses("1");
        whenRestTemplateReturn(HttpStatus.ALREADY_REPORTED);
        thenSubmitMessage();
    }

    @Test(expected = MessageDistributionException.class)
    public void testSubmitMessageBadRequestAndSetTransactionThrowsException() throws Exception {
        givenClaimsNeedStatuses("3");
        whenRestTemplateReturn(HttpStatus.BAD_REQUEST);
        thenSubmitMessage();
    }


    private void whenRestTemplateReturn(final HttpStatus status) throws Exception {
        final ResponseEntity<List<ClaimStatus>> response = new ResponseEntity(claimStatuses, status);
        when(restTemplate.exchange(anyString(), Mockito.<HttpMethod> any(), Mockito.<HttpEntity<List<String>>> any(), eq(new ParameterizedTypeReference<List<ClaimStatus>>() {}))).thenReturn(response);
    }

    private void whenRestTemplateThrowsException(final Exception exception) throws Exception {
        when(restTemplate.exchange(anyString(), Mockito.<HttpMethod> any(), Mockito.<HttpEntity<List<String>>> any(), eq(new ParameterizedTypeReference<List<ClaimStatus>>() {}))).thenThrow(exception);
    }

    private void thenMessageReturnedShouldBe(String status) {
        List<ClaimSummary> claims = dfStatusesImpl.getDfStatuses(claimSummaries);
        for (int i = 0; i < claimSummaries.size(); i++) {
            assertThat(claims.get(i)).isEqualToComparingFieldByField(claimSummaries.get(i));
        }
    }

    private void thenSubmitMessage() {
        dfStatusesImpl.getDfStatuses(claimSummaries);
    }

    protected void givenClaimsNeedStatuses(String status) throws Exception {
        claimSummaries = new ArrayList<>();
        claimSummaries.add(new ClaimSummary("12345678901", "claim", "AB123456A", "bon1", "bieber", 0L, "1"));
        claimSummaries.add(new ClaimSummary("12345678902", "claim", "AB123456B", "bon2", "bieber", 0L, "1"));
        claimSummaries.add(new ClaimSummary("12345678903", "claim", "AB123456C", "bon3", "bieber", 0L, "1"));
        claimSummaries.add(new ClaimSummary("12345678904", "claim", "AB123456D", "bon4", "bieber", 0L, "1"));
        claimSummaries.add(new ClaimSummary("12345678905", "claim", "AB123457A", "bon5", "bieber", 0L, "1"));
        claimSummaries.add(new ClaimSummary("12345678906", "claim", "AB123457B", "bon6", "bieber", 0L, "1"));
        claimSummaries.add(new ClaimSummary("12345678907", "claim", "AB123457C", "bon7", "bieber", 0L, "1"));
        claimSummaries.add(new ClaimSummary("12345678908", "claim", "AB123457D", "bon8", "bieber", 0L, "1"));
        claimSummaries.add(new ClaimSummary("12345678909", "claim", "AB123458A", "bon9", "bieber", 0L, "1"));
        claimStatuses = new ArrayList<>();
        claimStatuses.add(new ClaimStatus("12345678901", status));
        claimStatuses.add(new ClaimStatus("12345678902", status));
        claimStatuses.add(new ClaimStatus("12345678903", status));
        claimStatuses.add(new ClaimStatus("12345678904", status));
        claimStatuses.add(new ClaimStatus("12345678905", status));
        claimStatuses.add(new ClaimStatus("12345678906", status));
        claimStatuses.add(new ClaimStatus("12345678907", status));
        claimStatuses.add(new ClaimStatus("12345678908", status));
        claimStatuses.add(new ClaimStatus("12345678909", status));
    }
}