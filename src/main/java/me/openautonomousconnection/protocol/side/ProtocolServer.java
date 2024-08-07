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
import java.net.*;
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

    private NetworkServer server;
    private ProtocolBridge protocolBridge;

    public final void setProtocolBridge(ProtocolBridge protocolBridge) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.protocolBridge = protocolBridge;

        server = new NetworkServer.ServerBuilder()
                .setEventManager(protocolBridge.getProtocolSettings().eventManager).setPacketHandler(protocolBridge.getProtocolSettings().packetHandler)
                .setMaxReconnectAttempts(10).setReconnectDelay(5)
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

        String destination = domain.parsedDestination();

        if (!destination.startsWith("http://") && !destination.startsWith("https://")) destination = "http://" + destination;

        HttpURLConnection connection = null;

        try {
            URL u = new URL(destination);

            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");

            int code = connection.getResponseCode();

            reachable = code == 200;
        } catch (IOException exception) {
            InetAddress address = null;
            try {
                InetAddress address1 = InetAddress.getByName(destination);
                String ip = address1.getHostAddress();
                address = InetAddress.getByName(ip);
                reachable = address.isReachable(10000);
            } catch (IOException exc) {
                reachable = false;
                exc.printStackTrace();
            }

            reachable = false;
            exception.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
        }

        return domain;
    }

    public final boolean domainExists(RequestDomain domain) throws SQLException {
        return getDomain(domain) != null;
    }

    public final boolean domainExists(Domain domain) throws SQLException {
        return domainExists(new RequestDomain(domain.name, domain.topLevelDomain, domain.getPath()));
    }

    public final Domain getDomain(RequestDomain domain) throws SQLException {
        return getDomain(domain.name, domain.topLevelDomain, domain.getPath());
    }

    public final boolean topLevelDomainExists(String topLevelDomain) throws SQLException {
        return topLevelDomain.equalsIgnoreCase("oac") || getTopLevelDomains().contains(topLevelDomain);
    }

    public final Domain getDomain(String name, String topLevelDomain, String path) throws SQLException {
        if (!topLevelDomainExists(topLevelDomain)) return null;

        if (name.equalsIgnoreCase("info") && topLevelDomain.equalsIgnoreCase("oac")) return new Domain(name, topLevelDomain, getDNSServerInfoSite(), path);
        if (name.equalsIgnoreCase("interface") && topLevelDomain.equalsIgnoreCase("oac")) return new Domain(name, topLevelDomain, getInterfaceSite(), path);

        if (name.equalsIgnoreCase("info")) return new Domain(name, topLevelDomain, getInfoSite(topLevelDomain), path);

        for (Domain domain : getDomains()) if (domain.name.equals(name) && domain.topLevelDomain.equals(topLevelDomain)) return new Domain(domain.name, domain.topLevelDomain, domain.realDestination(), path);
        return null;
    }
}
