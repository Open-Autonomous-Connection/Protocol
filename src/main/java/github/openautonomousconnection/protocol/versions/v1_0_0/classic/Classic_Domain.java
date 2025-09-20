package github.openautonomousconnection.protocol.versions.v1_0_0.classic;

import github.openautonomousconnection.protocol.versions.v1_0_0.beta.Domain;
import github.openautonomousconnection.protocol.versions.v1_0_0.beta.DomainTest;
import lombok.Getter;
import me.finn.unlegitlibrary.string.StringUtils;

import java.io.Serializable;

public class Classic_Domain implements Serializable {
    public final String name;
    public final String topLevelDomain;
    private final String destination;
    public final String path;
    @Getter private final Domain domain;

    public Classic_Domain(String name, String topLevelDomain, String destination, String path) {
        this.domain = new Domain(name + "." + topLevelDomain + "/" + (path.startsWith("/") ? path : "/" + path));
        this.name = domain.getName();
        this.topLevelDomain = domain.getTopLevelName();
        this.destination = domain.getDestination();
        this.path = domain.getPath();
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        return new Classic_Domain(name, topLevelDomain, destination, path);
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof Classic_Domain other)) return false;
        return other.name.equalsIgnoreCase(name) && other.topLevelDomain.equalsIgnoreCase(topLevelDomain);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }
}
