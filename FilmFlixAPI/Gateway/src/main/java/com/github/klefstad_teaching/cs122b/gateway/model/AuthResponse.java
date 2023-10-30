package com.github.klefstad_teaching.cs122b.gateway.model;

public class AuthResponse {

    private MyResultClass result;

    public MyResultClass getResult() {
        return result;
    }

    public AuthResponse setResult(MyResultClass result) {
        this.result = result;
        return this;
    }
}
