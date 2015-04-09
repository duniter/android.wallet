package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinTxSignature;

public class TxSignature extends SQLiteEntity
        implements UcoinTxSignature {

    @SuppressWarnings("unused")
    public static final Creator<TxSignature> CREATOR = new Creator<TxSignature>() {
        @Override
        public TxSignature createFromParcel(Parcel in) {
            return new TxSignature(in);
        }

        @Override
        public TxSignature[] newArray(int size) {
            return new TxSignature[size];
        }
    };

    private Long mTxId;
    private String mValue;
    private Integer mIssuerOrder;

    public TxSignature(Context context, Long signatureId) {
        super(context, Provider.TX_SIGNATURE_URI, signatureId);
    }

    protected TxSignature(Parcel in) {
        mTxId = in.readByte() == 0x00 ? null : in.readLong();
        mValue = in.readString();
        mIssuerOrder = in.readByte() == 0x00 ? null : in.readInt();
    }

    @Override
    public Long txId() {
        return (this.mId == null) ? mTxId : getLong(SQLiteTable.TxSignature.TX_ID);
    }

    @Override
    public String value() {
        return (this.mId == null) ? mValue : getString(SQLiteTable.TxSignature.VALUE);
    }

    @Override
    public Integer issuerOrder() {
        return (this.mId == null) ? mIssuerOrder : getInt(SQLiteTable.TxSignature.ISSUER_ORDER);
    }

    @Override
    public String toString() {
        return "TxSignature id=" + id() + "\n" +
                "tx_id=" + txId() + "\n" +
                "value=" + value() + "\n" +
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
        dest.writeValue(mValue);
        if(mIssuerOrder == null) {
            dest.writeByte(((byte) 0x00));
        } else {
            dest.writeByte((byte) 0x01);
            dest.writeInt(mIssuerOrder);
        }
    }
}