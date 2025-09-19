package github.openautonomousconnection.protocol.classic;

import java.io.Serializable;

enum Classic_SiteType implements Serializable {
    CLIENT("oac-client"), SERVER("oac-server"),
    PUBLIC("oac"), PROTOCOL("oac-protocol"), LOCAL("oac-local");
    ;

    public final String name;

    Classic_SiteType(String name) {
        this.name = name;
    }
}
