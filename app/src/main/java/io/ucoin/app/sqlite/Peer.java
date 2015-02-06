package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinEndpoint;
import io.ucoin.app.model.UcoinEndpoints;
import io.ucoin.app.model.UcoinPeer;

public class Peer extends SQLiteEntity
        implements UcoinPeer {

    private Long mCurrencyId;
    private String mPublicKey;
    private String mBlock;
    private String mSignature;
    private UcoinEndpoints mEndpoints;

    public Peer(Context context, Long peerId) {
        super(context, Provider.PEER_URI, peerId);
        mEndpoints = new Endpoints(context, peerId);
    }

    public Peer(Long currencyId, io.ucoin.app.model.http_api.Peer peer) {
        mCurrencyId = currencyId;
        mPublicKey = peer.pubkey;
        mBlock = peer.block;
        mSignature = peer.signature;
        mEndpoints = new Endpoints(mId, peer);
    }

    public Peer(Long currencyId, UcoinPeer peer) {
        mCurrencyId = currencyId;
        mPublicKey = peer.publicKey();
        mBlock = peer.block();
        mSignature = peer.signature();
        mEndpoints = peer.endpoints();
    }

    @Override
    public Long currencyId() {
        return (this.mId == null) ? mCurrencyId : getLong(Contract.Peer.CURRENCY_ID);
    }

    @Override
    public String publicKey() {
        return (this.mId == null) ? mPublicKey : getString(Contract.Peer.PUBLIC_KEY);
    }

    @Override
    public String block() {
        return (this.mId == null) ? mBlock : getString(Contract.Peer.PUBLIC_KEY);
    }

    @Override
    public String signature() {
        return (this.mId == null) ? mSignature : getString(Contract.Peer.SIGNATURE);
    }

    @Override
    public UcoinEndpoints endpoints() {
        return mEndpoints;
    }

    @Override
    public String toString() {
        String s = "PEER id=" + ((id() == null) ? "not in database" : id()) + "\n" +
                "currency_id=" + currencyId() + "\n" +
                "public_key=" + publicKey() + "\n" +
                "block=" + block() + "\n" +
                "signature=" + signature();

        for(UcoinEndpoint endpoint : mEndpoints) {
            s += "\n\t" + endpoint .toString();
        }

        return s;
    }

    protected Peer(Parcel in) {
        mCurrencyId = in.readByte() == 0x00 ? null : in.readLong();
        mPublicKey = in.readString();
        mBlock = in.readString();
        mSignature = in.readString();
        mEndpoints = (UcoinEndpoints) in.readValue(UcoinEndpoints.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mCurrencyId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mCurrencyId);
        }
        dest.writeString(mPublicKey);
        dest.writeString(mBlock);
        dest.writeString(mSignature);
        dest.writeValue(mEndpoints);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Peer> CREATOR = new Parcelable.Creator<Peer>() {
        @Override
        public Peer createFromParcel(Parcel in) {
            return new Peer(in);
        }

        @Override
        public Peer[] newArray(int size) {
            return new Peer[size];
        }
    };
}