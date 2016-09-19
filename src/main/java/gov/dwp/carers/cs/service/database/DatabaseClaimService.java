package gov.dwp.carers.cs.service.database;

import gov.dwp.carers.cs.model.ClaimSummary;
import gov.dwp.carers.cs.model.ClaimSummaryCount;
import gov.dwp.carers.cs.model.TabCount;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DatabaseClaimService {
    List<ClaimSummary> claims(final String originTag, final String dateString);
    List<ClaimSummary> circs(final String originTag, final String dateString);
    List<ClaimSummary> claimsFilteredBySurname(final String originTag, final String dateString, final String sortBy);
    List<ClaimSummary> claimsFiltered(final String originTag, final String dateString, final String status);
    String fullClaim(final String transactionId, final String originTag);
    Boolean updateClaim(final String transactionId, final String status);
    Map<String, Long> claimNumbersFiltered(final String originTag, final List<String> status);
    Boolean submitMessage(final String message, final Boolean forceToday, final String originTag, final String transactionId);
    List<List<String>> export(final String originTag);
    Boolean purge(final String originTag);
    Map<String, TabCount> constructClaimSummaryWithTabTotals(final String originTag, final String dateString);
    Boolean updateStatus(final String transactionId, final Integer status) throws SQLException;
    Boolean health() throws SQLException;
}