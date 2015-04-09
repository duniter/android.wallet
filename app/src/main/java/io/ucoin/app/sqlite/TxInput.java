package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;

import io.ucoin.app.content.Provider;
import io.ucoin.app.enums.SourceType;
import io.ucoin.app.model.UcoinTxInput;

public class TxInput extends SQLiteEntity
        implements UcoinTxInput {

    @SuppressWarnings("unused")
    public static final Creator<TxInput> CREATOR = new Creator<TxInput>() {
        @Override
        public TxInput createFromParcel(Parcel in) {
            return new TxInput(in);
        }

        @Override
        public TxInput[] newArray(int size) {
            return new TxInput[size];
        }
    };

    private Long mTxId;
    private Integer mIndex;
    private SourceType mType;
    private Long mNumber;
    private String mFingerprint;
    private Long mAmount;

    public TxInput(Context context, Long inputId) {
        super(context, Provider.TX_INPUT_URI, inputId);
    }

    protected TxInput(Parcel in) {
        mTxId = in.readByte() == 0x00 ? null : in.readLong();
        mIndex = in.readByte() == 0x00 ? null : in.readInt();
        mType = (SourceType) in.readValue(SourceType.class.getClassLoader());
        mNumber = in.readByte() == 0x00 ? null : in.readLong();
        mFingerprint = in.readString();
        mAmount = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public Long txId() {
        return (this.mId == null) ? mTxId : getLong(SQLiteTable.TxInput.TX_ID);
    }

    @Override
    public Integer index() {
        return (this.mId == null) ? mIndex : getInt(SQLiteTable.TxInput.ISSUER_INDEX);
    }

    @Override
    public SourceType type() {
        return (this.mId == null) ? mType : SourceType.valueOf(getString(SQLiteTable.TxInput.TYPE));
    }

    @Override
    public Long number() {
        return (this.mId == null) ? mNumber : getLong(SQLiteTable.TxInput.NUMBER);
    }

    @Override
    public String fingerprint() {
        return (this.mId == null) ? mFingerprint : getString(SQLiteTable.TxInput.FINGERPRINT);
    }

    @Override
    public Long amount() {
        return (this.mId == null) ? mAmount : getLong(SQLiteTable.TxInput.AMOUNT);
    }


    @Override
    public String toString() {
        return "TxInput id=" + id() + "\n" +
                "tx_id=" + txId() + "\n" +
                "index=" + index() + "\n" +
                "type=" + type().name() + "\n" +
                "number=" + number() + "\n" +
                "fingerprint=" + fingerprint() + "\n" +
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
        if(mIndex == null) {
            dest.writeByte(((byte) 0x00));
        } else {
            dest.writeByte((byte) 0x01);
            dest.writeInt(mIndex);
        }
        dest.writeValue(mType);

        if (mNumber == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mNumber);
        }
        dest.writeString(mFingerprint);
        if (mAmount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mAmount);
        }
    }
}