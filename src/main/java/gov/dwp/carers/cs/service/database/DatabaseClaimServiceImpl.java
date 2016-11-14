package gov.dwp.carers.cs.service.database;

import gov.dwp.carers.cs.helpers.ClaimServiceHelper;
import gov.dwp.carers.cs.model.ClaimSummary;
import gov.dwp.carers.cs.model.ClaimSummaryCount;
import gov.dwp.carers.cs.model.TabCount;
import gov.dwp.carers.monitor.Counters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class DatabaseClaimServiceImpl implements DatabaseClaimService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseClaimServiceImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private final ClaimServiceHelper claimServiceHelper;
    private final Counters counters;
    private final String claimMetric;
    private final String claimSummaryMetric;

    private final String SACONSTANT_CIRCS="circs";
    private final String SACONSTANT_CLAIM="claim";

    @Inject
    public DatabaseClaimServiceImpl(final JdbcTemplate jdbcTemplate,
                                    final PlatformTransactionManager transactionManager,
                                    final ClaimServiceHelper claimServiceHelper,
                                    final Counters counters,
                                    @Value("${cs.db.claim.count}") final String claimMetric,
                                    @Value("${cs.db.claim.summary.count}") final String claimSummaryMetric) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.claimServiceHelper = claimServiceHelper;
        this.counters = counters;
        this.claimMetric = claimMetric;
        this.claimSummaryMetric = claimSummaryMetric;
    }

    @Override
    public Boolean health() throws SQLException {
        return jdbcTemplate.query("SELECT 1", resultSet -> resultSet.next() ? true : false);
    }

    @Override
    public List<ClaimSummary> claims(final String originTag, final String dateString) {
        //1) CLAIM_DATETIME_KEY 2) originTag 3) date 4) STATUS_KEY 5) originTag
        final String sql = "SELECT * FROM crosstab(" +
                "'SELECT cs.transId as transactionId, cs.key as key, cs.value as value " +
                "FROM carers.claimsummary cs " +
                "WHERE cs.transId IN (" +
                "SELECT cssq.transId as transactionId " +
                "FROM carers.claimsummary cssq " +
                "WHERE cssq.key = ''" + ClaimServiceHelper.CLAIM_DATETIME_KEY + "'' " +
                "AND cssq.origintag = ''" + originTag + "'' " +
                "AND left(cssq.value,8) = ''" + dateString + "'' " +
                "INTERSECT SELECT cssq.transId as transactionId " +
                "FROM carers.claimsummary cssq " +
                "WHERE cssq.key = ''" + ClaimServiceHelper.STATUS_KEY + "'' " +
                "AND cssq.origintag = ''" + originTag + "'' " +
                "AND cssq.value != ''completed'') " +
                "ORDER BY transactionId ASC'," +
                "'SELECT DISTINCT key from carers.claimsummary ORDER BY 1'" +
                ") AS " +
                "( " +
                "transactionId varchar, claimDateTime varchar, claimType varchar, forename varchar, " +
                "nino varchar, sortby varchar, status varchar, surname varchar " +
                ")";
        final List<ClaimSummary> claimSummaries = jdbcTemplate.query(sql, new ClaimSummaryRowMapper());
        return claimSummaries;
    }

    @Override
    public List<ClaimSummary> circs(final String originTag, final String dateString) {
        //1) CLAIM_DATETIME_KEY 2) originTag 3) date 4) STATUS_KEY 5) originTag 6) originTag
        final String sql = "SELECT * FROM crosstab(" +
                "'SELECT cs.transId as transactionId, cs.key as key, cs.value as value " +
                "FROM carers.claimsummary cs " +
                "WHERE cs.transId IN (" +
                "SELECT cssq.transId as transactionId " +
                "FROM carers.claimsummary cssq " +
                "WHERE cssq.key = ''" + ClaimServiceHelper.CLAIM_DATETIME_KEY + "'' " +
                "AND cssq.origintag = ''" + originTag + "'' " +
                "AND left(cssq.value,8) = ''" + dateString + "'' " +
                "INTERSECT SELECT cssq.transId as transactionId " +
                "FROM carers.claimsummary cssq " +
                "WHERE cssq.key = ''" + ClaimServiceHelper.STATUS_KEY + "'' " +
                "AND cssq.origintag = ''" + originTag + "'' " +
                "AND cssq.value != ''completed'' " +
                "INTERSECT SELECT c.transId as transactionId " +
                "FROM carers.claimsummary c " +
                "WHERE c.key=''claimType'' " +
                "AND c.value=''circs'' " +
                "AND c.origintag = ''" + originTag + "'') " +
                "ORDER BY transactionId ASC'," +
                "'SELECT DISTINCT key from carers.claimsummary ORDER BY 1'" +
                ") AS " +
                "( " +
                "transactionId varchar, claimDateTime varchar, claimType varchar, forename varchar, " +
                "nino varchar, sortby varchar, status varchar, surname varchar " +
                ")";
        final List<ClaimSummary> claimSummaries = jdbcTemplate.query(sql, new ClaimSummaryRowMapper());
        return claimSummaries;
    }

    @Override
    public List<ClaimSummary> claimsFilteredBySurname(final String originTag, final String dateString, final String sortBy) {
        //1) CLAIM_DATETIME_KEY 2) originTag 3) date 4) STATUS_KEY 5) originTag 6) sort type 7) sort 7) sortKey 8) originTag
        final String surnames = "ntoz".equalsIgnoreCase(sortBy) ? "[n-z]%" : "[a-m]%";
        final String sql = "SELECT * FROM crosstab(" +
                "'SELECT cs.transId as transactionId, cs.key as key, cs.value as value " +
                "FROM carers.claimsummary cs " +
                "WHERE cs.transId IN (" +
                "SELECT cssq.transId as transactionId " +
                "FROM carers.claimsummary cssq " +
                "WHERE cssq.key = ''" + ClaimServiceHelper.CLAIM_DATETIME_KEY + "'' " +
                "AND cssq.origintag = ''" + originTag + "'' " +
                "AND left(cssq.value,8) = ''" + dateString + "'' " +
                "INTERSECT SELECT cssq.transId as transactionId " +
                "FROM carers.claimsummary cssq " +
                "WHERE cssq.key = ''" + ClaimServiceHelper.STATUS_KEY + "'' " +
                "AND cssq.origintag = ''" + originTag + "'' " +
                "AND cssq.value != ''completed'' " +
                "INTERSECT SELECT c.transId as transactionId " +
                "FROM carers.claimsummary c " +
                "WHERE c.value SIMILAR TO ''" + surnames + "'' " +
                "AND length(c.value)>0 " +
                "AND c.key = ''" + ClaimServiceHelper.SORT_KEY + "'' " +
                "AND c.origintag = ''" + originTag + "'' " +
                "INTERSECT SELECT ct.transId as transactionId " +
                "FROM carers.claimsummary ct " +
                "WHERE ct.key=''" + ClaimServiceHelper.CLAIM_TYPE_KEY + "'' " +
                "AND ct.value=''" + SACONSTANT_CLAIM + "'' " +
                "AND ct.origintag = ''" + originTag + "'') " +
                "ORDER BY transactionId ASC'," +
                "'SELECT DISTINCT key FROM carers.claimsummary ORDER BY 1'" +
                ") AS " +
                "( " +
                "transactionId varchar, claimDateTime varchar, claimType varchar, forename varchar, " +
                "nino varchar, sortby varchar, status varchar, surname varchar " +
                ")";
        final List<ClaimSummary> claimSummaries = jdbcTemplate.query(sql, new ClaimSummaryRowMapper());
        return claimSummaries;
    }

    @Override
    public List<ClaimSummary> claimsFiltered(final String originTag, final String dateString, final String status) {
        //1) CLAIM_DATETIME_KEY 2) originTag 3) date 4) STATUS_KEY 5) originTag 6) status
        final String sql = "SELECT * FROM crosstab(" +
                "'SELECT cs.transId as transactionId, cs.key as key, cs.value as value " +
                "FROM carers.claimsummary cs " +
                "WHERE cs.transId IN (" +
                "SELECT cssq.transId as transactionId " +
                "FROM carers.claimsummary cssq " +
                "WHERE cssq.key = ''" + ClaimServiceHelper.CLAIM_DATETIME_KEY + "'' " +
                "AND cssq.origintag = ''" + originTag + "'' " +
                "AND left(cssq.value,8) = ''" + dateString + "'' " +
                "INTERSECT SELECT cssq.transId as transactionId " +
                "FROM carers.claimsummary cssq " +
                "WHERE cssq.key = ''" + ClaimServiceHelper.STATUS_KEY + "'' " +
                "AND cssq.origintag = ''" + originTag + "'' " +
                "AND cssq.value = ''" + status + "'') " +
                "ORDER BY transactionId ASC'," +
                "'SELECT DISTINCT key from carers.claimsummary ORDER BY 1'" +
                ") AS " +
                "( " +
                "transactionId varchar, claimDateTime varchar, claimType varchar, forename varchar, " +
                "nino varchar, sortby varchar, status varchar, surname varchar " +
                ")";
        final List<ClaimSummary> claimSummaries = jdbcTemplate.query(sql, new ClaimSummaryRowMapper());
        return claimSummaries;
    }

    @Override
    public String fullClaim(final String transactionId, final String originTag) {
        final String sql = "SELECT claimXml FROM carers.claim WHERE transId = ? AND origintag = ?";
        final String xml = jdbcTemplate.query(sql, resultSet -> resultSet.next() ? resultSet.getString(1) : null, transactionId, originTag);
        if (xml != null && !xml.isEmpty()) {
            updateClaim(transactionId, "viewed");
        }
        return xml;
    }

    @Override
    public Boolean updateClaim(final String transactionId, final String status) {
        //1) status 2) transactionId 3) STATUS_KEY 4) status
        final String sqlUpdate = "UPDATE carers.claimsummary SET value=? WHERE transId=? AND key=? AND value!=? AND value!='completed'";
        final Object[] args = new Object[] { status, transactionId, ClaimServiceHelper.STATUS_KEY, status };
        Boolean rtn = Boolean.TRUE;
        final int stored = jdbcTemplate.update(sqlUpdate, args);
        if (stored <= 0) {
            LOGGER.error("Could not update status: " + status + " for transactionId " + transactionId + ".");
            rtn = Boolean.FALSE;
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Successfully updated status: " + status + " for transactionId=" + transactionId + ".");
            }
            createClaimAudit(transactionId, status);
        }
        return rtn;
    }

    private void createClaimAudit(final String transactionId, final String status) {
        final Object[] args = new Object[] { transactionId, status };
        final String sqlInsert = "INSERT INTO carers.claimaudit(transId,status) VALUES(?,?)";
        jdbcTemplate.update(sqlInsert, args);
    }

    @Override
    public Map<String, Long> claimNumbersFiltered(final String originTag, final List<String> statuses) {
        //1) STATUS_KEY 2) originTag 3) CLAIM_DATETIME_KEY 4) originTag
        final String sql = "WITH lasteightdays (days) AS (" +
                "VALUES (to_char(now() - interval '7 day', 'DDMMYYYY')), " +
                "(to_char(now() - interval '6 day', 'DDMMYYYY'))," +
                "(to_char(now() - interval '5 day', 'DDMMYYYY'))," +
                "(to_char(now() - interval '4 day', 'DDMMYYYY'))," +
                "(to_char(now() - interval '3 day', 'DDMMYYYY'))," +
                "(to_char(now() - interval '2 day', 'DDMMYYYY'))," +
                "(to_char(now() - interval '1 day', 'DDMMYYYY'))," +
                "(to_char(now(), 'DDMMYYYY'))" +
                ") " +
                "SELECT days AS daysDate, coalesce(dailyCount, 0) AS dailyCount " +
                "FROM lasteightdays " +
                "LEFT JOIN (" +
                "SELECT left(c1.value,8) AS daysDate, count(c1.*) AS dailyCount " +
                "FROM carers.claimsummary c1, carers.claimsummary c2 " +
                "WHERE c1.key = ? " +
                "AND c1.origintag = ? " +
                "AND c2.transId = c1.transId " +
                "AND c2.key = ? " +
                "AND c2.origintag = ? AND (" + buildStatusString(statuses, "c2.value") + ") " +
                "AND to_date(left(c1.value,8), 'DDMMYYYY') >= now() - interval '8 day' " +
                "GROUP BY left(c1.value,8) " +
                ") as c1 on (c1.daysDate = lasteightdays.days)";
        final List<ClaimSummaryCount> claimSummaryCounts = jdbcTemplate.query(sql, new ClaimSummaryCountRowMapper(),
                ClaimServiceHelper.CLAIM_DATETIME_KEY, originTag, ClaimServiceHelper.STATUS_KEY, originTag);
        final Map<String, Long> result = claimSummaryCounts.stream().collect(Collectors.toMap(ClaimSummaryCount::getDay, ClaimSummaryCount::getCount));
        return result;
    }

    private String buildStatusString(final List<String> statuses, final String columnName) {
        final StringBuilder statusString = new StringBuilder();
        for (String status : statuses) {
            if (statusString != null && statusString.length() > 0) {
                statusString.append(" OR ");
            }
            statusString.append(columnName).append("='"+ status + "'");
        }
        return statusString.toString();
    }

    @Override
    public Map<String, TabCount> constructClaimSummaryWithTabTotals(final String originTag, final String dateString) {
        final Map<String, TabCount> counts = new ConcurrentHashMap<>();
        counts.put("counts",  new TabCount(countOfClaimsForTab(originTag, dateString, "[a-m]%"),
                countOfClaimsForTab(originTag, dateString, "[n-z]%"),
                countOfCircsForTab(originTag, dateString)));
        return counts;
    }

    private Long countOfClaimsForTab(final String originTag, final String dateString, final String sortBy) {
        //1) CLAIM_DATETIME_KEY 2) originTag 3) date 4) STATUS_KEY 5) originTag 6) SORT_KEY 7) originTag 8) sortBy
        final String sql = "SELECT COALESCE(count(c1.*),'0') as tabsCount " +
                "FROM carers.claimsummary c1,carers.claimsummary c2,carers.claimsummary c3,carers.claimsummary c4 " +
                "WHERE c1.key = ? AND c1.origintag = ? AND LEFT(c1.value,8) = ? " +
                "AND c1.transId = c2.transId AND c2.key = ? AND c2.origintag = ? AND c2.value != 'completed' " +
                "AND c2.transId = c3.transId AND c3.key = ? AND c3.origintag = ? AND c3.value SIMILAR TO ? " +
                "AND c3.transId = c4.transId AND c4.key = ? AND c4.origintag = ? AND c4.value = '" + SACONSTANT_CLAIM + "' " +
                "AND length(c3.value) > 0 GROUP BY LEFT(c1.value,8)";
        return jdbcTemplate.query(sql, resultSet -> resultSet.next() ? resultSet.getLong("tabsCount") : 0L,
                ClaimServiceHelper.CLAIM_DATETIME_KEY, originTag, dateString, ClaimServiceHelper.STATUS_KEY,
                originTag, ClaimServiceHelper.SORT_KEY, originTag, sortBy, ClaimServiceHelper.CLAIM_TYPE_KEY, originTag);
    }

    private Long countOfCircsForTab(final String originTag, final String dateString) {
        //1) CLAIM_DATETIME_KEY 2) originTag 3) date 4) STATUS_KEY 5) originTag 6) CLAIM_TYPE 7) originTag
        final String sql = "SELECT COALESCE(count(c1.*),'0') as tabsCount " +
                "FROM carers.claimsummary c1,carers.claimsummary c2,carers.claimsummary c3 " +
                "WHERE c1.key = ? AND c1.origintag = ? AND LEFT(c1.value,8) = ? " +
                "AND c1.transId = c2.transId AND c2.key = ? AND c2.origintag = ? AND c2.value != 'completed' " +
                "AND c2.transId = c3.transId AND c3.key = ? AND c3.origintag = ? AND c3.value='" + SACONSTANT_CIRCS + "' " +
                "GROUP BY LEFT(c1.value,8)";
        return jdbcTemplate.query(sql, resultSet -> resultSet.next() ? resultSet.getLong("tabsCount") : 0L,
                ClaimServiceHelper.CLAIM_DATETIME_KEY, originTag, dateString, ClaimServiceHelper.STATUS_KEY,
                originTag, ClaimServiceHelper.CLAIM_TYPE_KEY, originTag);
    }

    @Override
    public List<List<String>> export(final String originTag) {
        //1) originTag
        final String sql = "SELECT * FROM crosstab(" +
                "'SELECT cs.transId as transactionId, cs.key as key, cs.value as value " +
                "FROM carers.claimsummary cs " +
                "WHERE cs.transId IN (" +
                "SELECT transid " +
                "FROM carers.claimsummary " +
                "WHERE key = ''status'' " +
                "AND value = ''completed'' " +
                "AND origintag = ''" + originTag + "'' " +
                "INTERSECT SELECT transid " +
                "FROM carers.claimsummary " +
                "WHERE key = ''claimDateTime'' " +
                "AND (current_date - to_date(value, ''DDMMYYYYHH24MI'')) > 7) " +
                "ORDER BY transactionId ASC'," +
                "'SELECT DISTINCT key from carers.claimsummary ORDER BY 1'" +
                ") AS " +
                "( " +
                "transactionId varchar, claimDateTime varchar, claimType varchar, forename varchar, " +
                "nino varchar, sortby varchar, status varchar, surname varchar " +
                ")";
        final List<List<String>> claimSummaries = jdbcTemplate.query(sql, new ClaimSummaryRowListMapper());
        return addHeader(claimSummaries);
    }

    private List<List<String>> addHeader(final List<List<String>> claimSummaries) {
        claimSummaries.add(0, Arrays.asList("NINO", "Claim Date Time", "Claim Type", "Surname", "sortby", "Status", "Forename"));
        return claimSummaries;
    }

    @Override
    public Boolean purge(final String originTag) {
        //1) originTag 2) originTag
        final String sqlDelete = "DELETE FROM carers.claimsummary " +
                "WHERE transid IN (" +
                "SELECT transid " +
                "FROM carers.claimsummary " +
                "WHERE key = 'status' AND value = 'completed' AND origintag=? " +
                "INTERSECT SELECT transid " +
                "FROM carers.claimsummary " +
                "WHERE key = 'claimDateTime' AND origintag=? AND (current_date - to_date(value,'DDMMYYYYHH24MI')) > 7)";
        final Object[] args = new Object[] { originTag, originTag };
        final int deleted = jdbcTemplate.update(sqlDelete, args);
        Boolean rtn = Boolean.TRUE;
        if (deleted <= 0) {
            LOGGER.error("No rows to purge for origin: " + originTag + ".");
            rtn = Boolean.FALSE;
        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Successfully purged " + deleted + " rows for origin:" + originTag + ".");
        }
        return rtn;
    }

    @Override
    public Boolean submitMessage(final String xml, final Boolean forceToday, final String originTag, final String transactionId) {
        final Object result = transactionTemplate.execute(status -> {
            Boolean rtn = Boolean.FALSE;
            try {
                rtn = insertClaim(transactionId, xml, originTag);
                if (rtn) {
                    rtn = insertClaimSummary(transactionId, xml, forceToday, originTag);
                }
                if (!rtn) {
                    status.setRollbackOnly();
                }
            } catch (Exception ex) {
                LOGGER.error("Failed to insert into claim summary tables, error:" +ex.getMessage(), ex);
                status.setRollbackOnly();
            }
            return rtn;
        });
        return (Boolean)result;
    }

    @Override
    public Boolean updateStatus(final String transactionId, final Integer status) throws SQLException {
        final String sqlUpdate = "UPDATE carers.claim set drs_status=? WHERE transId=?";
        final Object[] args = new Object[] { status, transactionId };
        final int stored = jdbcTemplate.update(sqlUpdate, args);
        Boolean rtn = Boolean.TRUE;
        if (stored <= 0) {
            LOGGER.error("Could not update status: " + status + " for transactionId " + transactionId + ".");
            rtn = Boolean.FALSE;
        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Successfully updated status: " + status + " for transactionId=" + transactionId + ".");
        }
        return rtn;
    }

    private Boolean insertClaim(final String transactionId, final String xml, final String originTag) {
        final String sqlInsert = "INSERT INTO carers.claim(transId, claimXml, originTag) VALUES (?,?,?)";
        final Object[] args = new Object[] { transactionId, xml, originTag };
        final int stored = jdbcTemplate.update(sqlInsert, args);
        Boolean rtn = Boolean.TRUE;
        if (stored <= 0) {
            LOGGER.error("Could not insert message into carers.claim for transactionId " + transactionId + ".");
            rtn = Boolean.FALSE;
            counters.incrementMetric(claimMetric);
        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Successfully inserted message into carers.claim for transactionId=" + transactionId + ".");
        }
        return rtn;
    }

    private Boolean insertClaimSummary(final String transactionId, final String xml, final Boolean forceToday, final String originTag) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ClaimSummary added for transactionId:" + transactionId + ".");
        }
        final Map<String, String> claimSummaryKeyValue = claimServiceHelper.getClaimSummaryKeyValue(xml, forceToday);
        claimSummaryKeyValue.forEach((key, value) -> insertClaimSummary(transactionId, key, value, originTag));
        counters.incrementMetric(claimSummaryMetric);
        return Boolean.TRUE;
    }

    private void insertClaimSummary(final String transactionId, final String key, final String value, final String originTag) {
        final String sqlInsert = "INSERT INTO carers.claimsummary VALUES(?,?,?,?)";
        final Object[] args = new Object[] { transactionId, key, value, originTag };
        final int stored = jdbcTemplate.update(sqlInsert, args);
        if (stored <= 0) {
            LOGGER.error("Could not insert into carers.claimsummary for transactionId " + transactionId + ".");
        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Successfully inserted into carers.claimsummary for transactionId=" + transactionId + ".");
        }
    }
}
