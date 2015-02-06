package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinSource;
import io.ucoin.app.model.enums.SourceType;

public class Source extends SQLiteEntity
        implements UcoinSource {

    private Long mWalletId;
    private Integer mNumber;
    private SourceType mType;
    private String mFingerprint;
    private Long mAMount;

    public Source(Context context, Long sourceId) {
        super(context, Provider.SOURCE_URI, sourceId);
    }

    public Source(Long walletId, Integer number, SourceType type,
                  String fingerprint, Long amount) {
        mWalletId = walletId;
        mNumber = number;
        mType = type;
        mFingerprint = fingerprint;
        mAMount = amount;
    }


    public Source(Long walletId, UcoinSource source) {
        mWalletId = walletId;
        mNumber = source.number();
        mType = source.type();
        mFingerprint = source.fingerprint();
        mAMount = source.amount();
    }

    @Override
    public Long walletId() {
        return (this.mId == null) ? mWalletId : getLong(Contract.Source.WALLET_ID);
    }

    @Override
    public Integer number() {
        return (this.mId == null) ? mNumber : getInt(Contract.Source.NUMBER);
    }
    @Override
    public SourceType type() {
        return (this.mId == null) ? mType : SourceType.valueOf(getString(Contract.Source.TYPE));
    }

    @Override
    public String fingerprint() {
        return (this.mId == null) ? mFingerprint : getString(Contract.Source.FINGERPRINT);
    }
    @Override
    public Long amount() {
        return (this.mId == null) ? mAMount : getLong(Contract.Source.AMOUNT);
    }

    @Override
    public String toString() {
        return "SOURCE id=" + ((id() == null) ? "not in database" : id()) + "\n" +
                "wallet_id=" + mWalletId + "\n" +
                "number=" + number() + "\n" +
                "type=" + type().toString() + "\n" +
                "fingerprint=" + fingerprint() + "\n" +
                "amount=" + amount();
    }

    protected Source(Parcel in) {
        mWalletId = in.readByte() == 0x00 ? null : in.readLong();
        mNumber = in.readByte() == 0x00 ? null : in.readInt();
        mType = (SourceType) in.readValue(SourceType.class.getClassLoader());
        mFingerprint = in.readString();
        mAMount = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mWalletId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mWalletId);
        }
        if (mNumber == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mNumber);
        }
        dest.writeValue(mType);
        dest.writeString(mFingerprint);
        if (mAMount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mAMount);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Source> CREATOR = new Parcelable.Creator<Source>() {
        @Override
        public Source createFromParcel(Parcel in) {
            return new Source(in);
        }

        @Override
        public Source[] newArray(int size) {
            return new Source[size];
        }
    };
}