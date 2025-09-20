package github.openautonomousconnection.protocol.versions.v1_0_0.classic;

import java.io.Serializable;

public enum Classic_ProtocolVersion implements Serializable {
    PV_1_0_0("1.0.0");
    public final String version;

    Classic_ProtocolVersion(String version) {
        this.version = version;
    }
}
