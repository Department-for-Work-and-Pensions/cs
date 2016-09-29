package gov.dwp.carers.cs.model;

/**
 * Created by peterwhitehead on 25/07/2016.
 */
public class ClaimStatus {
    private String id;
    private String desc;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(final String desc) {
        this.desc = desc;
    }

    public ClaimStatus(final String id, final String desc) {
        this.id = id;
        this.desc = desc;
    }

    public ClaimStatus() {
        //constructor for rest
    }
}
