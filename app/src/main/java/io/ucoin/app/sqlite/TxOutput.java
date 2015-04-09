package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinTxOutput;

public class TxOutput extends SQLiteEntity
        implements UcoinTxOutput {

    @SuppressWarnings("unused")
    public static final Creator<TxOutput> CREATOR = new Creator<TxOutput>() {
        @Override
        public TxOutput createFromParcel(Parcel in) {
            return new TxOutput(in);
        }

        @Override
        public TxOutput[] newArray(int size) {
            return new TxOutput[size];
        }
    };

    private Long mTxId;
    private String mPublicKey;
    private Long mAmount;

    public TxOutput(Context context, Long outputId) {
        super(context, Provider.TX_OUTPUT_URI, outputId);
    }

    protected TxOutput(Parcel in) {
        mTxId = in.readByte() == 0x00 ? null : in.readLong();
        mPublicKey = in.readString();
        mAmount = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public Long txId() {
        return (this.mId == null) ? mTxId : getLong(SQLiteTable.TxOutput.TX_ID);
    }

    @Override
    public String publicKey() {
        return (this.mId == null) ? mPublicKey : getString(SQLiteTable.TxOutput.PUBLIC_KEY);
    }

    @Override
    public Long amount() {
        return (this.mId == null) ? mAmount : getLong(SQLiteTable.TxOutput.AMOUNT);
    }


    @Override
    public String toString() {
        return "TxOutput id=" + id() + "\n" +
                "tx_id=" + txId() + "\n" +
                "public_key=" + publicKey() + "\n" +
                "amount=" + amount();
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
        dest.writeString(mPublicKey);
        if (mAmount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mAmount);
        }
    }
}