package gov.dwp.carers.cs.service.messaging;

import gov.dwp.carers.cs.MessageDistributionException;
import gov.dwp.carers.cs.helpers.TestUtils;
import gov.dwp.carers.cs.service.database.DatabaseClaimService;
import gov.dwp.carers.helper.TestMessage;
import gov.dwp.carers.monitor.Counters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by peterwhitehead on 12/09/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class DrSubmitterImplTest {
    private String msg;
    private String transactionId;

    private DrSubmitterImpl drSubmitterImpl;

    @Mock
    private Counters counters;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private DatabaseClaimService databaseClaimService;

    @Before
    public void setUp() throws Exception {
        drSubmitterImpl = new DrSubmitterImpl(restTemplate, databaseClaimService, counters, "cs-submission-error-status-", "http://localhost:9012");
    }

    @Test
    public void testSubmitMessage() throws Exception {
//        givenMessageHasArrived(TestMessage.EncryptedSensitiveDataClaim.getFileName(), TestMessage.EncryptedSensitiveDataClaim.getTransactionId());
//        whenRestTemplateReturn(HttpStatus.OK);
//        thenMessageReturnedShouldBe(true);
//        thenSetTransactionStatus(1, InspectionStatus.SUCCESSFUL_DISTRIBUTION.getStatus());
    }

//    @Test(expected = MessageDistributionException.class)
//    public void testSubmitMessageRestTemplateThrowsRestClientException() throws Exception {
//        givenMessageHasArrived(TestMessage.EncryptedSensitiveDataClaim.getFileName(), TestMessage.EncryptedSensitiveDataClaim.getTransactionId());
//        whenRestTemplateThrowsException(new RestClientException("test"));
//        thenSubmitMessage();
//        thenSetTransactionStatus(1, InspectionStatus.FAILED_DISTRIBUTION.getStatus());
//    }
//
//    @Test
//    public void testSubmitMessageRestTemplateThrowsRuntimeException() throws Exception {
//        givenMessageHasArrived(TestMessage.EncryptedSensitiveDataClaim.getFileName(), TestMessage.EncryptedSensitiveDataClaim.getTransactionId());
//        whenRestTemplateThrowsException(new RuntimeException("test"));
//        thenMessageReturnedShouldBe(false);
//        thenSetTransactionStatus(1, InspectionStatus.FAILED_DISTRIBUTION.getStatus());
//        thenStoreSuspiciousMessage(1);
//    }
//
//    @Test(expected = MessageDistributionException.class)
//    public void testSubmitMessageTimeout() throws Exception {
//        givenMessageHasArrived(TestMessage.EncryptedSensitiveDataClaim.getFileName(), TestMessage.EncryptedSensitiveDataClaim.getTransactionId());
//        whenRestTemplateReturn(HttpStatus.REQUEST_TIMEOUT);
//        thenSubmitMessage();
//        thenSetTransactionStatus(1, InspectionStatus.FAILED_DISTRIBUTION.getStatus());
//    }
//
//    @Test(expected = MessageDistributionException.class)
//    public void testSubmitMessageServiceUnavailable() throws Exception {
//        givenMessageHasArrived(TestMessage.EncryptedSensitiveDataClaim.getFileName(), TestMessage.EncryptedSensitiveDataClaim.getTransactionId());
//        whenRestTemplateReturn(HttpStatus.SERVICE_UNAVAILABLE);
//        thenSubmitMessage();
//        thenSetTransactionStatus(1, InspectionStatus.FAILED_DISTRIBUTION.getStatus());
//    }
//
//    @Test
//    public void testSubmitMessageBadRequest() throws Exception {
//        givenMessageHasArrived(TestMessage.EncryptedSensitiveDataClaim.getFileName(), TestMessage.EncryptedSensitiveDataClaim.getTransactionId());
//        whenRestTemplateReturn(HttpStatus.BAD_REQUEST);
//        thenMessageReturnedShouldBe(true);
//        thenSetTransactionStatus(1, InspectionStatus.DISTRIBUTION_REJECTED.getStatus());
//        thenStoreSuspiciousMessage(1);
//    }
//
//    @Test(expected = MessageDistributionException.class)
//    public void testSubmitMessageOtherStatus() throws Exception {
//        givenMessageHasArrived(TestMessage.EncryptedSensitiveDataClaim.getFileName(), TestMessage.EncryptedSensitiveDataClaim.getTransactionId());
//        whenRestTemplateReturn(HttpStatus.ALREADY_REPORTED);
//        thenSubmitMessage();
//        thenSetTransactionStatus(1, InspectionStatus.FAILED_DISTRIBUTION.getStatus());
//    }
//
//    @Test(expected = MessageDistributionException.class)
//    public void testSubmitMessageBadRequestAndSetTransactionThrowsException() throws Exception {
//        givenMessageHasArrived(TestMessage.EncryptedSensitiveDataClaim.getFileName(), TestMessage.EncryptedSensitiveDataClaim.getTransactionId());
//        whenRestTemplateReturn(HttpStatus.BAD_REQUEST);
//        whenSetTransactionStatusThrowsException(InspectionStatus.DISTRIBUTION_REJECTED.getStatus());
//        thenSubmitMessage();
//        thenStoreSuspiciousMessage(1);
//    }
//
//    @Test(expected = MessageDistributionException.class)
//    public void testSubmitMessageBadRequestAndStoreSuspiciousThrowsException() throws Exception {
//        givenMessageHasArrived(TestMessage.EncryptedSensitiveDataClaim.getFileName(), TestMessage.EncryptedSensitiveDataClaim.getTransactionId());
//        whenRestTemplateReturn(HttpStatus.BAD_REQUEST);
//        whenStoreSuspiciousMessageThrowsException();
//        thenSubmitMessage();
//        thenStoreSuspiciousMessage(1);
//    }
//
//    private void thenSetTransactionStatus(final int times, final Integer status) throws Exception {
//        verify(databaseClaimService, times(times)).setStatusForTransaction(transactionId, status);
//    }
//
//    private void whenSetTransactionStatusThrowsException(final Integer status) throws Exception {
//        when(databaseClaimService.setStatusForTransaction(transactionId, status)).thenThrow(new SQLException("test"));
//    }
//
//    private void thenStoreSuspiciousMessage(final int times) throws Exception {
//        verify(databaseClaimService, times(times)).storeSuspiciousMessage(msg, transactionId);
//    }
//
//    private void whenStoreSuspiciousMessageThrowsException() throws Exception {
//        when(databaseClaimService.storeSuspiciousMessage(msg, transactionId)).thenThrow(new SQLException("test"));
//    }
//
//    private void whenRestTemplateReturn(final HttpStatus status) throws Exception {
//        final ResponseEntity<String> response = new ResponseEntity("", status);
//        when(restTemplate.exchange(anyString(), Mockito.<HttpMethod> any(), Mockito.<HttpEntity<String>> any(), Mockito.<Class<String>> any())).thenReturn(response);
//    }
//
//    private void whenRestTemplateThrowsException(final Exception exception) throws Exception {
//        when(restTemplate.exchange(anyString(), Mockito.<HttpMethod> any(), Mockito.<HttpEntity<String>> any(), Mockito.<Class<String>> any())).thenThrow(exception);
//    }
//
//    private void thenMessageReturnedShouldBe(final Boolean result) {
//        assertThat(drSubmitterImpl.drSubmit(msg, transactionId), is(result));
//    }
//
//    private void thenSubmitMessage() {
//        drSubmitterImpl.drSubmit(msg, transactionId);
//    }
//
//    protected void givenMessageHasArrived(final String fileName, final String transactionId) throws Exception {
//        this.msg = TestUtils.loadXmlFromFile(fileName);
//        this.transactionId = transactionId;
//    }
}