package gov.dwp.carers.cs.helpers;

import gov.dwp.carers.cs.model.ClaimSummary;
import gov.dwp.carers.cs.model.TabCount;
import gov.dwp.carers.helper.Utils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by peterwhitehead on 16/06/2016.
 */
public class TestUtils extends Utils {
    private static final transient SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmm");

    public static void insertClaim(final String transactionId,
                                     final String xml, final Integer drsStatus,
                                     final String originTag, final JdbcTemplate jdbcTemplate) {
        final String insertSql = "INSERT INTO carers.claim(transid, claimxml, drs_status, origintag) VALUES (?,?,?,?)";
        final Object[] args = new Object[] { transactionId, xml, drsStatus, originTag };
        jdbcTemplate.update(insertSql, args);
    }

    public static void insertClaimSummary(final String transactionId,
                                     final String key, final String value,
                                     final String originTag, final JdbcTemplate jdbcTemplate) {
        final String insertSql = "INSERT INTO carers.claimsummary(transid, key, value, origintag) VALUES (?,?,?,?)";
        final Object[] args = new Object[] { transactionId, key, value, originTag };
        jdbcTemplate.update(insertSql, args);
    }

    public static void insertClaimAudit(final String transactionId, final Integer status, final JdbcTemplate jdbcTemplate) {
        final Object[] args = new Object[] { transactionId, status };
        final String sqlInsert = "INSERT INTO carers.claimaudit(transId,status) VALUES(?,?)";
        jdbcTemplate.update(sqlInsert, args);
    }

    public static Boolean transactionIdExistsInClaim(final String transactionId, final JdbcTemplate jdbcTemplate) {
        final String selectSql = "SELECT transid FROM carers.claim WHERE transid = ?";
        return jdbcTemplate.query(selectSql, resultSet -> resultSet.next() ? true : false, transactionId);
    }

    public static Boolean transactionIdExistsInClaimSummary(final String transactionId, final JdbcTemplate jdbcTemplate) {
        final String selectSql = "SELECT transid FROM carers.claimsummary WHERE transid = ?";
        return jdbcTemplate.query(selectSql, resultSet -> resultSet.next() ? true : false, transactionId);
    }

    public static String getKeyValue(final String transactionId, final String key, final JdbcTemplate jdbcTemplate) {
        final String selectSql = "SELECT value FROM carers.claimsummary WHERE transid = ? AND key = ?";
        return jdbcTemplate.query(selectSql, resultSet -> resultSet.next() ? resultSet.getString(1) : null, transactionId, key);
    }

    public static String checkStatusHistory(final String transactionId, final String key, final JdbcTemplate jdbcTemplate) {
        final String selectSql = "SELECT old_status FROM carers.claim_status_history WHERE transid = ? AND new_status = ?";
        return jdbcTemplate.query(selectSql, resultSet -> resultSet.next() ? resultSet.getString(1) : "No record", transactionId, key);
    }

    public static List<ClaimSummary> createClaims(String claimType, String dateTime) throws Exception {
        List<ClaimSummary> claims = new ArrayList<>();
        claims.add(new ClaimSummary("1610000234", claimType, "AB123456B", "fred", "bieber", simpleDateFormat.parse(dateTime).getTime(), "received"));
        claims.add(new ClaimSummary("1610000235", claimType, "AB123457B", "fred1", "bieber1", simpleDateFormat.parse(dateTime).getTime(), "received"));
        claims.add(new ClaimSummary("1610000236", claimType, "AB123458B", "fred2", "bieber2", simpleDateFormat.parse(dateTime).getTime(), "received"));
        return claims;
    }

    public static Map<String, Long> getClaimNumbersFiltered() {
        Map<String, Long> claimNumbersFiltered = new HashMap<>();
        claimNumbersFiltered.put("14092016", 1L);
        return claimNumbersFiltered;
    }

    public static Map<String, TabCount> getTabCounts() {
        Map<String, TabCount> counts = new HashMap<>();
        counts.put("counts", new TabCount(1L, 2L, 3L));
        return counts;
    }

    public static List<List<String>> getSummaryValues(String dateTime) {
        List<List<String>> summary = new ArrayList<>();
        List<String> header = Arrays.asList("NINO", "Claim Date Time", "Claim Type", "Surname", "sortby", "Status", "Forename");
        List<String> data = Arrays.asList("AB123456B", dateTime, "claim", "bieber", "b", "received", "fred");
        summary.add(header);
        summary.add(data);
        return summary;
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }
}
