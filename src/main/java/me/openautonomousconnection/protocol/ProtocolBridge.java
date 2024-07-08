/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol;

import me.finn.unlegitlibrary.network.system.client.NetworkClient;
import me.openautonomousconnection.protocol.listeners.ClientListener;
import me.openautonomousconnection.protocol.listeners.ServerListener;
import me.openautonomousconnection.protocol.side.ProtocolClient;
import me.openautonomousconnection.protocol.side.ProtocolServer;
import me.openautonomousconnection.protocol.utils.APIInformation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;

public class ProtocolBridge {
    private final APIInformation apiInformation;
    private final ProtocolSettings protocolSettings;
    private final ProtocolVersion protocolVersion;

    private final ProtocolServer protocolServer;
    private final ProtocolClient protocolClient;

    public final boolean isRunningAsServer() {
        return protocolServer != null;
    }

    public final ProtocolClient getProtocolClient() {
        return protocolClient;
    }

    public final ProtocolSettings getProtocolSettings() {
        return protocolSettings;
    }

    public final ProtocolServer getProtocolServer() {
        return protocolServer;
    }

    public final ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    public ProtocolBridge(ProtocolVersion protocolVersion, ProtocolSettings protocolSettings, ProtocolClient protocolClient, APIInformation apiInformation) {
        checkUpdates();

        this.protocolServer = null;

        this.protocolVersion = protocolVersion;
        this.protocolSettings = protocolSettings;
        this.apiInformation = apiInformation;
        this.protocolClient = protocolClient;
    }

    public ProtocolBridge(ProtocolVersion protocolVersion, ProtocolSettings protocolSettings, ProtocolServer protocolServer) {
        checkUpdates();

        this.apiInformation = null;
        this.protocolClient = null;

        this.protocolVersion = protocolVersion;
        this.protocolSettings = protocolSettings;
        this.protocolServer = protocolServer;
    }

    private final void checkUpdates() {
        try {
            URL oracle = new URL("https://raw.githubusercontent.com/Open-Autonomous-Connection/Protocol/master/src/main/java/me/openautonomousconnection/protocol/version.txt");

            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
            String version = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null) version += inputLine;

            if (!version.equalsIgnoreCase(Files.readString(new File("version.txt").toPath()))) {
                System.out.println();
                System.out.println("===============================================");
                System.out.println("IMPORTANT: A NEW VERSION IS PUBLISHED ON GITHUB");
                System.out.println("===============================================");
                System.out.println();
            }
        } catch (IOException exception) {
            System.out.println();
            System.out.println("===============================================");
            System.out.println("IMPORTANT: VERSION CHECK COULD NOT COMPLETED! VISIT OUR GITHUB");
            System.out.println("https://github.com/Open-Autonomous-Connection");
            System.out.println("===============================================");
            System.out.println();
        }
    }

    public final APIInformation getApiInformation() {
        return apiInformation;
    }
}
