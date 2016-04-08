package at.tgm.ablkreim.balancer;

import java.io.Serializable;
import java.math.BigDecimal;

public class PiResponse implements Serializable {

    public final BigDecimal pi;
    public final long id;

    public PiResponse(BigDecimal pi, long id) {

        this.pi = pi;
        this.id = id;
    }

    @Override
    public String toString() {

        return "PiResponse{#" + id + " Pi:" + pi + "}";
    }
}
