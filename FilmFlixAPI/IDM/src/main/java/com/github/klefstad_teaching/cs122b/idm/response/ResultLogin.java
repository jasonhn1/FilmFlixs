package com.github.klefstad_teaching.cs122b.idm.response;


import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;

public class ResultLogin extends ResponseModel<ResultLogin> {

    private String accessToken;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
