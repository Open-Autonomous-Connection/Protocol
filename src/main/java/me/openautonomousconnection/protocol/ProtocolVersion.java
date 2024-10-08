/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol;

import java.io.Serializable;

public enum ProtocolVersion implements Serializable {
    PV_1_0_0("1.0.0");
    ;
    public final String version;

    ProtocolVersion(String version) {
        this.version = version;
    }
}
