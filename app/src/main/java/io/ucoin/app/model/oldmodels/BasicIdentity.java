package io.ucoin.app.model.oldmodels;

import java.io.Serializable;

/**
 * Basic information on a identity.
 * 
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 *
 */
public class BasicIdentity implements Serializable {

    private static final long serialVersionUID = 8080689271400316984L;

    private String pubkey;

    private String signature;

    private String uid;

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSelf() {
        return signature;
    }

    public void setSelf(String signature) {
        this.signature = signature;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    
    @Override
    public String toString() {
        return new StringBuilder()
        .append("uid=").append(uid)
        .append(",pubkey=").append(pubkey)
        .append(",signature=").append(signature)
         .toString();
    }

    public void copy(BasicIdentity identity) {
        this.uid = identity.uid;
        this.pubkey = identity.pubkey;
        this.signature = identity.signature;
    }
}
