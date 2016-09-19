package gov.dwp.carers.cs.controllers;

import gov.dwp.carers.cs.helpers.TestUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Created by peterwhitehead on 15/06/2016.
 */
@ActiveProfiles({ "testpostgres" })
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations="classpath:test.application.properties")
@SpringBootTest(classes = { CsApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class AbstractCsApplicationIntegrationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCsApplicationIntegrationTest.class);

    private static final String BASE_URL = "http://localhost:";

    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    private MockRestServiceServer mockServer;

    @Inject
    protected JdbcTemplate jdbcTemplate;

    @Inject
    private RestTemplate restTemplate;

    @Value("${df.url}")
    private String dfURL;

    @Value("${dr.url}")
    private String drURL;

    @Value("${server.port}")
    private String port;
    protected String msg;
    protected String transactionId;
    protected String date;
    protected String dateTime;

    @Before
    public void setUp() throws Exception {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    protected void whenDfServerReturns(final String body, final MediaType mediaType) throws Exception {
        mockServer.expect(requestTo(dfURL + "/statuses")).andExpect(method(HttpMethod.POST)).andRespond(withSuccess(body, mediaType));
    }

    protected void whenDfServerReturnsError(final String url, final HttpStatus status) throws Exception {
        mockServer.expect(requestTo(dfURL + url)).andExpect(method(HttpMethod.POST)).andRespond(withStatus(status));
    }

    protected void whenDrServerReturns(final HttpStatus status, final int times) throws Exception {
        for (int i=0; i<times; i++) {
            mockServer.expect(requestTo(drURL + "/submission")).andExpect(method(HttpMethod.POST)).andRespond(withStatus(status));
        }
    }

    protected String postMessage(final String url, final String fileName) throws Exception {
        final String textCaseXml = TestUtils.loadXmlFromFile(fileName);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        final HttpEntity<String> request = new HttpEntity<>(textCaseXml, headers);
        return testRestTemplate.exchange(BASE_URL + port + url, HttpMethod.POST, request, String.class).getBody();
    }

    protected String postMessage(final String url) throws Exception {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        final HttpEntity<String> request = new HttpEntity<>("", headers);
        return testRestTemplate.exchange(BASE_URL + port + url, HttpMethod.POST, request, String.class).getBody();
    }

    protected String getMessage(final String url) throws Exception {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        final HttpEntity<String> request = new HttpEntity<>("", headers);
        return testRestTemplate.exchange(BASE_URL + port + url, HttpMethod.GET, request, String.class).getBody();
    }

    protected String putMessage(final String url) throws Exception {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        final HttpEntity<String> request = new HttpEntity<>("", headers);
        return testRestTemplate.exchange(BASE_URL + port + url, HttpMethod.PUT, request, String.class).getBody();
    }

    protected void thenMessageStoredInDatabaseWithStatus(final String transactionId, final String status) {
        assertThat(TestUtils.getKeyValue(transactionId, "status", jdbcTemplate), is(status));
    }

    protected void thenMessageSent() throws Exception {
        mockServer.verify();
    }

    protected void thenResultShouldBe(final String result, final String expectedResult) {
        assertThat(result, is(expectedResult));
    }

    protected void insertClaimSummary(final String originTag, final String status, final String queryDate, final String claimType) {
        if (queryDate == null) {
            Date newDate = new Date();
            dateTime = TestUtils.getSimpleDateFormat().format(newDate);
        } else {
            dateTime = queryDate;
        }
        TestUtils.insertClaim(transactionId, msg, 1, originTag, jdbcTemplate);
        TestUtils.insertClaimSummary(transactionId, "status", status, originTag, jdbcTemplate);
        TestUtils.insertClaimSummary(transactionId, "claimDateTime", dateTime, originTag, jdbcTemplate);
        TestUtils.insertClaimSummary(transactionId, "claimType", claimType, originTag, jdbcTemplate);
        TestUtils.insertClaimSummary(transactionId, "sortby", "b", originTag, jdbcTemplate);
        TestUtils.insertClaimSummary(transactionId, "nino", "AB123456B", originTag, jdbcTemplate);
        TestUtils.insertClaimSummary(transactionId, "forename", "fred", originTag, jdbcTemplate);
        TestUtils.insertClaimSummary(transactionId, "surname", "bieber", originTag, jdbcTemplate);
        TestUtils.insertClaimAudit(transactionId, 1, jdbcTemplate);
        date = dateTime.substring(0, 8);
    }

    protected void givenMessageHasArrived(final String fileName, final String transactionId) throws Exception {
        this.msg = TestUtils.loadXmlFromFile(fileName);
        this.transactionId = transactionId;
    }
}
