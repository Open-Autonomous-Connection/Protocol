/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol.utils;

import java.io.Serializable;

public enum SiteType implements Serializable {
    CLIENT("oac-client"), SERVER("oac-server"),
    PUBLIC("oac"), PROTOCOL("oac-protocol"), LOCAL("oac-local");
    ;

    public final String name;

    SiteType(String name) {
        this.name = name;
    }
}
