package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinTxIssuer;

public class TxIssuer extends SQLiteEntity
        implements UcoinTxIssuer {

    @SuppressWarnings("unused")
    public static final Creator<TxIssuer> CREATOR = new Creator<TxIssuer>() {
        @Override
        public TxIssuer createFromParcel(Parcel in) {
            return new TxIssuer(in);
        }

        @Override
        public TxIssuer[] newArray(int size) {
            return new TxIssuer[size];
        }
    };

    private Long mTxId;
    private String mPublicKey;
    private Integer mIssuerOrder;

    public TxIssuer(Context context, Long IssuerId) {
        super(context, Provider.TX_ISSUER_URI, IssuerId);
    }

    protected TxIssuer(Parcel in) {
        mTxId = in.readByte() == 0x00 ? null : in.readLong();
        mPublicKey = in.readString();
        mIssuerOrder = in.readByte() == 0x00 ? null : in.readInt();
    }

    @Override
    public Long txId() {
        return (this.mId == null) ? mTxId : getLong(SQLiteTable.TxIssuer.TX_ID);
    }

    @Override
    public String publicKey() {
        return (this.mId == null) ? mPublicKey : getString(SQLiteTable.TxIssuer.PUBLIC_KEY);
    }

    @Override
    public Integer issuerOrder() {
        return (this.mId == null) ? mIssuerOrder : getInt(SQLiteTable.TxIssuer.ISSUER_ORDER);
    }

    @Override
    public String toString() {
        return "TxIssuer id=" + id() + "\n" +
                "tx_id=" + txId() + "\n" +
                "public_key=" + publicKey() + "\n" +
                "issuer_order=" + issuerOrder();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mTxId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mTxId);
        }
        dest.writeValue(mPublicKey);
        if(mIssuerOrder == null) {
            dest.writeByte(((byte) 0x00));
        } else {
            dest.writeByte((byte) 0x01);
            dest.writeInt(mIssuerOrder);
        }
    }
}