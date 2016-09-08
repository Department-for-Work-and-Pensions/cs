package gov.dwp.carers.cs.service.claim;

import gov.dwp.carers.cs.model.ClaimSummary;
import gov.dwp.carers.cs.model.ClaimSummaryCount;
import gov.dwp.carers.cs.model.TabCount;

import java.util.List;
import java.util.Map;

/**
 * Created by peterwhitehead on 26/08/2016.
 */
public interface ClaimRetrievalService {
    List<ClaimSummary> claimsForDate(final String currentDate, final String originTag);
    String claim(final String transactionId, final String originTag);
    List<ClaimSummary> circs(final String currentDate, final String originTag);
    List<ClaimSummary> claimsForDateFiltered(final String currentDate, final String status, final String originTag);
    List<ClaimSummary> claimsForDateFilteredBySurname(final String currentDate, final String sortBy, final String originTag);
    Map<String, Long> claimsNumbersFiltered(final String statuses, final String originTag);
    Map<String, TabCount> countOfClaimsForTabs(final String currentDate, final String originTag);
    String render(final String transactionId, final String originTag);
    List<List<String>> export(final String originTag);
}
