package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinSources;
import io.ucoin.app.model.UcoinTxs;
import io.ucoin.app.model.UcoinWallet;

public class Wallet extends SQLiteEntity
        implements UcoinWallet {

    private Long mCurrencyId;
    private String mCurrencyName;
    private String mSalt;
    private String mPublicKey;
    private String mPrivateKey;
    private String mAlias;
    private Long mQuantitativeAmount;
    private Float mRelativeAmount;

    public Wallet(Context context, Long walletId) {
        super(context, Provider.WALLET_URI, walletId);
    }

    @Override
    public Long currencyId() {
        return getLong(SQLiteView.Wallet.CURRENCY_ID);
    }

    @Override
    public String currencyName() {
        return getString(SQLiteView.Wallet.CURRENCY_NAME);
    }

    @Override
    public String salt() {
        return getString(SQLiteView.Wallet.SALT);
    }

    @Override
    public String publicKey() {
        return getString(SQLiteView.Wallet.PUBLIC_KEY);
    }

    @Override
    public String privateKey() {
        return getString(SQLiteView.Wallet.PRIVATE_KEY);
    }

    @Override
    public String alias() {
        return getString(SQLiteView.Wallet.ALIAS);
    }

    @Override
    public Long quantitativeAmount(){
        return getLong(SQLiteView.Wallet.QUANTITATIVE_AMOUNT);
    }

    @Override
    public Float relativeAmount() {
        return getFloat(SQLiteView.Wallet.RELATIVE_AMOUNT);
    }

    @Override
    public UcoinSources sources() {
        return new Sources(mContext, mId);
    }

    @Override
    public UcoinTxs txs() {
        return new Txs(mContext, mId);
    }

    @Override
    public String toString() {
        String s = "WALLET id=" + id() + "\n" ;
        s += "\ncurrencyId=" + currencyId();
        s += "\ncurrencyName=" + currencyName();
        s += "\nsalt=" + salt();
        s += "\npublicKey=" + publicKey();
        s += "\nprivateKey=" + privateKey();
        s += "\nalias=" + alias();
        s += "\nquantitativeAmount=" + quantitativeAmount();
        s += "\nrelativeAmount=" + relativeAmount();

        return s;
    }

    protected Wallet(Parcel in) {
        mCurrencyId = in.readByte() == 0x00 ? null : in.readLong();
        mCurrencyName = in.readString();
        mSalt = in.readString();
        mPublicKey = in.readString();
        mPrivateKey = in.readString();
        mAlias = in.readString();
        mQuantitativeAmount = in.readByte() == 0x00 ? null : in.readLong();
        mRelativeAmount = in.readByte() == 0x00 ? null : in.readFloat();
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
        dest.writeString(mCurrencyName);
        dest.writeString(mSalt);
        dest.writeString(mPublicKey);
        dest.writeString(mPrivateKey);
        dest.writeString(mAlias);
        if (mQuantitativeAmount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mQuantitativeAmount);
        }
        if (mRelativeAmount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(mRelativeAmount);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Wallet> CREATOR = new Parcelable.Creator<Wallet>() {
        @Override
        public Wallet createFromParcel(Parcel in) {
            return new Wallet(in);
        }

        @Override
        public Wallet[] newArray(int size) {
            return new Wallet[size];
        }
    };
}