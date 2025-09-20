package github.openautonomousconnection.protocol.versions.v1_0_0.classic;

import java.io.Serializable;

enum Classic_SiteType implements Serializable {
    CLIENT("oac-client"), SERVER("oac-server"),
    PUBLIC("oac"), PROTOCOL("oac-protocol"), LOCAL("oac-local");

    public final String name;

    Classic_SiteType(String name) {
        this.name = name;
    }
}
