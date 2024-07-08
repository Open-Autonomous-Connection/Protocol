/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol.side;

import me.finn.unlegitlibrary.network.system.server.NetworkServer;
import me.openautonomousconnection.protocol.ProtocolBridge;
import me.openautonomousconnection.protocol.domain.Domain;
import me.openautonomousconnection.protocol.domain.RequestDomain;
import me.openautonomousconnection.protocol.listeners.ClientListener;
import me.openautonomousconnection.protocol.listeners.ServerListener;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;

public abstract class ProtocolServer {
    public abstract List<Domain> getDomains() throws SQLException;
    public abstract boolean domainExists(RequestDomain domain) throws SQLException;

    private final int timeoutInSeconds;
    private NetworkServer server;
    private ProtocolBridge protocolBridge;

    public ProtocolServer(ProtocolBridge protocolBridge, int timeoutInSeconds) throws IOException, InterruptedException {
        this.timeoutInSeconds = timeoutInSeconds;
        this.protocolBridge = protocolBridge;

        server = new NetworkServer.ServerBuilder()
                .enableAutoRestart().enableDebugLog()
                .setEventManager(protocolBridge.getProtocolSettings().eventManager).setPacketHandler(protocolBridge.getProtocolSettings().packetHandler)
                .setMaxAttempts(10).setAttemptDelayInSeconds(1)
                .setPort(protocolBridge.getProtocolSettings().port).build();

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

    public final boolean domainExists(Domain domain) throws SQLException {
        return domainExists(new RequestDomain(domain.name, domain.topLevelDomain));
    }

    public final Domain getDomain(RequestDomain domain) throws SQLException {
        return getDomain(domain.name, domain.topLevelDomain);
    }

    public final Domain getDomain(String name, String topLevelDomain) throws SQLException {
        for (Domain domain : getDomains()) if (domain.name.equals(name) && domain.topLevelDomain.equals(topLevelDomain)) return domain;
        return null;
    }
}
