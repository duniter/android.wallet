package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinMember;
import io.ucoin.app.model.enums.CertificationType;

public class Member extends SQLiteEntity
        implements UcoinMember, Parcelable {

    private Long mCurrencyId;
    private String mUid;
    private String mPublicKey;
    private Boolean mIsMember;
    private Boolean mWasMember;
    private String mSelf;
    private Long mTimestamp;

    public Member(Context context, Long memberId) {
        super(context, Provider.MEMBER_URI, memberId);
    }

    public Member(Long currencyId, String uid, String publicKey, Boolean isMember,
                  Boolean wasMember, String self, Long timestamp) {
        mCurrencyId = currencyId;
        mUid = uid;
        mPublicKey = publicKey;
        mIsMember = isMember;
        mWasMember = wasMember;
        mSelf = self;
        mTimestamp = timestamp;
    }

    public Member(Long currencyId, UcoinMember member) {
        mCurrencyId = currencyId;
        mUid = member.uid();
        mPublicKey = member.publicKey();
        mIsMember = member.isMember();
        mWasMember = member.wasMember();
        mSelf = member.self();
        mTimestamp = member.timestamp();
    }

    @Override
    public Long currencyId() {
        return (this.mId == null) ? mCurrencyId : getLong(Contract.Member.CURRENCY_ID);
    }

    @Override
    public String uid() {
        return (this.mId == null) ? mUid : getString(Contract.Member.UID);
    }

    @Override
    public String publicKey() {
        return (this.mId == null) ? mPublicKey : getString(Contract.Member.PUBLIC_KEY);
    }

    @Override
    public Boolean isMember() {
        return (this.mId == null) ? mIsMember : getBoolean(Contract.Member.IS_MEMBER);
    }

    @Override
    public Boolean wasMember() {
        return (this.mId == null) ? mWasMember : getBoolean(Contract.Member.WAS_MEMBER);
    }

    @Override
    public String self() {
        return (this.mId == null) ? mSelf : getString(Contract.Member.SELF);
    }

    @Override
    public Long timestamp() {
        return (this.mId == null) ? mTimestamp : getLong(Contract.Member.TIMESTAMP);
    }

    @Override
    public void isMember(Boolean is) {
        if(this.mId == null) {
            mIsMember = is;
        } else {
            setBoolean(Contract.Member.IS_MEMBER, is);
        }
    }

    @Override
    public void wasMember(Boolean was) {
        if(this.mId == null) {
            mWasMember = was;
        } else {
            setBoolean(Contract.Member.WAS_MEMBER, was);
        }
    }


    @Override
    public String toString() {
        String s = "MEMBER id=" + ((id() == null) ? "not in database" : id()) + "\n" ;
        s += "\ncurrencyId=" + currencyId();
        s += "\nuid=" + uid();
        s += "\npublicKey=" + publicKey();
        s += "\nisMember=" + isMember();
        s += "\nwasMember=" + wasMember();
        s += "\nself=" + self();
        s += "\ntimestamp=" + timestamp();

        return s;
    }

    protected Member(Parcel in) {
        mCurrencyId = in.readByte() == 0x00 ? null : in.readLong();
        mUid = in.readString();
        mPublicKey = in.readString();
        byte mIsMemberVal = in.readByte();
        mIsMember = mIsMemberVal == 0x02 ? null : mIsMemberVal != 0x00;
        byte mWasMemberVal = in.readByte();
        mWasMember = mWasMemberVal == 0x02 ? null : mWasMemberVal != 0x00;
        mSelf = in.readString();
        mTimestamp = in.readByte() == 0x00 ? null : in.readLong();
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
        dest.writeString(mUid);
        dest.writeString(mPublicKey);
        if (mIsMember == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (mIsMember ? 0x01 : 0x00));
        }
        if (mWasMember == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (mWasMember ? 0x01 : 0x00));
        }
        dest.writeString(mSelf);
        if (mTimestamp == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mTimestamp);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Member> CREATOR = new Parcelable.Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };
}