package github.openautonomousconnection.protocol.versions.v1_0_0.classic;

import java.io.Serializable;

public class Classic_RequestDomain extends Classic_Domain implements Serializable {

    public Classic_RequestDomain(String name, String topLevelDomain, String path) {
        super(name, topLevelDomain, null, path);
    }
}
