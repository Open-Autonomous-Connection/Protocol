/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol.listeners;

import me.finn.unlegitlibrary.event.EventListener;
import me.finn.unlegitlibrary.event.Listener;
import me.openautonomousconnection.protocol.ProtocolBridge;
import me.openautonomousconnection.protocol.domain.Domain;
import me.openautonomousconnection.protocol.domain.LocalDomain;
import me.openautonomousconnection.protocol.events.v1_0_0.DomainPacketReceivedEvent;
import me.openautonomousconnection.protocol.events.v1_0_0.PingPacketReceivedEvent;
import me.openautonomousconnection.protocol.packets.v1_0_0.PingPacket;
import me.openautonomousconnection.protocol.utils.SiteType;
import me.openautonomousconnection.protocol.utils.WebsitesContent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

public class ClientListener extends EventListener {

    @Listener
    public void onDomain(DomainPacketReceivedEvent event) {
        boolean exists = event.domain != null;

        if (exists) {
            if (!ProtocolBridge.getInstance().getProtocolClient().getClient().sendPacket(new PingPacket(event.requestDomain, event.domain, false))) {
                ProtocolBridge.getInstance().getProtocolClient().handleHTMLContent(SiteType.PROTOCOL, new LocalDomain("error-occurred", "html", ""),
                        WebsitesContent.ERROR_OCCURRED(event.domain.toString() + "/" + event.domain.getPath()));
            }
        } else ProtocolBridge.getInstance().getProtocolClient().handleHTMLContent(SiteType.PROTOCOL, new LocalDomain("domain-not-found", "html", ""), WebsitesContent.DOMAIN_NOT_FOUND);
    }

    @Listener
    public void onPing(PingPacketReceivedEvent event) {
        if (event.reachable) {
            String destination = event.domain.parsedDestination();
            boolean reachable = false;

            if (destination == null) return;
            if (!destination.startsWith("http://")) destination = "http://" + destination;

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

            try {
                if (!reachable) {
                    ProtocolBridge.getInstance().getProtocolClient().handleHTMLContent(SiteType.PROTOCOL, new LocalDomain("error-not-reached", "html", ""), WebsitesContent.DOMAIN_NOT_REACHABLE);
                    return;
                }

                URL url = new URL(destination);
                HttpURLConnection connection2 = (HttpURLConnection) url.openConnection();
                connection2.setRequestMethod("GET");

                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection2.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) content.append(line);
                }

                ProtocolBridge.getInstance().getProtocolClient().handleHTMLContent(SiteType.PUBLIC, event.domain, content.toString());
            } catch (IOException exception) {
                ProtocolBridge.getInstance().getProtocolClient().handleHTMLContent(SiteType.PROTOCOL, new LocalDomain("error-occurred", "html", ""),
                        WebsitesContent.ERROR_OCCURRED(exception.getMessage().replace(event.domain.parsedDestination(), event.domain.toString() + "/" + event.domain.getPath())));
            } finally {
                if (connection != null) connection.disconnect();
            }
        } else ProtocolBridge.getInstance().getProtocolClient().handleHTMLContent(SiteType.PROTOCOL, new LocalDomain("error-not-reached", "html", ""), WebsitesContent.DOMAIN_NOT_REACHABLE);
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public final String toString() {
        return super.toString();
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }
}
