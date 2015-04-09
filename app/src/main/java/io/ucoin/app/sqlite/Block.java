package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinBlock;

public class Block extends SQLiteEntity
        implements UcoinBlock {

    @SuppressWarnings("unused")
    public static final Creator<Block> CREATOR = new Creator<Block>() {
        @Override
        public Block createFromParcel(Parcel in) {
            return new Block(in);
        }

        @Override
        public Block[] newArray(int size) {
            return new Block[size];
        }
    };
    private Long mCurrencyId;
    private Integer mNumber;
    private Long mMonetaryMass;
    private String mSignature;

    public Block(Context context, Long blockId) {
        super(context, Provider.BLOCK_URI, blockId);
    }


    protected Block(Parcel in) {
        mCurrencyId = in.readByte() == 0x00 ? null : in.readLong();
        mNumber = in.readByte() == 0x00 ? null : in.readInt();
        mMonetaryMass = in.readByte() == 0x00 ? null : in.readLong();
        mSignature = in.readString();
    }

    @Override
    public Long currencyId() {
        return (this.mId == null) ? mCurrencyId : getLong(SQLiteTable.Block.CURRENCY_ID);
    }

    @Override
    public Long monetaryMass() {
        return (this.mId == null) ? mMonetaryMass : getLong(SQLiteTable.Block.MONETARY_MASS);
    }

    @Override
    public String signature() {
        return (this.mId == null) ? mSignature : getString(SQLiteTable.Block.SIGNATURE);
    }

    @Override
    public Integer number() {
        return (this.mId == null) ? mNumber : getInt(SQLiteTable.Block.NUMBER);
    }

    @Override
    public String toString() {
        return "BLOCK id=" + ((id() == null) ? "not in database" : id()) + "\n" +
                "currency_id=" + currencyId() + "\n" +
                "number=" + number() + "\n" +
                "monetary_mass=" + monetaryMass() +
                "signature=" + signature();
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
        if (mNumber == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mNumber);
        }

        if (mMonetaryMass == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mMonetaryMass);
        }
        dest.writeString(mSignature);
    }
}