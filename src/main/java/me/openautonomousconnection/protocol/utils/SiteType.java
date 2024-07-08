/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol.utils;

public enum SiteType {
    LOCAL("oac-local"), PUBLIC("oac");
    ;

    public final String name;

    SiteType(String name) {
        this.name = name;
    }
}
