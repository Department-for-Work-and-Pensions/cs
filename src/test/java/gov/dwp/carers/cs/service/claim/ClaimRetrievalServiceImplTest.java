package gov.dwp.carers.cs.service.claim;

import gov.dwp.carers.cs.helpers.TestUtils;
import gov.dwp.carers.cs.model.ClaimSummary;
import gov.dwp.carers.cs.model.TabCount;
import gov.dwp.carers.cs.service.database.DatabaseClaimService;
import gov.dwp.carers.cs.service.messaging.DfStatuses;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by peterwhitehead on 12/09/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClaimRetrievalServiceImplTest {
    private ClaimRetrievalServiceImpl claimRetrievalServiceImpl;
    private static final String DATETIME = "140920160909";
    private static final String DATE = "14092016";
    private static final String ORIGIN_TAG = "GB";
    private String transactionId;
    private List<ClaimSummary> claims;
    private List<ClaimSummary> retrievedClaims;

    @Mock
    private DatabaseClaimService databaseClaimService;

    @Mock
    private DfStatuses dfStatuses;

    @Before
    public void setUp() throws Exception {
        transactionId = "1610000234";
        claims = TestUtils.createClaims("claim", DATETIME);
        claimRetrievalServiceImpl = new ClaimRetrievalServiceImpl(databaseClaimService, dfStatuses);
    }

    @Test
    public void testClaimsForDate() throws Exception {
        when(dfStatuses.getDfStatuses(claims)).thenReturn(claims);
            when(databaseClaimService.claims(ORIGIN_TAG, DATE)).thenReturn(claims);
        retrievedClaims = claimRetrievalServiceImpl.claimsForDate(DATE, ORIGIN_TAG);
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaim() throws Exception {
        when(databaseClaimService.fullClaim(transactionId, ORIGIN_TAG)).thenReturn("<xml>test</xml>");
        assertThat(claimRetrievalServiceImpl.claim(transactionId, ORIGIN_TAG), is("<xml>test</xml>"));
    }

    @Test
    public void testCircs() throws Exception {
        claims = TestUtils.createClaims("circs", DATETIME);
        when(dfStatuses.getDfStatuses(claims)).thenReturn(claims);
        when(databaseClaimService.circs(ORIGIN_TAG, DATE)).thenReturn(claims);
        retrievedClaims = claimRetrievalServiceImpl.circs(DATE, ORIGIN_TAG);
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaimsForDateFiltered() throws Exception {
        when(dfStatuses.getDfStatuses(claims)).thenReturn(claims);
        when(databaseClaimService.claimsFiltered(ORIGIN_TAG, DATE, "received")).thenReturn(claims);
        retrievedClaims = claimRetrievalServiceImpl.claimsForDateFiltered(DATE, "received", ORIGIN_TAG);
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaimsForDateFilteredBySurname() throws Exception {
        when(dfStatuses.getDfStatuses(claims)).thenReturn(claims);
        when(databaseClaimService.claimsFilteredBySurname(ORIGIN_TAG, DATE, "atoz")).thenReturn(claims);
        retrievedClaims = claimRetrievalServiceImpl.claimsForDateFilteredBySurname(DATE, "atoz", ORIGIN_TAG);
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaimsNumbersFiltered() throws Exception {
        final Map<String, Long> claimNumbersFiltered = TestUtils.getClaimNumbersFiltered();
        when(databaseClaimService.claimNumbersFiltered(ORIGIN_TAG, Arrays.asList("received"))).thenReturn(claimNumbersFiltered);
        final Map<String, Long> data = claimRetrievalServiceImpl.claimsNumbersFiltered("received", ORIGIN_TAG);
        assertThat(data.get(DATE), is(1L));
    }

    @Test
    public void testCountOfClaimsForTabs() throws Exception {
        final Map<String, TabCount> counts = TestUtils.getTabCounts();
        when(databaseClaimService.constructClaimSummaryWithTabTotals(ORIGIN_TAG, DATE)).thenReturn(counts);
        final Map<String, TabCount> data = claimRetrievalServiceImpl.countOfClaimsForTabs(DATE, ORIGIN_TAG);
        org.assertj.core.api.Assertions.assertThat(data.get("counts")).isEqualToComparingFieldByField(counts.get("counts"));
    }

    @Test
    public void testRender() throws Exception {
        when(databaseClaimService.fullClaim(transactionId, ORIGIN_TAG)).thenReturn("<xml>test</xml>");
        final String xml = claimRetrievalServiceImpl.render(transactionId, ORIGIN_TAG);
        assertThat(xml, is("<xml>test</xml>"));
    }

    @Test
    public void testExport() throws Exception {
        final List<List<String>> summary = TestUtils.getSummaryValues(DATETIME);
        when(databaseClaimService.export(ORIGIN_TAG)).thenReturn(summary);
        org.assertj.core.api.Assertions.assertThat(claimRetrievalServiceImpl.export(ORIGIN_TAG)).containsOnlyElementsOf(summary);
    }

    private void thenClaimSummaryShouldBe() {
        if (claims.isEmpty() || retrievedClaims.isEmpty()) {
            fail("No claims returned");
        }
        for (int i = 0; i < claims.size(); i++) {
            org.assertj.core.api.Assertions.assertThat(claims.get(i)).isEqualToComparingFieldByField(retrievedClaims.get(i));
        }
    }
}