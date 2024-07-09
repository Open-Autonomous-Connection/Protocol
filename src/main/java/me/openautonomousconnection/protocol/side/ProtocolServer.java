/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol.side;

import me.finn.unlegitlibrary.network.system.server.NetworkServer;
import me.finn.unlegitlibrary.utils.DefaultMethodsOverrider;
import me.openautonomousconnection.protocol.ProtocolBridge;
import me.openautonomousconnection.protocol.domain.Domain;
import me.openautonomousconnection.protocol.domain.RequestDomain;
import me.openautonomousconnection.protocol.listeners.ClientListener;
import me.openautonomousconnection.protocol.listeners.ServerListener;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class ProtocolServer extends DefaultMethodsOverrider {
    public abstract List<Domain> getDomains() throws SQLException;
    public abstract List<String> getTopLevelDomains() throws SQLException;
    public abstract void handleMessage(int clientID, String message);
    public abstract String getDNSServerInfoSite() throws SQLException;
    public abstract String getInfoSite(String topLevelDomain) throws SQLException;
    public abstract String getInterfaceSite() throws SQLException;

    private final int timeoutInSeconds;
    private NetworkServer server;
    private ProtocolBridge protocolBridge;

    public final void setProtocolBridge(ProtocolBridge protocolBridge) {
        this.protocolBridge = protocolBridge;

        server = new NetworkServer.ServerBuilder()
                .enableDebugLog()
                .setEventManager(protocolBridge.getProtocolSettings().eventManager).setPacketHandler(protocolBridge.getProtocolSettings().packetHandler)
                .setMaxAttempts(0).setAttemptDelayInSeconds(5)
                .setPort(protocolBridge.getProtocolSettings().port).build();
    }

    public ProtocolServer(int timeoutInSeconds) throws IOException, InterruptedException {
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public final ProtocolBridge getProtocolBridge() {
        return protocolBridge;
    }

    public final void startServer() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, IOException, InterruptedException {
        server.getEventManager().registerListener(ServerListener.class);
        server.getEventManager().unregisterListener(ClientListener.class);

        server.start();
    }

    public final NetworkServer getServer() {
        return server;
    }

    public final void stopServer() throws IOException {
        server.getEventManager().unregisterListener(ServerListener.class);
        server.stop();
    }

    public final Domain ping(RequestDomain requestDomain) throws SQLException {
        Domain domain = getDomain(requestDomain);
        boolean reachable = false;

        try {
            InetAddress address = null;
            reachable = domain != null;

            if (reachable) address = InetAddress.getByName(domain.parsedDestination());
            if (address == null) reachable = false;
            else reachable = address.isReachable(timeoutInSeconds * 1000);
        } catch (UnknownHostException exception) {
            reachable = domain.parsedDestination().startsWith("https://raw.githubusercontent.com/");

            if (!reachable) exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return domain;
    }

    public final boolean domainExists(RequestDomain domain) throws SQLException {
        return getDomain(domain) != null;
    }

    public final boolean domainExists(Domain domain) throws SQLException {
        return domainExists(new RequestDomain(domain.name, domain.topLevelDomain));
    }

    public final Domain getDomain(RequestDomain domain) throws SQLException {
        return getDomain(domain.name, domain.topLevelDomain);
    }

    public final boolean topLevelDomainExists(String topLevelDomain) throws SQLException {
        return topLevelDomain.endsWith("oac") || getTopLevelDomains().contains(topLevelDomain);
    }

    public final Domain getDomain(String name, String topLevelDomain) throws SQLException {
        if (!topLevelDomainExists(topLevelDomain)) return null;

        if (name.equalsIgnoreCase("info") && topLevelDomain.equalsIgnoreCase("oac")) return new Domain(name, topLevelDomain, getDNSServerInfoSite());
        if (name.equalsIgnoreCase("info")) return new Domain(name, topLevelDomain, getInfoSite(topLevelDomain));
        if (name.equalsIgnoreCase("interface") && topLevelDomain.equalsIgnoreCase("oac")) return new Domain(name, topLevelDomain, getInterfaceSite());

        for (Domain domain : getDomains()) if (domain.name.equals(name) && domain.topLevelDomain.equals(topLevelDomain)) return domain;
        return null;
    }
}
