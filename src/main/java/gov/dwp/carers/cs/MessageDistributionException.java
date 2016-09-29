package gov.dwp.carers.cs;

import gov.dwp.exceptions.DwpRuntimeException;

/**
 * Created by peterwhitehead on 30/06/2016.
 */

public class MessageDistributionException extends DwpRuntimeException {
    public MessageDistributionException(final Throwable cause) {
        super(cause);
    }

    public MessageDistributionException(final String message) {
        super(message);
    }

    public MessageDistributionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}