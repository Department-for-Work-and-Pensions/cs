package gov.dwp.carers.cs.controllers;

import gov.dwp.carers.cs.helpers.TestUtils;
import gov.dwp.carers.cs.model.ClaimSummary;
import gov.dwp.carers.cs.model.TabCount;
import gov.dwp.carers.cs.service.claim.ClaimRetrievalService;
import gov.dwp.carers.helper.TestMessage;
import gov.dwp.carers.monitor.Counters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by peterwhitehead on 12/09/2016.
 */
@TestPropertySource(value = "classpath:test.application.properties")
@RunWith(MockitoJUnitRunner.class)
public class ApplicationRetrievalTest {
    private final String originTag = "GB";
    private final String date = "14092016";
    private final String dateTime = "140920160909";
    private String transactionId;
    private String msg;
    private List<ClaimSummary> claims;
    private List<ClaimSummary> retrievedClaims;

    @Mock
    private ClaimRetrievalService claimRetrievalService;

    @Mock
    private Counters counters;

    private ApplicationRetrieval applicationRetrieval;

    @Before
    public void setUp() throws Exception {
        transactionId = "1610000234";
        claims = TestUtils.createClaims("claim", dateTime);
        applicationRetrieval = new ApplicationRetrieval(claimRetrievalService, counters, "cs-count");
    }

    @Test
    public void testClaimsForDate() throws Exception {
        when(claimRetrievalService.claimsForDate(date, originTag)).thenReturn(claims);
        retrievedClaims = applicationRetrieval.claimsForDate(date, originTag);
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaim() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(claimRetrievalService.claim(transactionId, originTag)).thenReturn(msg);
        assertThat(applicationRetrieval.claim(transactionId, originTag), is(msg));
    }

    @Test
    public void testCircs() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(claimRetrievalService.claim(transactionId, originTag)).thenReturn(msg);
        assertThat(applicationRetrieval.claim(transactionId, originTag), is(msg));
    }

    @Test
    public void testClaimsForDateFiltered() throws Exception {
        when(claimRetrievalService.claimsForDateFiltered(date, "received", originTag)).thenReturn(claims);
        retrievedClaims = applicationRetrieval.claimsForDateFiltered(date, "received", originTag);
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaimsForDateFilteredBySurname() throws Exception {
        when(claimRetrievalService.claimsForDateFilteredBySurname(date, "atoz", originTag)).thenReturn(claims);
        retrievedClaims = applicationRetrieval.claimsForDateFilteredBySurname(date, "atoz", originTag);
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaimsNumbersFiltered() throws Exception {
        Map<String, Long> claimNumbersFiltered = TestUtils.getClaimNumbersFiltered();
        when(claimRetrievalService.claimsNumbersFiltered("received", originTag)).thenReturn(claimNumbersFiltered);
        Map<String, Long> data = applicationRetrieval.claimsNumbersFiltered("received", originTag);
        assertThat(data.get(date), is(1L));
    }

    @Test
    public void testCountOfClaimsForTabs() throws Exception {
        Map<String, TabCount> counts = TestUtils.getTabCounts();
        when(claimRetrievalService.countOfClaimsForTabs(date, originTag)).thenReturn(counts);
        Map<String, TabCount> data = applicationRetrieval.countOfClaimsForTabs(date, originTag);
        org.assertj.core.api.Assertions.assertThat(data.get("counts")).isEqualToComparingFieldByField(counts.get("counts"));
    }

    @Test
    public void testRender() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(claimRetrievalService.render(transactionId, originTag)).thenReturn(msg);
        assertThat(applicationRetrieval.render(transactionId, originTag), is(msg));
    }

    @Test
    public void testExport() throws Exception {
        List<List<String>> summary = TestUtils.getSummaryValues(dateTime);
        when(claimRetrievalService.export(originTag)).thenReturn(summary);
        org.assertj.core.api.Assertions.assertThat(applicationRetrieval.export(originTag)).containsOnlyElementsOf(summary);
    }

    private void thenClaimSummaryShouldBe() {
        if (claims.isEmpty() || retrievedClaims.isEmpty()) {
            fail("No claims returned");
        }
        for (int i = 0; i < claims.size(); i++) {
            org.assertj.core.api.Assertions.assertThat(claims.get(i)).isEqualToComparingFieldByField(retrievedClaims.get(i));
        }
    }

    private void givenMessageHasArrived(final String fileName, final String transactionId) throws Exception {
        this.msg = TestUtils.loadXmlFromFile(fileName);
        this.transactionId = transactionId;
    }
}