package gov.dwp.carers.cs.helpers;

/**
 * Created by peterwhitehead on 28/06/2016.
 */
public enum ClaimServiceStatus {
    SUCCESSFULLY_SENT(0),
    NOT_SENT(1),
    FAILED_TO_SEND(2);

    private Integer status;

    ClaimServiceStatus(final Integer status) { this.status = status; }

    public Integer getStatus() {
        return status;
    }
}
