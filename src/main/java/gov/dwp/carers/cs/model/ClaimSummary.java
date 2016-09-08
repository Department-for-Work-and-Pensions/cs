package gov.dwp.carers.cs.model;

/**
 * Created by peterwhitehead on 05/09/2016.
 */
public class ClaimSummary {
    private String transactionId;
    private String claimType;
    private String nino;
    private String forename;
    private String surname;
    private Long claimDateTime;
    private String status;
    private String drsStatus;

    public ClaimSummary(final String transactionId,
                        final String claimType,
                        final String nino,
                        final String forename,
                        final String surname,
                        final Long claimDateTime,
                        final String status) {
        this.transactionId = transactionId;
        this.claimType = claimType;
        this.nino = nino;
        this.forename = forename;
        this.surname = surname;
        this.claimDateTime = claimDateTime;
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public String getNino() {
        return nino;
    }

    public void setNino(String nino) {
        this.nino = nino;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Long getClaimDateTime() {
        return claimDateTime;
    }

    public void setClaimDateTime(Long claimDateTime) {
        this.claimDateTime = claimDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDrsStatus() {
        return drsStatus;
    }

    public void setDrsStatus(String drsStatus) {
        this.drsStatus = drsStatus;
    }
}
