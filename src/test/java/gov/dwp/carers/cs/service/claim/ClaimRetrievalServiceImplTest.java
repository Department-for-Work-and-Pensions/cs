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
    private final String dateTime = "140920160909";
    private final String date = "14092016";
    private final String originTag = "GB";
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
        claims = TestUtils.createClaims("claim", dateTime);
        claimRetrievalServiceImpl = new ClaimRetrievalServiceImpl(databaseClaimService, dfStatuses);
    }

    @Test
    public void testClaimsForDate() throws Exception {
        when(dfStatuses.getDfStatuses(claims)).thenReturn(claims);
            when(databaseClaimService.claims(originTag, date)).thenReturn(claims);
        retrievedClaims = claimRetrievalServiceImpl.claimsForDate(date, originTag);
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaim() throws Exception {
        when(databaseClaimService.fullClaim(transactionId, originTag)).thenReturn("<xml>test</xml>");
        assertThat(claimRetrievalServiceImpl.claim(transactionId, originTag), is("<xml>test</xml>"));
    }

    @Test
    public void testCircs() throws Exception {
        claims = TestUtils.createClaims("circs", dateTime);
        when(dfStatuses.getDfStatuses(claims)).thenReturn(claims);
        when(databaseClaimService.circs(originTag, date)).thenReturn(claims);
        retrievedClaims = claimRetrievalServiceImpl.circs(date, originTag);
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaimsForDateFiltered() throws Exception {
        when(dfStatuses.getDfStatuses(claims)).thenReturn(claims);
        when(databaseClaimService.claimsFiltered(originTag, date, "received")).thenReturn(claims);
        retrievedClaims = claimRetrievalServiceImpl.claimsForDateFiltered(date, "received", originTag);
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaimsForDateFilteredBySurname() throws Exception {
        when(dfStatuses.getDfStatuses(claims)).thenReturn(claims);
        when(databaseClaimService.claimsFilteredBySurname(originTag, date, "atoz")).thenReturn(claims);
        retrievedClaims = claimRetrievalServiceImpl.claimsForDateFilteredBySurname(date, "atoz", originTag);
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaimsNumbersFiltered() throws Exception {
        Map<String, Long> claimNumbersFiltered = TestUtils.getClaimNumbersFiltered();
        when(databaseClaimService.claimNumbersFiltered(originTag, Arrays.asList("received"))).thenReturn(claimNumbersFiltered);
        Map<String, Long> data = claimRetrievalServiceImpl.claimsNumbersFiltered("received", originTag);
        assertThat(data.get(date), is(1L));
    }

    @Test
    public void testCountOfClaimsForTabs() throws Exception {
        Map<String, TabCount> counts = TestUtils.getTabCounts();
        when(databaseClaimService.constructClaimSummaryWithTabTotals(originTag, date)).thenReturn(counts);
        Map<String, TabCount> data = claimRetrievalServiceImpl.countOfClaimsForTabs(date, originTag);
        org.assertj.core.api.Assertions.assertThat(data.get("counts")).isEqualToComparingFieldByField(counts.get("counts"));
    }

    @Test
    public void testRender() throws Exception {
        when(databaseClaimService.fullClaim(transactionId, originTag)).thenReturn("<xml>test</xml>");
        String xml = claimRetrievalServiceImpl.render(transactionId, originTag);
        assertThat(xml, is("<xml>test</xml>"));
    }

    @Test
    public void testExport() throws Exception {
        List<List<String>> summary = TestUtils.getSummaryValues(dateTime);
        when(databaseClaimService.export(originTag)).thenReturn(summary);
        org.assertj.core.api.Assertions.assertThat(claimRetrievalServiceImpl.export(originTag)).containsOnlyElementsOf(summary);
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