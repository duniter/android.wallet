package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinCertification;
import io.ucoin.app.model.UcoinMember;
import io.ucoin.app.model.enums.CertificationType;

public class Certification extends SQLiteEntity
        implements UcoinCertification {

    private Long mIdentityId;
    private Long mMemberId;
    private CertificationType mType;
    private Long mBlock;
    private Long mMedianTime;
    public String mSignature;
    private UcoinMember mMember;

    public Certification(Context context, Long certificationId) {
        super(context, Provider.CERTIFICATION_URI, certificationId);
        mMember = new Member(context, memberId());
    }

    public Certification(Long identityId, Long  memberId, CertificationType type,
                         Long block, Long medianTime, String signature) {

        mIdentityId = identityId;
        mMemberId = memberId;
        mType =type;
        mBlock = block;
        mMedianTime = medianTime;
        mSignature = signature;
    }

    @Override
    public Long identityId() {
        return (this.mId == null) ? mIdentityId : getLong(Contract.Certification.IDENTITY_ID);
    }

    @Override
    public Long memberId() {
        return (this.mId == null) ? mMemberId : getLong(Contract.Certification.MEMBER_ID);
    }

    @Override
    public Long block() {
        return (this.mId == null) ? mBlock : getLong(Contract.Certification.BLOCK);
    }

    @Override
    public Long medianTime() {
        return (this.mId == null) ? mMedianTime : getLong(Contract.Certification.MEDIAN_TIME);
    }

    @Override
    public String signature() {
        return (this.mId == null) ? mSignature : getString(Contract.Certification.SIGNATURE);
    }

    @Override
    public UcoinMember member() {
        return mMember;
    }

    @Override
    public CertificationType type() {
        return (this.mId == null) ? mType : CertificationType.valueOf(getString(Contract.Certification.TYPE));
    }

    @Override
    public String toString() {
        String s = "\nCERTIFICATION id=" + ((id() == null) ? "not in database" : id()) + "\n";
        s += "\nidentityId=" + identityId();
        s += "\nmemberId=" + memberId();
        s += "\ntype=" + type().name();
        s += "\nblock=" + block();
        s += "\nmedianTime=" + medianTime();
        s += "\nsignature=" + signature();
        s += "\nmember=" + mMember.toString();

        return s;
    }

    protected Certification(Parcel in) {
        mIdentityId = in.readByte() == 0x00 ? null : in.readLong();
        mMemberId = in.readByte() == 0x00 ? null : in.readLong();
        mType = (CertificationType) in.readValue(CertificationType.class.getClassLoader());
        mBlock = in.readByte() == 0x00 ? null : in.readLong();
        mMedianTime = in.readByte() == 0x00 ? null : in.readLong();
        mSignature = in.readString();
        mMember = (UcoinMember) in.readValue(UcoinMember.class.getClassLoader());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mIdentityId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mIdentityId);
        }
        if (mMemberId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mMemberId);
        }
        dest.writeValue(mType);
        if (mBlock == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mBlock);
        }
        if (mMedianTime == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mMedianTime);
        }
        dest.writeString(mSignature);
        dest.writeValue(mMember);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Certification> CREATOR = new Parcelable.Creator<Certification>() {
        @Override
        public Certification createFromParcel(Parcel in) {
            return new Certification(in);
        }

        @Override
        public Certification[] newArray(int size) {
            return new Certification[size];
        }
    };
}