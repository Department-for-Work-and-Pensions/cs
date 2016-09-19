package gov.dwp.carers.cs.controllers;

import gov.dwp.carers.cs.helpers.TestUtils;
import gov.dwp.carers.cs.service.claim.ClaimUpdateService;
import gov.dwp.carers.helper.TestMessage;
import gov.dwp.carers.monitor.Counters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by peterwhitehead on 12/09/2016.
 */
@TestPropertySource(value = "classpath:test.application.properties")
@RunWith(MockitoJUnitRunner.class)
public class ApplicationUpdateTest {
    @Mock
    private ClaimUpdateService claimUpdateService;
    private String msg;
    private String transactionId;
    private final String originTag = "GB";

    @Mock
    private Counters counters;

    private ApplicationUpdate applicationUpdate;

    @Before
    public void setUp() throws Exception {
        applicationUpdate = new ApplicationUpdate(claimUpdateService, counters, "cs-count");
    }

    @Test
    public void testClaimUpdate() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(claimUpdateService.claimUpdate(transactionId, originTag)).thenReturn("Success");
        assertThat(applicationUpdate.claimUpdate(transactionId, originTag), is("Success"));
    }

    @Test
    public void testSubmitClaim() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(claimUpdateService.submitClaim(msg)).thenReturn("");
        assertThat(applicationUpdate.submitClaim(msg), is(""));
    }

    @Test
    public void testSubmitClaimForceToday() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(claimUpdateService.submitClaimForceToday(msg)).thenReturn("");
        assertThat(applicationUpdate.submitClaimForceToday(msg), is(""));
    }

    @Test
    public void testPurge() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(claimUpdateService.purge(originTag)).thenReturn("Success");
        assertThat(applicationUpdate.purge(originTag), is("Success"));
    }

    private void givenMessageHasArrived(final String fileName, final String transactionId) throws Exception {
        this.msg = TestUtils.loadXmlFromFile(fileName);
        this.transactionId = transactionId;
    }
}