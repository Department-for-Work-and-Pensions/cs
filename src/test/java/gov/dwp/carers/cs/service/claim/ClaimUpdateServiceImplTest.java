package gov.dwp.carers.cs.service.claim;

import gov.dwp.carers.cs.helpers.TestUtils;
import gov.dwp.carers.cs.service.database.DatabaseClaimService;
import gov.dwp.carers.cs.service.messaging.DrSubmitter;
import gov.dwp.carers.helper.TestMessage;
import gov.dwp.carers.xml.helpers.XMLExtractor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.SQLException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by peterwhitehead on 12/09/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClaimUpdateServiceImplTest {
    private ClaimUpdateServiceImpl claimUpdateServiceImpl;
    private String msg;
    private String transactionId;
    private final String originTag = "GB";

    @Mock
    private DatabaseClaimService databaseClaimService;

    @Mock
    private DrSubmitter drSubmitter;
    private Boolean dfEnabled;

    @Before
    public void setUp() throws Exception {
        dfEnabled = Boolean.TRUE;
        claimUpdateServiceImpl = new ClaimUpdateServiceImpl(databaseClaimService, drSubmitter, dfEnabled, new XMLExtractor());
    }

    @Test
    public void testClaimUpdate() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(databaseClaimService.updateClaim(transactionId, "received")).thenReturn(Boolean.TRUE);
        assertThat(claimUpdateServiceImpl.claimUpdate(transactionId, "received"), is("Success"));
    }

    @Test
    public void testClaimUpdateFails() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(databaseClaimService.updateClaim(transactionId, "received")).thenReturn(Boolean.FALSE);
        assertThat(claimUpdateServiceImpl.claimUpdate(transactionId, "received"), is("Failure"));
    }

    @Test(expected = SQLException.class)
    public void testClaimUpdateThrowsException() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(databaseClaimService.updateClaim(transactionId, "received")).thenThrow(SQLException.class);
        claimUpdateServiceImpl.claimUpdate(transactionId, "received");
    }

    @Test
    public void testSubmitClaim() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(databaseClaimService.submitMessage(msg, false, originTag, transactionId)).thenReturn(Boolean.TRUE);
        when(drSubmitter.drSubmit(msg, transactionId)).thenReturn(true);
        assertThat(claimUpdateServiceImpl.submitClaim(msg), is("Success"));
        verify(drSubmitter, times(1)).drSubmit(msg, transactionId);
    }

    @Test
    public void testSubmitClaimNoDRS() throws Exception {
        claimUpdateServiceImpl = new ClaimUpdateServiceImpl(databaseClaimService, drSubmitter, Boolean.FALSE, new XMLExtractor());
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(databaseClaimService.submitMessage(msg, false, originTag, transactionId)).thenReturn(Boolean.TRUE);
        assertThat(claimUpdateServiceImpl.submitClaim(msg), is("Success"));
        verify(drSubmitter, times(0)).drSubmit(msg, transactionId);
    }

    @Test(expected = SQLException.class)
    public void testSubmitClaimThrowsException() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(databaseClaimService.submitMessage(msg, false, originTag, transactionId)).thenThrow(SQLException.class);
        claimUpdateServiceImpl.submitClaim(msg);
    }

    @Test
    public void testSubmitClaimForceToday() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(databaseClaimService.submitMessage(msg, true, originTag, transactionId)).thenReturn(Boolean.TRUE);
        when(drSubmitter.drSubmit(msg, transactionId)).thenReturn(true);
        assertThat(claimUpdateServiceImpl.submitClaimForceToday(msg), is("Success"));
        verify(drSubmitter, times(1)).drSubmit(msg, transactionId);
    }

    @Test
    public void testSubmitClaimForceTodayNoDRS() throws Exception {
        claimUpdateServiceImpl = new ClaimUpdateServiceImpl(databaseClaimService, drSubmitter, Boolean.FALSE, new XMLExtractor());
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(databaseClaimService.submitMessage(msg, true, originTag, transactionId)).thenReturn(Boolean.TRUE);
        assertThat(claimUpdateServiceImpl.submitClaimForceToday(msg), is("Success"));
        verify(drSubmitter, times(0)).drSubmit(msg, transactionId);
    }

    @Test(expected = SQLException.class)
    public void testSubmitClaimForceTodayThrowsException() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(databaseClaimService.submitMessage(msg, true, originTag, transactionId)).thenThrow(SQLException.class);
        claimUpdateServiceImpl.submitClaimForceToday(msg);
    }

    @Test
    public void testPurge() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(databaseClaimService.purge(originTag)).thenReturn(Boolean.TRUE);
        assertThat(claimUpdateServiceImpl.purge(originTag), is("Success"));
    }

    @Test
    public void testPurgeFails() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(databaseClaimService.purge(originTag)).thenReturn(Boolean.FALSE);
        assertThat(claimUpdateServiceImpl.purge(originTag), is("Failure"));
    }

    @Test(expected = SQLException.class)
    public void testPurgeThrowsException() throws Exception {
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName(), TestMessage.ValidXMLWithRSASignature.getTransactionId());
        when(databaseClaimService.purge(originTag)).thenThrow(SQLException.class);
        claimUpdateServiceImpl.purge(originTag);
    }

    private void givenMessageHasArrived(final String fileName, final String transactionId) throws Exception {
        this.msg = TestUtils.loadXmlFromFile(fileName);
        this.transactionId = transactionId;
    }
}