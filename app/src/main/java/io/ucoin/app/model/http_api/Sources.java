package io.ucoin.app.model.http_api;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;

import io.ucoin.app.technical.StandardCharsets;
import io.ucoin.app.technical.gson.GsonUtils;


public class Sources implements Serializable {

    public String currency;
    public String  pubkey;
    public Source[] sources = new Source[]{};

    public static io.ucoin.app.model.http_api.Sources fromJson(InputStream json) {
        Gson gson = GsonUtils.newBuilder().create();
        Reader reader = new InputStreamReader(json, StandardCharsets.UTF_8);
        return gson.fromJson(reader, io.ucoin.app.model.http_api.Sources.class);
    }

    public String toString() {
        String s = "API_SOURCES____________________";
        s += "\ncurency=" + currency;
        s += "\npubkey=" + pubkey;
        for (Source source : sources) {
            s += "\ntype=" + source.type;
            s += "\nnumber=" + source.number;
            s += "\nfingerprint=" + source.fingerprint;
            s += "\namount=" + source.amount;
        }

        return s;
    }

    public class Source implements Serializable {
        public String type;
        public Integer number;
        public String fingerprint;
        public Long amount;
    }
}
