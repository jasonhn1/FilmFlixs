package com.github.klefstad_teaching.cs122b.gateway.model;

import java.sql.Timestamp;

public class GatewayRequest {

    private String ipAddress;
    private String path;
    private Timestamp callTime;


    public String getIpAddress() {
        return ipAddress;
    }

    public GatewayRequest setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public String getPath() {
        return path;
    }

    public GatewayRequest setPath(String path) {
        this.path = path;
        return this;
    }

    public Timestamp getCallTime() {
        return callTime;
    }

    public GatewayRequest setCallTime(Timestamp callTime) {
        this.callTime = callTime;
        return this;
    }
}
