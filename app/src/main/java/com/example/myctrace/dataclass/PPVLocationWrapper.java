package com.example.myctrace.dataclass;

import com.google.gson.Gson;

public class PPVLocationWrapper {

    public static PPVLocation fromJson(String s) {
        return new Gson().fromJson(s, PPVLocation.class);
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
