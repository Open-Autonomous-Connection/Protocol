/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.protocol;

import java.io.Serializable;

public class APIInformation implements Serializable {
    public final String username;
    public final String apiApplication;
    public final String apiKey;

    public APIInformation(String username, String apiApplication, String apiKey) {
        this.username = username;
        this.apiApplication = apiApplication;
        this.apiKey = apiKey;
    }
}
