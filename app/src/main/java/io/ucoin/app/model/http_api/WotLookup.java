package io.ucoin.app.model.http_api;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;

import io.ucoin.app.technical.StandardCharsets;
import io.ucoin.app.technical.gson.GsonUtils;

public class WotLookup implements Serializable {
    public Result[] results;

    public static WotLookup fromJson(InputStream json) {
        Gson gson = GsonUtils.newBuilder().create();
        Reader reader = new InputStreamReader(json, StandardCharsets.UTF_8);
        return gson.fromJson(reader, WotLookup.class);
    }

    public String toString() {
        String s = new String();
        for (Result result : results) {
            s = "pubkey=" + result.pubkey;
            for (Uid uid : result.uids) {
                s += "\nuid=" + uid.uid;
                s += "\ntimestamp=" + uid.meta.timestamp;
                s += "self=" + uid.self;
            }
        }
        return s;
    }

    public class Result implements Serializable {
        public String pubkey;
        public Uid[] uids;
        public String signature;

    }

    public class Uid implements Serializable {
        public String uid;
        public Meta meta;
        public String self;
    }

    public class Meta implements Serializable {
        public Long timestamp;
    }
}
