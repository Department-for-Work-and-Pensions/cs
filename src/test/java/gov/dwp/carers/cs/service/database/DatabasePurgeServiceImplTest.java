package gov.dwp.carers.cs.service.database;

import gov.dwp.carers.configuration.EmbeddedDBJavaConfig;
import gov.dwp.carers.cs.helpers.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

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
    private EmbeddedDatabase dataSource;
    private JdbcTemplate jdbcTemplate;
    private DatabasePurgeServiceImpl databasePurgeServiceImpl;
    private DateTimeFormatter dateTimeFormatter;

    @Before
    public void setUp() throws Exception {
        dataSource = new EmbeddedDBJavaConfig().dataSource();
        jdbcTemplate = new JdbcTemplate(dataSource);
        databasePurgeServiceImpl = new DatabasePurgeServiceImpl(jdbcTemplate, 8);
        dateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");
    }

    @Test
    public void testInvoke() throws Exception {
        String time = getCreatedOn(10);
        TestUtils.insertClaim("1610000234", "<test>testing</test>", "1", "GB", jdbcTemplate);
        TestUtils.insertClaimSummary("1610000234", "status", "completed", "GB", jdbcTemplate);
        TestUtils.insertClaimSummary("1610000234", "claimDateTime", time, "GB", jdbcTemplate);
        databasePurgeServiceImpl.invoke();
        assertThat(TestUtils.transactionIdExistsInClaim("1610000234", jdbcTemplate), is(false));
        assertThat(TestUtils.transactionIdExistsInClaimSummary("1610000234", jdbcTemplate), is(true));
    }

    @Test
    public void testInvokeNewRecord() throws Exception {
        String time = getCreatedOn(7);
        TestUtils.insertClaim("1610000234", "<test>testing</test>", "1", "GB", jdbcTemplate);
        TestUtils.insertClaimSummary("1610000234", "status", "completed", "GB", jdbcTemplate);
        TestUtils.insertClaimSummary("1610000234", "claimDateTime", time, "GB", jdbcTemplate);
        databasePurgeServiceImpl.invoke();
        assertThat(TestUtils.transactionIdExistsInClaim("1610000234", jdbcTemplate), is(true));
        assertThat(TestUtils.transactionIdExistsInClaimSummary("1610000234", jdbcTemplate), is(true));
    }

    private String getCreatedOn(final Integer interval) {
        final LocalDateTime instant = LocalDateTime.now();
        final LocalDateTime instant1 = instant.minus(interval, ChronoUnit.DAYS);
        return dateTimeFormatter.format(instant1);
    }
}