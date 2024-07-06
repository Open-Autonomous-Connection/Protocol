package me.openautonomousconnection.protocol.domain;

import java.io.Serializable;

public class RequestDomain extends Domain implements Serializable {

    public RequestDomain(String name, String topLevelDomain) {
        super(name, topLevelDomain, null);
    }
}
