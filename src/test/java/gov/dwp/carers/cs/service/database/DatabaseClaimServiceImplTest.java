package gov.dwp.carers.cs.service.database;

import gov.dwp.carers.configuration.EmbeddedPostgresDB;
import gov.dwp.carers.configuration.EmbeddedPostgresDataSource;
import gov.dwp.carers.cs.helpers.ClaimServiceHelper;
import gov.dwp.carers.cs.helpers.TestUtils;
import gov.dwp.carers.cs.model.ClaimSummary;
import gov.dwp.carers.cs.model.TabCount;
import gov.dwp.carers.helper.TestMessage;
import gov.dwp.carers.monitor.Counters;
import gov.dwp.carers.xml.helpers.XMLExtractor;
import gov.dwp.carers.xml.helpers.XmlSchemaDecryptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by peterwhitehead on 12/09/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class DatabaseClaimServiceImplTest {
    private EmbeddedPostgresDataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private String msg;
    private String transactionId;
    private Boolean rtn;
    private List<ClaimSummary> claims;
    private Map<String, Long> data;
    private Map<String, TabCount> tabs;
    private List<List<String>> export;

    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private Counters counters;

    @Mock
    private PlatformTransactionManager transactionManager;
    private String claimMetric = "metric1";
    private String claimSummaryMetric = "metric2";
    private String date;
    private ClaimSummary claimSummary;
    private final transient SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmm");

    private DatabaseClaimServiceImpl databaseClaimServiceImpl;

    @Before
    public void setUp() throws Exception {
        transactionId = "1610000234";
        dataSource = new EmbeddedPostgresDB().dataSource();
        jdbcTemplate = new JdbcTemplate(dataSource);
        databaseClaimServiceImpl = new DatabaseClaimServiceImpl(jdbcTemplate, transactionManager,
                new ClaimServiceHelper(new XmlSchemaDecryptor(), new XMLExtractor()), counters, claimMetric, claimSummaryMetric);
    }

    @After
    public void finish() {
        dataSource.stop();
    }

    @Test
    public void testHealth() throws Exception {
        assertThat(databaseClaimServiceImpl.health(), is(true));
    }

    @Test
    public void testClaims() throws Exception {
        givenMessageHasBeenReceived("received", "claim", null);
        whenClaimsCalled();
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testCircs() throws Exception {
        givenMessageHasBeenReceived("received", "circs", null);
        whenCircsCalled();
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaimsFilteredBySurname() throws Exception {
        givenMessageHasBeenReceived("received", "circs", null);
        whenClaimsFilteredBySurnameCalled();
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testClaimsFiltered() throws Exception {
        givenMessageHasBeenReceived("received", "claim", null);
        whenClaimsFilteredCalled();
        thenClaimSummaryShouldBe();
    }

    @Test
    public void testFullClaim() throws Exception {
        givenMessageHasBeenReceived("received", "claim", null);
        whenFullClaimCalled();
        thenFullClaimReturnedShouldBe();
    }

    @Test
    public void testUpdateClaim() throws Exception {
        givenMessageHasBeenReceived("received", "claim", null);
        whenUpdateStatusCalled("completed");
        thenUpdateStatusShouldBe("completed");
    }

    @Test
    public void testClaimNumbersFiltered() throws Exception {
        givenMessageHasBeenReceived("received", "claim", null);
        whenClaimNumbersFilteredCalled();
        thenClaimNumbersFilteredReturnedShouldBe();
    }

    @Test
    public void testConstructClaimSummaryWithTabTotals() throws Exception {
        givenMessageHasBeenReceived("received", "claim", null);
        whenClaimSummaryTabsCalled();
        thenClaimSummaryTabsShouldBe();
    }

    @Test
    public void testExport() throws Exception {
        givenMessageHasBeenReceived("completed", "claim", "100820162345");
        whenClaimsExportedCalled();
        thenClaimsExportedReturnedShouldBe();
    }

    @Test
    public void testPurge() throws Exception {
        givenMessageHasBeenReceived("completed", "claim", "100820162345");
        whenPurgeCalled();
        thenPurgeReturnedShouldBe();
    }

    @Test
    public void testSubmitMessage() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        whenSubmitMessageCalled();
        thenSubmitMessageShouldBe();
    }

    @Test
    public void testUpdateStatus() throws Exception {
        givenMessageHasBeenReceived("received", "claim", null);
        whenUpdateStatusCalled("completed");
        thenUpdateStatusReturnedShouldBe("completed");
    }

    private void givenMessageHasArrived(final String fileName, final String transactionId) throws Exception {
        this.msg = TestUtils.loadXmlFromFile(fileName);
        this.transactionId = transactionId;
    }

    private void givenMessageHasBeenReceived(String status, String claimType, String queryDate) throws Exception {
        if (queryDate == null) {
            Date newDate = new Date();
            date = simpleDateFormat.format(newDate);
        } else {
            date = queryDate;
        }

        TestUtils.insertClaim(transactionId, "<test>testing</test>", 1, "GB", jdbcTemplate);
        TestUtils.insertClaimSummary(transactionId, "status", status, "GB", jdbcTemplate);
        TestUtils.insertClaimSummary(transactionId, "claimDateTime", date, "GB", jdbcTemplate);
        TestUtils.insertClaimSummary(transactionId, "claimType", claimType, "GB", jdbcTemplate);
        TestUtils.insertClaimSummary(transactionId, "sortby", "a", "GB", jdbcTemplate);
        TestUtils.insertClaimSummary(transactionId, "nino", "AB123456B", "GB", jdbcTemplate);
        TestUtils.insertClaimSummary(transactionId, "forename", "fred", "GB", jdbcTemplate);
        TestUtils.insertClaimSummary(transactionId, "surname", "bieber", "GB", jdbcTemplate);
        TestUtils.insertClaimAudit(transactionId, 1, jdbcTemplate);
        claimSummary = new ClaimSummary(transactionId, claimType, "AB123456B", "fred", "bieber", simpleDateFormat.parse(date).getTime(), status);
        date = date.substring(0, 8);
    }

    private void whenClaimsCalled() {
        claims = databaseClaimServiceImpl.claims("GB", date);
    }

    private void whenCircsCalled() {
        claims = databaseClaimServiceImpl.claims("GB", date);
    }

    private void whenClaimsFilteredCalled() {
        claims = databaseClaimServiceImpl.claimsFiltered("GB", date, "received");
    }

    private void whenClaimsFilteredBySurnameCalled() {
        claims = databaseClaimServiceImpl.claimsFilteredBySurname("GB", date, "atom");
    }

    private void whenClaimNumbersFilteredCalled() {
        this.data = databaseClaimServiceImpl.claimNumbersFiltered("GB", Arrays.asList("received"));
    }

    private void thenClaimNumbersFilteredReturnedShouldBe() {
        assertThat(data.get(date), is(1L));
    }

    private void whenClaimsExportedCalled() {
        this.export = databaseClaimServiceImpl.export("GB");
    }

    private void thenClaimsExportedReturnedShouldBe() {
        if (export.size() == 1) {
            fail("No claims returned");
        }
        assertThat(export.get(0).get(0), is("NINO"));
        assertThat(export.get(1).get(0), is(claimSummary.getNino()));
    }

    private void whenFullClaimCalled() {
        this.msg = databaseClaimServiceImpl.fullClaim(transactionId, "GB");
    }

    private void thenFullClaimReturnedShouldBe() {
        assertThat(msg, is("<test>testing</test>"));
        assertThat(TestUtils.getKeyValue(transactionId, "status", jdbcTemplate), is("viewed"));
    }

    private void whenUpdateStatusCalled(String status) {
        this.rtn = databaseClaimServiceImpl.updateClaim(transactionId, status);
    }

    private void thenUpdateStatusShouldBe(String status) {
        assertThat(rtn, is(Boolean.TRUE));
        assertThat(TestUtils.getKeyValue(transactionId, "status", jdbcTemplate), is(status));
        assertThat(TestUtils.checkStatusHistory(transactionId, status, jdbcTemplate), is("received"));
    }

    private void whenClaimSummaryTabsCalled() {
       this.tabs = databaseClaimServiceImpl.constructClaimSummaryWithTabTotals("GB", date);
    }

    private void thenClaimSummaryTabsShouldBe() {
        TabCount tabCount = new TabCount(1L, 0L, 0L);
        org.assertj.core.api.Assertions.assertThat(tabs.get("counts")).isEqualToComparingFieldByField(tabCount);
    }

    private void whenPurgeCalled() {
        this.rtn = databaseClaimServiceImpl.purge("GB");
    }

    private void thenPurgeReturnedShouldBe() {
        assertThat(rtn, is(Boolean.TRUE));
        assertThat(TestUtils.transactionIdExistsInClaimSummary(transactionId, jdbcTemplate), is(false));
    }

    private void thenClaimSummaryShouldBe() {
        if (claims.isEmpty()) {
            fail("No claims returned");
        }
        for (int i = 0; i < claims.size(); i++) {
            org.assertj.core.api.Assertions.assertThat(claims.get(i)).isEqualToComparingFieldByField(claimSummary);
        }
    }

    private void whenSubmitMessageCalled() {
        this.rtn = databaseClaimServiceImpl.submitMessage(msg, true, "GB", transactionId);
    }

    private void thenSubmitMessageShouldBe() {
        assertThat(TestUtils.transactionIdExistsInClaim(transactionId, jdbcTemplate), is(true));
        assertThat(TestUtils.transactionIdExistsInClaimSummary(transactionId, jdbcTemplate), is(true));
        assertThat(TestUtils.getKeyValue(transactionId, "status", jdbcTemplate), is("received"));
    }

    private void thenUpdateStatusReturnedShouldBe(String status) {
        assertThat(TestUtils.getKeyValue(transactionId, "status", jdbcTemplate), is(status));
    }
}