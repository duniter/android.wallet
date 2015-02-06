package io.ucoin.app.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinSource;
import io.ucoin.app.model.UcoinSources;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.enums.SourceType;

public class Wallet extends SQLiteEntity
        implements UcoinWallet {

    private Long mCurrencyId;
    private String mSalt;
    private String mPublicKey;
    private String mPrivateKey;
    private String mAlias;
    private UcoinSources mSources;

    public Wallet(Context context, Long walletId) {
        super(context, Provider.WALLET_URI, walletId);
        mSources = new Sources(context, walletId);
    }

    public Wallet(Long currencyId, String salt, String publicKey, String alias) {
        mCurrencyId = currencyId;
        mSalt = salt;
        mPublicKey = publicKey;
        mAlias = alias;
        mSources = new Sources(mId, new ArrayList<UcoinSource>());
    }

    public Wallet(Long currencyId, String salt, String publicKey, String privateKey,
                  String alias) {
        mCurrencyId = currencyId;
        mSalt = salt;
        mPublicKey = publicKey;
        mPrivateKey = privateKey;
        mAlias = alias;
        mSources = new Sources(mId, new ArrayList<UcoinSource>());
    }

    public Wallet(Long currencyId, UcoinWallet wallet) {
        mCurrencyId = currencyId;
        mSalt = wallet.salt();
        mPublicKey = wallet.publicKey();
        mPrivateKey = wallet.privateKey();
        mAlias = wallet.alias();
        mSources = wallet.sources();
    }

    @Override
    public Long currencyId() {
        return (this.mId == null) ? mCurrencyId : getLong(Contract.Peer.CURRENCY_ID);
    }

    @Override
    public String salt() {
        return (this.mId == null) ? mSalt : getString(Contract.Wallet.SALT);
    }

    @Override
    public String publicKey() {
        return (this.mId == null) ? mPublicKey : getString(Contract.Wallet.PUBLIC_KEY);
    }

    @Override
    public String privateKey() {
        return (this.mId == null) ? mPrivateKey : getString(Contract.Wallet.PRIVATE_KEY);
    }

    @Override
    public String alias() {
        return (this.mId == null) ? mAlias : getString(Contract.Wallet.ALIAS);
    }



    @Override
    public Long relativeBalance() {
        return (long) 0;
    }

    @Override
    public Long quantitativeBalance(){
        if(mId == null)
            return (long) 0;

        Uri uri = Uri.withAppendedPath(Provider.BALANCE_URI, mId.toString());
        Cursor cursor = mContext.getContentResolver().query(uri,null,
                null, null, null);

        if (!cursor.moveToNext())
            return (long) 0;

        int balanceIndex = cursor.getColumnIndex("balance");
        Long balance = cursor.getLong(balanceIndex);
        cursor.close();
        return balance;
    }

    @Override
    public UcoinSources sources() {
        return mSources;
    }

    @Override
    public UcoinSources newSources(io.ucoin.app.model.http_api.Sources sources) {
        ArrayList<UcoinSource> sourcesArray = new ArrayList<>();
        for(io.ucoin.app.model.http_api.Sources.Source apiSource : sources.sources) {
            UcoinSource source = new Source(mId, apiSource.number,
                    SourceType.valueOf(apiSource.type),
                    apiSource.fingerprint, apiSource.amount);
            sourcesArray.add(source);
        }
        return new Sources(mId, sourcesArray);
    }

    @Override
    public String toString() {
        String s = "WALLET id=" + ((id() == null) ? "not in database" : id()) + "\n" ;
        s += "\ncurrencyId=" + currencyId();
        s += "\nsalt=" + salt();
        s += "\npublicKey=" + publicKey();
        s += "\nprivateKey=" + privateKey();
        s += "\nalias=" + alias();

        for(UcoinSource source : mSources) {
            s += "\n\t" + source .toString();
        }

        return s;
    }

    protected Wallet(Parcel in) {
        mCurrencyId = in.readByte() == 0x00 ? null : in.readLong();
        mSalt = in.readString();
        mPublicKey = in.readString();
        mPrivateKey = in.readString();
        mAlias = in.readString();
        mSources = (UcoinSources) in.readValue(UcoinSources.class.getClassLoader());
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
        dest.writeString(mSalt);
        dest.writeString(mPublicKey);
        dest.writeString(mPrivateKey);
        dest.writeString(mAlias);
        dest.writeValue(mSources);
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