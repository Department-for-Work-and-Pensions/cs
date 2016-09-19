package gov.dwp.carers.cs.controllers;

import gov.dwp.carers.cs.helpers.TestUtils;
import gov.dwp.carers.helper.TestMessage;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

/**
 * Created by peterwhitehead on 12/09/2016.
 */
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CsApplicationIntegrationTest extends AbstractCsApplicationIntegrationTest {
    private static final String ORIGIN_TAG = "GB";
    private static final String STATUS = "received";
    private static final String DRS_STATUS = "1";
    @Test
    public void testClaimsForDate() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(ORIGIN_TAG, STATUS, null, "claim");
        final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
        whenDfServerReturns("[{\"id\":\"" + transactionId + "\",\"desc\":\"" + DRS_STATUS + "\"}]", mediaType);
        final String result = getMessage("/claims/" + date + "/" + ORIGIN_TAG);
        thenMessageSent();
        thenResultShouldBe(result, "[{\"transactionId\":\"16070000241\",\"claimType\":\"claim\",\"nino\":\"AB123456B\",\"forename\":\"fred\",\"surname\":\"bieber\",\"claimDateTime\":" + TestUtils.getSimpleDateFormat().parse(dateTime).getTime() + ",\"status\":\"" + STATUS + "\",\"drsStatus\":\"" + DRS_STATUS + "\"}]");
    }

    @Test
    public void testClaim() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(ORIGIN_TAG, STATUS, null, "claim");
        final String result = getMessage("/claim/" + transactionId + "/" + ORIGIN_TAG);
        thenResultShouldBe(result, msg);
        thenMessageStoredInDatabaseWithStatus(TestMessage.ValidXMLWithRSASignature.getTransactionId(), "viewed");
    }

    @Test
    public void testCircs() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(ORIGIN_TAG, STATUS, null, "circs");
        final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
        whenDfServerReturns("[{\"id\":\"" + transactionId + "\",\"desc\":\"" + DRS_STATUS + "\"}]", mediaType);
        final String result = getMessage("/circs/" + date + "/" + ORIGIN_TAG);
        thenMessageSent();
        thenResultShouldBe(result, "[{\"transactionId\":\"16070000241\",\"claimType\":\"circs\",\"nino\":\"AB123456B\",\"forename\":\"fred\",\"surname\":\"bieber\",\"claimDateTime\":" + TestUtils.getSimpleDateFormat().parse(dateTime).getTime() + ",\"status\":\"" + STATUS + "\",\"drsStatus\":\"" + DRS_STATUS + "\"}]");
    }

    @Test
    public void testClaimsForDateFiltered() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(ORIGIN_TAG, STATUS, null, "claim");
        final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
        whenDfServerReturns("[{\"id\":\"" + transactionId + "\",\"desc\":\"" + DRS_STATUS + "\"}]", mediaType);
        final String result = getMessage("/claims/" + date + "/" + STATUS + "/" + ORIGIN_TAG);
        thenMessageSent();
        thenResultShouldBe(result, "[{\"transactionId\":\"16070000241\",\"claimType\":\"claim\",\"nino\":\"AB123456B\",\"forename\":\"fred\",\"surname\":\"bieber\",\"claimDateTime\":" + TestUtils.getSimpleDateFormat().parse(dateTime).getTime() + ",\"status\":\"received\",\"drsStatus\":\"" + DRS_STATUS + "\"}]");
    }

    @Test
    public void testClaimsForDateFilteredBySurname() throws Exception {
        final String sortBy = "atoz";
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(ORIGIN_TAG, STATUS, null, "claim");
        final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
        whenDfServerReturns("[{\"id\":\"" + transactionId + "\",\"desc\":\"" + DRS_STATUS + "\"}]", mediaType);
        final String result = getMessage("/claims/surname/" + date + "/" + sortBy + "/" + ORIGIN_TAG);
        thenResultShouldBe(result, "[{\"transactionId\":\"16070000241\",\"claimType\":\"claim\",\"nino\":\"AB123456B\",\"forename\":\"fred\",\"surname\":\"bieber\",\"claimDateTime\":" + TestUtils.getSimpleDateFormat().parse(dateTime).getTime() + ",\"status\":\"received\",\"drsStatus\":\"" + DRS_STATUS + "\"}]");
    }

    @Test
    public void testClaimsNumbersFiltered() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(ORIGIN_TAG, STATUS, null, "claim");
        final String result = getMessage("/counts/" + STATUS + ",viewed/" + ORIGIN_TAG);
        assertThat(result, containsString("\"" + date + "\":1"));
    }

    @Test
    public void testCountOfClaimsForTabs() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(ORIGIN_TAG, STATUS, null, "claim");
        final String result = getMessage("/countOfClaimsForTabs/" + date + "/" + ORIGIN_TAG);
        thenResultShouldBe(result, "{\"counts\":{\"atom\":1,\"ntoz\":0,\"circs\":0}}");
    }

    @Test
    public void testRender() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(ORIGIN_TAG, STATUS, null, "claim");
        final String result = getMessage("/render/" + transactionId + "/" + ORIGIN_TAG);
        thenResultShouldBe(result, msg);
        thenMessageStoredInDatabaseWithStatus(TestMessage.ValidXMLWithRSASignature.getTransactionId(), "viewed");
    }

    @Test
    public void testExport() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(ORIGIN_TAG, "completed", "010820160909", "claim");
        final String result = getMessage("/export/" + ORIGIN_TAG);
        thenResultShouldBe(result, "[[\"NINO\",\"Claim Date Time\",\"Claim Type\",\"Surname\",\"sortby\",\"Status\",\"Forename\"],[\"AB123456B\",\"010820160909\",\"claim\",\"bieber\",\"b\",\"completed\",\"fred\"]]");
    }

    @Test
    public void testClaimUpdate() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(ORIGIN_TAG, STATUS, null, "claim");
        final String result = putMessage("/claim/" + transactionId + "/viewed");
        thenResultShouldBe(result, "Success");
    }

    @Test
    public void testSubmitClaim() throws Exception {
        testSubmitClaim("/claim/submit-force-today");
    }

    @Test
    public void testSubmitClaimForceToday() throws Exception {
        testSubmitClaim("/claim/submit");
    }

    private void testSubmitClaim(final String url) throws Exception {
        whenDrServerReturns(HttpStatus.OK, 1);
        final String result = postMessage(url, TestMessage.ValidXMLWithRSASignature.getFileName());
        thenResultShouldBe(result, "Success");
        thenMessageSent();
        thenMessageStoredInDatabaseWithStatus(TestMessage.ValidXMLWithRSASignature.getTransactionId(), "received");
    }

    @Test
    public void testPurge() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(ORIGIN_TAG, "completed", "010820160909", "claim");
        final String result = postMessage("/purge/" + ORIGIN_TAG);
        thenResultShouldBe(result, "Success");
    }
}