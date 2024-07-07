/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol.domain;

import me.openautonomousconnection.protocol.Utils;

import java.io.Serializable;

public class Domain implements Serializable {
    public final String name;
    public final String topLevelDomain;
    private final String destination;

    public Domain(String name, String topLevelDomain, String destination) {
        this.name = name;
        this.topLevelDomain = topLevelDomain;
        this.destination = destination;
    }

    public final String realDestination() {
        return destination;
    }

    public final String parsedDestination() {
        if (destination.toLowerCase().startsWith("https://github.com/")) {

            String base = "https://raw.githubusercontent.com/";
            String username = Utils.getPath(destination).split("/")[0];
            String site = Utils.getPath(destination).split("/")[1];

            return base + username + "/" + site + "/main/index.html";
        }

        return destination;
    }
}
