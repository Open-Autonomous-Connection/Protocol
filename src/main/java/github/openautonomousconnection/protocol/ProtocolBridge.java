package github.openautonomousconnection.protocol;

import github.openautonomousconnection.protocol.listeners.ClientListener;
import github.openautonomousconnection.protocol.listeners.ServerListener;
import github.openautonomousconnection.protocol.packets.OACPacket;
import github.openautonomousconnection.protocol.versions.ProtocolVersion;
import github.openautonomousconnection.protocol.versions.v1_0_0.classic.ClassicHandlerClient;
import github.openautonomousconnection.protocol.versions.v1_0_0.classic.ClassicHandlerServer;
import github.openautonomousconnection.protocol.packets.v1_0_0.classic.Classic_DomainPacket;
import github.openautonomousconnection.protocol.side.client.ProtocolClient;
import github.openautonomousconnection.protocol.side.server.ProtocolServer;
import lombok.Getter;
import lombok.Setter;
import me.finn.unlegitlibrary.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ProtocolBridge {

    @Getter
    private final ProtocolSettings protocolSettings;

    @Getter
    private final ProtocolVersion protocolVersion;

    @Getter
    private ProtocolServer protocolServer;

    @Getter
    private ProtocolClient protocolClient;

    @Getter
    private final Logger logger;

    @Getter @Setter
    private ClassicHandlerServer classicHandlerServer;
    @Getter @Setter
    private ClassicHandlerClient classicHandlerClient;

    @Getter
    private static ProtocolBridge instance;

    public ProtocolBridge(ProtocolServer protocolServer, ProtocolSettings protocolSettings, ProtocolVersion protocolVersion, File logFolder) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        this.protocolServer = protocolServer;
        this.protocolSettings = protocolSettings;
        this.protocolVersion = protocolVersion;

        Logger tmpLogger = null;
        try {
            tmpLogger = new Logger(logFolder, false, true);
        } catch (IOException | NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
            tmpLogger = null;
            System.exit(1);
        }

        this.logger = tmpLogger;
        protocolSettings.eventManager.registerListener(new ServerListener());
        protocolSettings.eventManager.unregisterListener(new ClientListener());

        if (!validateProtocolSide()) {
            this.logger.error("Invalid protocol version '" + protocolVersion.toString() + "'!");
            System.exit(1);
        }

        instance = this;
    }

    public ProtocolBridge(ProtocolClient protocolClient, ProtocolSettings protocolSettings, ProtocolVersion protocolVersion, File logFolder) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        this.protocolClient = protocolClient;
        this.protocolSettings = protocolSettings;
        this.protocolVersion = protocolVersion;

        Logger tmpLogger = null;
        try {
            tmpLogger = new Logger(logFolder, false, true);
        } catch (IOException | NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
            tmpLogger = null;
            System.exit(1);
        }

        this.logger = tmpLogger;
        protocolSettings.eventManager.registerListener(new ClientListener());
        protocolSettings.eventManager.unregisterListener(new ServerListener());

        if (!validateProtocolSide()) {
            this.logger.error("Invalid protocol version '" + protocolVersion.toString() + "'!");
            System.exit(1);
        }

        if (isClassicSupported()) {
            Classic_DomainPacket cDomainPacket = new Classic_DomainPacket();
            Classic_DomainPacket cMessagePacket = new Classic_DomainPacket();
            Classic_DomainPacket cPingPacket = new Classic_DomainPacket();

            if (isPacketSupported(cDomainPacket)) protocolSettings.packetHandler.registerPacket(cDomainPacket);
            if (isPacketSupported(cMessagePacket)) protocolSettings.packetHandler.registerPacket(cMessagePacket);
            if (isPacketSupported(cPingPacket)) protocolSettings.packetHandler.registerPacket(cPingPacket);
        }

        instance = this;
    }

    public boolean isPacketSupported(OACPacket packet) {
        return isVersionSupported(packet.getProtocolVersion());
    }

    public boolean isClassicSupported() {
        boolean yes = false;
        for (ProtocolVersion compatibleVersion : protocolVersion.getCompatibleVersions()) {
            yes = compatibleVersion.getProtocolType() == ProtocolVersion.ProtocolType.CLASSIC;
            if (yes) break;
        }

        return protocolVersion.getProtocolType() == ProtocolVersion.ProtocolType.CLASSIC || yes;
    }

    public boolean isRunningAsServer() {
        return protocolServer != null;
    }

    public boolean isRunningAsClient() {
        return protocolClient != null;
    }

    private boolean validateProtocolSide() {
        return (isRunningAsServer() && protocolVersion.getProtocolSide() != ProtocolVersion.ProtocolSide.CLIENT) ||
                (isRunningAsClient() && protocolVersion.getProtocolSide() != ProtocolVersion.ProtocolSide.SERVER);
    }

    public boolean isVersionSupported(ProtocolVersion targetVersion) {
        return protocolVersion == targetVersion || protocolVersion.getCompatibleVersions().contains(targetVersion);
    }
}
