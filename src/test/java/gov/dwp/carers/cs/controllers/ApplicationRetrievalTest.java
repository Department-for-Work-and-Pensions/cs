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
    private static final String ORIGIN_TAG = "GB";
    private static final String DATE = "14092016";
    private static final String DATETIME = "140920160909";
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
        claims = TestUtils.createClaims("claim", DATETIME);
        applicationRetrieval = new ApplicationRetrieval(claimRetrievalService, counters, "cs-count");
    }

    @Test
    public void testClaimsForDate() throws Exception {
        when(claimRetrievalService.claimsForDate(DATE, ORIGIN_TAG)).thenReturn(claims);
        retrievedClaims = applicationRetrieval.claimsForDate(DATE, ORIGIN_TAG);
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaim() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(claimRetrievalService.claim(transactionId, ORIGIN_TAG)).thenReturn(msg);
        assertThat(applicationRetrieval.claim(transactionId, ORIGIN_TAG), is(msg));
    }

    @Test
    public void testCircs() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(claimRetrievalService.claim(transactionId, ORIGIN_TAG)).thenReturn(msg);
        assertThat(applicationRetrieval.claim(transactionId, ORIGIN_TAG), is(msg));
    }

    @Test
    public void testClaimsForDateFiltered() throws Exception {
        when(claimRetrievalService.claimsForDateFiltered(DATE, "received", ORIGIN_TAG)).thenReturn(claims);
        retrievedClaims = applicationRetrieval.claimsForDateFiltered(DATE, "received", ORIGIN_TAG);
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaimsForDateFilteredBySurname() throws Exception {
        when(claimRetrievalService.claimsForDateFilteredBySurname(DATE, "atoz", ORIGIN_TAG)).thenReturn(claims);
        retrievedClaims = applicationRetrieval.claimsForDateFilteredBySurname(DATE, "atoz", ORIGIN_TAG);
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaimsNumbersFiltered() throws Exception {
        final Map<String, Long> claimNumbersFiltered = TestUtils.getClaimNumbersFiltered();
        when(claimRetrievalService.claimsNumbersFiltered("received", ORIGIN_TAG)).thenReturn(claimNumbersFiltered);
        final Map<String, Long> data = applicationRetrieval.claimsNumbersFiltered("received", ORIGIN_TAG);
        assertThat(data.get(DATE), is(1L));
    }

    @Test
    public void testCountOfClaimsForTabs() throws Exception {
        final Map<String, TabCount> counts = TestUtils.getTabCounts();
        when(claimRetrievalService.countOfClaimsForTabs(DATE, ORIGIN_TAG)).thenReturn(counts);
        final Map<String, TabCount> data = applicationRetrieval.countOfClaimsForTabs(DATE, ORIGIN_TAG);
        org.assertj.core.api.Assertions.assertThat(data.get("counts")).isEqualToComparingFieldByField(counts.get("counts"));
    }

    @Test
    public void testRender() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(claimRetrievalService.render(transactionId, ORIGIN_TAG)).thenReturn(msg);
        assertThat(applicationRetrieval.render(transactionId, ORIGIN_TAG), is(msg));
    }

    @Test
    public void testExport() throws Exception {
        final List<List<String>> summary = TestUtils.getSummaryValues(DATETIME);
        when(claimRetrievalService.export(ORIGIN_TAG)).thenReturn(summary);
        org.assertj.core.api.Assertions.assertThat(applicationRetrieval.export(ORIGIN_TAG)).containsOnlyElementsOf(summary);
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