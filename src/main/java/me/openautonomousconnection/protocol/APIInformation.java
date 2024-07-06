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
