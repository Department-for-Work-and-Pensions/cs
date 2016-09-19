package gov.dwp.carers.cs.service.database;

import gov.dwp.carers.configuration.EmbeddedPostgresDB;
import gov.dwp.carers.configuration.EmbeddedPostgresDataSource;
import gov.dwp.carers.cs.helpers.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by peterwhitehead on 12/09/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class DatabasePurgeServiceImplTest {
    private EmbeddedPostgresDataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private DatabasePurgeServiceImpl databasePurgeServiceImpl;
    private DateTimeFormatter dateTimeFormatter;
    private static final String TRANSACTION_ID = "1610000234";
    private static final String ORIGIN_TAG = "GB";

    @Before
    public void setUp() throws Exception {
        dataSource = new EmbeddedPostgresDB().dataSource();
        jdbcTemplate = new JdbcTemplate(dataSource);
        databasePurgeServiceImpl = new DatabasePurgeServiceImpl(jdbcTemplate, 8);
        dateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");
    }

    @After
    public void finish() {
        dataSource.stop();
    }

    @Test
    public void testInvoke() throws Exception {
        final String time = getCreatedOn(10);
        TestUtils.insertClaim(TRANSACTION_ID, "<test>testing</test>", 1, ORIGIN_TAG, jdbcTemplate);
        TestUtils.insertClaimSummary(TRANSACTION_ID, "status", "completed", ORIGIN_TAG, jdbcTemplate);
        TestUtils.insertClaimSummary(TRANSACTION_ID, "claimDateTime", time, ORIGIN_TAG, jdbcTemplate);
        databasePurgeServiceImpl.invoke();
        assertThat(TestUtils.transactionIdExistsInClaim(TRANSACTION_ID, jdbcTemplate), is(false));
        assertThat(TestUtils.transactionIdExistsInClaimSummary(TRANSACTION_ID, jdbcTemplate), is(true));
    }

    @Test
    public void testInvokeNewRecord() throws Exception {
        final String time = getCreatedOn(7);
        TestUtils.insertClaim(TRANSACTION_ID, "<test>testing</test>", 1, ORIGIN_TAG, jdbcTemplate);
        TestUtils.insertClaimSummary(TRANSACTION_ID, "status", "completed", ORIGIN_TAG, jdbcTemplate);
        TestUtils.insertClaimSummary(TRANSACTION_ID, "claimDateTime", time, ORIGIN_TAG, jdbcTemplate);
        databasePurgeServiceImpl.invoke();
        assertThat(TestUtils.transactionIdExistsInClaim(TRANSACTION_ID, jdbcTemplate), is(true));
        assertThat(TestUtils.transactionIdExistsInClaimSummary(TRANSACTION_ID, jdbcTemplate), is(true));
    }

    private String getCreatedOn(final Integer interval) {
        final LocalDateTime instant = LocalDateTime.now();
        final LocalDateTime instant1 = instant.minus(interval, ChronoUnit.DAYS);
        return dateTimeFormatter.format(instant1);
    }
}