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
    private String originTag = "GB";
    private String status = "received";
    private String drsStatus = "1";
    @Test
    public void testClaimsForDate() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(originTag, status, null, "claim");
        final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
        whenDfServerReturns("[{\"id\":\"" + transactionId + "\",\"desc\":\"" + drsStatus + "\"}]", mediaType);
        final String result = getMessage("/claims/" + date + "/" + originTag);
        thenMessageSent();
        thenResultShouldBe(result, "[{\"transactionId\":\"16070000241\",\"claimType\":\"claim\",\"nino\":\"AB123456B\",\"forename\":\"fred\",\"surname\":\"bieber\",\"claimDateTime\":" + TestUtils.getSimpleDateFormat().parse(dateTime).getTime() + ",\"status\":\"" + status + "\",\"drsStatus\":\"" + drsStatus + "\"}]");
    }

    @Test
    public void testClaim() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(originTag, status, null, "claim");
        final String result = getMessage("/claim/" + transactionId + "/" + originTag);
        thenResultShouldBe(result, msg);
        thenMessageStoredInDatabaseWithStatus(TestMessage.ValidXMLWithRSASignature.getTransactionId(), "viewed");
    }

    @Test
    public void testCircs() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(originTag, status, null, "circs");
        final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
        whenDfServerReturns("[{\"id\":\"" + transactionId + "\",\"desc\":\"" + drsStatus + "\"}]", mediaType);
        final String result = getMessage("/circs/" + date + "/" + originTag);
        thenMessageSent();
        thenResultShouldBe(result, "[{\"transactionId\":\"16070000241\",\"claimType\":\"circs\",\"nino\":\"AB123456B\",\"forename\":\"fred\",\"surname\":\"bieber\",\"claimDateTime\":" + TestUtils.getSimpleDateFormat().parse(dateTime).getTime() + ",\"status\":\"" + status + "\",\"drsStatus\":\"" + drsStatus + "\"}]");
    }

    @Test
    public void testClaimsForDateFiltered() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(originTag, status, null, "claim");
        final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
        whenDfServerReturns("[{\"id\":\"" + transactionId + "\",\"desc\":\"" + drsStatus + "\"}]", mediaType);
        final String result = getMessage("/claims/" + date + "/" + status + "/" + originTag);
        thenMessageSent();
        thenResultShouldBe(result, "[{\"transactionId\":\"16070000241\",\"claimType\":\"claim\",\"nino\":\"AB123456B\",\"forename\":\"fred\",\"surname\":\"bieber\",\"claimDateTime\":" + TestUtils.getSimpleDateFormat().parse(dateTime).getTime() + ",\"status\":\"received\",\"drsStatus\":\"" + drsStatus + "\"}]");
    }

    @Test
    public void testClaimsForDateFilteredBySurname() throws Exception {
        final String sortBy = "atoz";
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(originTag, status, null, "claim");
        final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
        whenDfServerReturns("[{\"id\":\"" + transactionId + "\",\"desc\":\"" + drsStatus + "\"}]", mediaType);
        final String result = getMessage("/claims/surname/" + date + "/" + sortBy + "/" + originTag);
        thenResultShouldBe(result, "[{\"transactionId\":\"16070000241\",\"claimType\":\"claim\",\"nino\":\"AB123456B\",\"forename\":\"fred\",\"surname\":\"bieber\",\"claimDateTime\":" + TestUtils.getSimpleDateFormat().parse(dateTime).getTime() + ",\"status\":\"received\",\"drsStatus\":\"" + drsStatus + "\"}]");
    }

    @Test
    public void testClaimsNumbersFiltered() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(originTag, status, null, "claim");
        final String result = getMessage("/counts/" + status + ",viewed/" + originTag);
        assertThat(result, containsString("\"" + date + "\":1"));
    }

    @Test
    public void testCountOfClaimsForTabs() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(originTag, status, null, "claim");
        final String result = getMessage("/countOfClaimsForTabs/" + date + "/" + originTag);
        thenResultShouldBe(result, "{\"counts\":{\"atom\":1,\"ntoz\":0,\"circs\":0}}");
    }

    @Test
    public void testRender() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(originTag, status, null, "claim");
        final String result = getMessage("/render/" + transactionId + "/" + originTag);
        thenResultShouldBe(result, msg);
        thenMessageStoredInDatabaseWithStatus(TestMessage.ValidXMLWithRSASignature.getTransactionId(), "viewed");
    }

    @Test
    public void testExport() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(originTag, "completed", "010820160909", "claim");
        final String result = getMessage("/export/" + originTag);
        thenResultShouldBe(result, "[[\"NINO\",\"Claim Date Time\",\"Claim Type\",\"Surname\",\"sortby\",\"Status\",\"Forename\"],[\"AB123456B\",\"010820160909\",\"claim\",\"bieber\",\"b\",\"completed\",\"fred\"]]");
    }

    @Test
    public void testClaimUpdate() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(originTag, status, null, "claim");
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

    private void testSubmitClaim(String url) throws Exception {
        whenDrServerReturns(HttpStatus.OK, 1);
        final String result = postMessage(url, TestMessage.ValidXMLWithRSASignature.getFileName());
        thenResultShouldBe(result, "Success");
        thenMessageSent();
        thenMessageStoredInDatabaseWithStatus(TestMessage.ValidXMLWithRSASignature.getTransactionId(), "received");
    }

    @Test
    public void testPurge() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        insertClaimSummary(originTag, "completed", "010820160909", "claim");
        final String result = postMessage("/purge/" + originTag);
        thenResultShouldBe(result, "Success");
    }
}