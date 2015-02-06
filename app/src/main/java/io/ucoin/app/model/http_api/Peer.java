package io.ucoin.app.model.http_api;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;

import io.ucoin.app.technical.StandardCharsets;
import io.ucoin.app.technical.gson.GsonUtils;


public class Peer implements Serializable {

    public String currency;
    public String pubkey;
    public String signature;
    public String block;
    public String[] endpoints;


    public static Peer fromJson(InputStream json) {
        Gson gson = GsonUtils.newBuilder().create();
        Reader reader = new InputStreamReader(json, StandardCharsets.UTF_8);
        return gson.fromJson(reader, Peer.class);
    }

    public String toString() {
        String s = "currency=" + currency;
        s += "\npubkey=" + pubkey;
        s += "\nsignature=" + signature;
        s += "\nblock=" + block;
        s += "\nendpoints:";
        for(String endpoint : endpoints) {
            s += "\n\t" + endpoint;
        }

        return s;
    }
}
