package io.ucoin.app.model.oldmodels;

import io.ucoin.app.technical.crypto.CryptoUtils;
import io.ucoin.app.technical.crypto.KeyPair;

/**
 * A wallet is a user account
 * Created by eis on 13/01/15.
 */
public class Wallet extends KeyPair {

    private Identity identity;
    private String salt;
    private String currency;

    public Wallet() {
        super(null, null);
        this.identity = new Identity();
    }

    public Wallet(String currency, String uid, byte[] pubKey, byte[] secKey) {
        super(pubKey, secKey);
        this.currency = currency;
        this.identity = new Identity();
        this.identity.setPubkey(pubKey == null ? null : CryptoUtils.encodeBase58(pubKey));
        this.identity.setUid(uid);
    }

    public Wallet(String currency, byte[] secKey, Identity identity) {
        super(CryptoUtils.decodeBase58(identity.getPubkey()), secKey);
        this.currency = currency;
        this.identity = identity;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public String getPubKeyHash() {
        return identity.getPubkey();
    }

    public String getSalt(){
        return salt;
    }

    public void setSalt(String salt){
        this.salt = salt;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isAuthenticate() {
        return secretKey != null && identity != null && identity.getPubkey() != null;
    }

    public String toString() {
        return new StringBuilder().append(identity.getUid())
                .append('@')
                .append(currency)
                .toString();
    }
}
