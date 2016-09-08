package gov.dwp.carers.cs.service.database;

import gov.dwp.carers.Procedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Created by peterwhitehead on 26/08/2016.
 */
@Component
public class DatabasePurgeServiceImpl implements Procedure {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePurgeServiceImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final Integer databasePurgePeriod;
    private static final String PURGE_SQL = "DELETE FROM carers.claim WHERE transid IN (SELECT transid FROM carers.claimsummary WHERE key = 'status' and value = 'completed' INTERSECT SELECT transid FROM carers.claimsummary WHERE key = 'claimDateTime' AND (current_date - to_date(value,'DDMMYYYYHH24MI')) > 7)";

    @Override
    public void invoke() {
        final Object[] args = new Object[] { 4, getCreatedOnTimestamp(databasePurgePeriod) };
        final int stored = jdbcTemplate.update(PURGE_SQL, args);
        if (stored <= 0) {
            LOGGER.debug("No old records > " + databasePurgePeriod + " available to be removed from suspiciousmessages table.");
        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Successfully removed " + stored + " old records from suspiciousmessages table");
        }
    }

    @Inject
    public DatabasePurgeServiceImpl(final JdbcTemplate jdbcTemplate, @Value("${database.purge.period}") final Integer databasePurgePeriod) {
        this.jdbcTemplate = jdbcTemplate;
        this.databasePurgePeriod = databasePurgePeriod;
    }

    private Timestamp getCreatedOnTimestamp(final Integer interval) {
        final Instant instant = Instant.now();
        final Instant instant1 = instant.minus(interval, ChronoUnit.DAYS);
        return new Timestamp(instant1.toEpochMilli());
    }
}
