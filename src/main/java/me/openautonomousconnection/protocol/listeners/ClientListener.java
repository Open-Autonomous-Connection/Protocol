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
import me.openautonomousconnection.protocol.events.v1_0_0.DomainPacketReceivedEvent;
import me.openautonomousconnection.protocol.events.v1_0_0.PingPacketReceivedEvent;
import me.openautonomousconnection.protocol.packets.v1_0_0.PingPacket;
import me.openautonomousconnection.protocol.utils.WebsitesContent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientListener extends EventListener {

    @Listener
    public void onDomain(DomainPacketReceivedEvent event) {
        boolean exists = event.domain != null;

        if (exists) {
            try {
                event.protocolBridge.getProtocolClient().getClient().
                        sendPacket(new PingPacket(event.protocolBridge, event.requestDomain, event.domain, false));
            } catch (IOException | ClassNotFoundException exception) {
                event.protocolBridge.getProtocolClient().handleHTMLContent(WebsitesContent.ERROR_OCCURRED(exception.getMessage()));
            }
        } else event.protocolBridge.getProtocolClient().handleHTMLContent(WebsitesContent.DOMAIN_NOT_FOUND);
    }

    @Listener
    public void onPing(PingPacketReceivedEvent event) {
        if (event.reachable) {
            try {
                URL url = new URL(event.domain.parsedDestination());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) content.append(line);
                }

                event.protocolBridge.getProtocolClient().handleHTMLContent(content.toString());
            } catch (IOException exception) {
                event.protocolBridge.getProtocolClient().handleHTMLContent(WebsitesContent.ERROR_OCCURRED(exception.getMessage()));
            }
        } else event.protocolBridge.getProtocolClient().handleHTMLContent(WebsitesContent.DOMAIN_NOT_REACHABLE);
    }
}
