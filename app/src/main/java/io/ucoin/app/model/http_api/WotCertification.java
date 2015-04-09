package io.ucoin.app.model.http_api;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.Charset;

public class WotCertification implements Serializable {
    public String pubkey;
    public String uid;
    public Boolean isMember;
    public Certification[] certifications;

    public static WotCertification fromJson(InputStream json) {
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(json, Charset.forName("UTF_8"));
        return gson.fromJson(reader, WotCertification.class);
    }

    public String toString() {
        String s = "pubkey=" + pubkey;
        s += "\nuid=" + uid;
        s += "\nisMember=" + isMember;
        for(Certification certification : certifications) {
            s += "\n\tpubkey=" + certification.pubkey;
            s += "\n\tuid=" + certification.uid;
            s += "\n\tisMember=" + certification.isMember;
            s += "\n\t\tblock=" + certification.cert_time.block;
            s += "\n\t\tmedianTime=" + certification.cert_time.medianTime;
            s += "\n\twritten=" + certification.written.toString();
            s += "\n\tsignature" + certification.signature;
        }

        return s;
    }

    public class Certification implements Serializable {
        public String pubkey;
        public String uid;
        public Boolean isMember;
        public CertTime cert_time;
        public Boolean written;
        public String signature;

    }

    public class CertTime implements Serializable {
        public Long block;
        public Long medianTime;
    }
}
