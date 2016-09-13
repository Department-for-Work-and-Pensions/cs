package gov.dwp.carers.cs.helpers;

import gov.dwp.carers.helper.Utils;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by peterwhitehead on 16/06/2016.
 */
public class TestUtils extends Utils {
    public static void insertClaim(final String transactionId,
                                     final String xml, final String drsStatus,
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

    public static Boolean transactionIdExistsInClaim(final String transactionId, final JdbcTemplate jdbcTemplate) {
        final String selectSql = "SELECT transid FROM carers.claim WHERE transid = ?";
        return jdbcTemplate.query(selectSql, resultSet -> resultSet.next() ? true : false, transactionId);
    }

    public static Boolean transactionIdExistsInClaimSummary(final String transactionId, final JdbcTemplate jdbcTemplate) {
        final String selectSql = "SELECT transid FROM carers.claimsummary WHERE transid = ?";
        return jdbcTemplate.query(selectSql, resultSet -> resultSet.next() ? true : false, transactionId);
    }
}
