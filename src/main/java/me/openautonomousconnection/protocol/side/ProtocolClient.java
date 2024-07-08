/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol.side;

import me.finn.unlegitlibrary.network.system.client.NetworkClient;
import me.openautonomousconnection.protocol.ProtocolBridge;
import me.openautonomousconnection.protocol.domain.RequestDomain;
import me.openautonomousconnection.protocol.listeners.ClientListener;
import me.openautonomousconnection.protocol.listeners.ServerListener;
import me.openautonomousconnection.protocol.packets.v1_0_0.DomainPacket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class ProtocolClient {

    private NetworkClient client;
    private ProtocolBridge protocolBridge;
    
    public abstract void handleHTMLContent(String htmlContent);
    public abstract void handleMessage(String message);

    public final NetworkClient getClient() {
        return client;
    }

    public final ProtocolBridge getProtocolBridge() {
        return protocolBridge;
    }

    public final void setProtocolBridge(ProtocolBridge protocolBridge) {
        this.protocolBridge = protocolBridge;

        client = new NetworkClient.ClientBuilder()
                .enableDebugLog().enableAutoReconnect()
                .setEventManager(protocolBridge.getProtocolSettings().eventManager).setPacketHandler(protocolBridge.getProtocolSettings().packetHandler)
                .setMaxAttempts(10).setAttemptDelayInSeconds(1)
                .setPort(protocolBridge.getProtocolSettings().port).setHost(protocolBridge.getProtocolSettings().host).
                build();
    }

    public final void startClient() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, IOException, InterruptedException {
        client.getEventManager().unregisterListener(ServerListener.class);
        client.getEventManager().registerListener(ClientListener.class);

        client.connect();
    }

    public final void disconnectClient() throws IOException {
        client.getEventManager().unregisterListener(ClientListener.class);
        client.disconnect();
    }
    
    public final void resolveSite(RequestDomain requestDomain) throws IOException, ClassNotFoundException {
        if (!client.isConnected()) return;
        
        client.sendPacket(new DomainPacket(protocolBridge, requestDomain, null));
    }
}
