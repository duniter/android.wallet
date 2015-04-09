package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinCertification;
import io.ucoin.app.model.UcoinCertifications;
import io.ucoin.app.model.UcoinIdentity;
import io.ucoin.app.model.UcoinWallet;

public class Identity extends SQLiteEntity
        implements UcoinIdentity {

    private Long mCurrencyId;
    private Long mWalletId;
    private String mUid;
    private String mSelf;
    private Long mTimestamp;
    private UcoinWallet mWallet;
    private UcoinCertifications mCertifications;

    public Identity(Context context, Long identityId) {
        super(context, Provider.IDENTITY_URI, identityId);
        mWallet = new Wallet(context, walletId());
        mCertifications = new Certifications(context, identityId);
    }

    public Identity(Long currencyId, Long walletId, String uid) {
        mCurrencyId = currencyId;
        mWalletId = walletId;
        mUid = uid;
    }

    public Identity(Long currencyId, UcoinIdentity identity) {
        mCurrencyId = currencyId;
        mWalletId = identity.walletId();
        mUid = identity.uid();
        mSelf = identity.self();
        mTimestamp = identity.timestamp();
    }

    @Override
    public Long currencyId() {
        return (this.mId == null) ? mCurrencyId : getLong(SQLiteTable.Identity.CURRENCY_ID);
    }

    @Override
    public Long walletId() {
        return (this.mId == null) ? mWalletId : getLong(SQLiteTable.Identity.WALLET_ID);
    }

    @Override
    public String uid() {
        return (this.mId == null) ? mUid : getString(SQLiteTable.Identity.UID);
    }

    @Override
    public String self() {
        return (this.mId == null) ? mSelf : getString(SQLiteTable.Identity.SELF);
    }

    @Override
    public Long timestamp() {
        return (this.mId == null) ? mTimestamp : getLong(SQLiteTable.Identity.TIMESTAMP);
    }

    @Override
    public UcoinWallet wallet() {
        return mWallet;
    }

    @Override
    public UcoinCertifications certifications() {
        return mCertifications;
    }

    @Override
    public String toString() {
        String s = "\nIDENTITY id=" + ((id() == null) ? "not in database" : id()) + "\n";
        s += "\ncurrencyId=" + currencyId();
        s += "\nwalletId=" + walletId();
        s += "\nuid=" + uid();
        s += "\nself=" + self();
        s += "\ntimestamp=" + timestamp();
        s += "\n\twallet=" + mWallet.toString();

        for(UcoinCertification certification : mCertifications) {
            certification.toString();
        }

        return s;
    }

    protected Identity(Parcel in) {
        mCurrencyId = in.readByte() == 0x00 ? null : in.readLong();
        mWalletId = in.readByte() == 0x00 ? null : in.readLong();
        mUid = in.readString();
        mSelf = in.readString();
        mTimestamp = in.readByte() == 0x00 ? null : in.readLong();
        mWallet = (UcoinWallet) in.readValue(UcoinWallet.class.getClassLoader());
        mCertifications = (UcoinCertifications) in.readValue(UcoinCertifications.class.getClassLoader());
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
        if (mWalletId == null) {
            dest.writeByte((byte) 0x00);
        } else {
            dest.writeByte((byte) 0x01);
            dest.writeLong(mWalletId);
        }
        dest.writeString(mUid);
        dest.writeString(mSelf);
        if (mTimestamp == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mTimestamp);
        }
        dest.writeValue(mWallet);
        dest.writeValue(mCertifications);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Identity> CREATOR = new Parcelable.Creator<Identity>() {
        @Override
        public Identity createFromParcel(Parcel in) {
            return new Identity(in);
        }

        @Override
        public Identity[] newArray(int size) {
            return new Identity[size];
        }
    };
}