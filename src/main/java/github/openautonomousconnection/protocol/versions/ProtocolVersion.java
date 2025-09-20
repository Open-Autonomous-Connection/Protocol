package github.openautonomousconnection.protocol.versions;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ProtocolVersion implements Serializable {
    PV_1_0_0_CLASSIC("1.0.0", ProtocolType.CLASSIC, ProtocolSide.BOTH),
    PV_1_0_0_BETA("1.0.0", ProtocolType.BETA, ProtocolSide.BOTH, PV_1_0_0_CLASSIC);

    @Getter
    private final String version;
    @Getter
    private final ProtocolType protocolType;
    @Getter
    private final ProtocolSide protocolSide;
    @Getter
    private final List<ProtocolVersion> compatibleVersions;

    ProtocolVersion(String version, ProtocolType protocolType, ProtocolSide protocolSide, ProtocolVersion... compatibleVersions) {
        this.version = version;
        this.protocolType = protocolType;
        this.protocolSide = protocolSide;
        this.compatibleVersions = new ArrayList<>(Arrays.stream(compatibleVersions).toList());
        if (!this.compatibleVersions.contains(this)) this.compatibleVersions.add(this);
    }

    @Override
    public final String toString() {
        StringBuilder compatible = new StringBuilder("[");
        for (ProtocolVersion compatibleVersion : compatibleVersions) compatible.append(compatibleVersion.buildName());
        compatible.append("]");

        return "{version=" + version + ";type=" + protocolType.toString() + ";side=" + protocolSide.toString() + ";compatible=" + compatible + "}";
    }

    public final String buildName() {
        return version + "-" + protocolType.toString();
    }

    public enum ProtocolType implements Serializable {
        CLASSIC, // -> See "_old" Projects on GitHub Organisation https://github.com/Open-Autonomous-Connection/
        BETA,
        STABLE;

        @Override
        public final String toString() {
            return name().toUpperCase();
        }
    }

    public enum ProtocolSide implements Serializable {
        CLIENT, // Protocol version can only used on Client
        SERVER, // Protocol version can only used on Server
        BOTH // Protocol version can only used on Server and Client

        ;

        @Override
        public final String toString() {
            return name().toUpperCase();
        }
    }
}
