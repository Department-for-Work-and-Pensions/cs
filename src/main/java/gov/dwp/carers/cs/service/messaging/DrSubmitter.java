package gov.dwp.carers.cs.service.messaging;

/**
 * Created by peterwhitehead on 30/06/2016.
 */
public interface DrSubmitter {
    Boolean drSubmit(final String msg, final String transactionId);
}
