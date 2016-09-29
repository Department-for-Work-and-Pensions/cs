package gov.dwp.carers.cs.model;

/**
 * Created by peterwhitehead on 06/09/2016.
 */
public class TabCount {
    private Long atom;
    private Long ntoz;
    private Long circs;

    public TabCount(final Long atom, final Long ntoz, final Long circs) {
        this.atom = atom;
        this.ntoz = ntoz;
        this.circs = circs;
    }

    public Long getAtom() {
        return atom;
    }

    public void setAtom(final Long atom) {
        this.atom = atom;
    }

    public Long getNtoz() {
        return ntoz;
    }

    public void setNtoz(final Long ntoz) {
        this.ntoz = ntoz;
    }

    public Long getCircs() {
        return circs;
    }

    public void setCircs(final Long circs) {
        this.circs = circs;
    }
}
