package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinIdentities;
import io.ucoin.app.model.UcoinIdentity;
import io.ucoin.app.model.UcoinMember;
import io.ucoin.app.model.UcoinMembers;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.UcoinWallets;
import io.ucoin.app.model.UcoinPeer;
import io.ucoin.app.model.UcoinPeers;
import io.ucoin.app.model.http_api.BlockchainBlock;
import io.ucoin.app.model.http_api.BlockchainParameter;

public class Currency extends SQLiteEntity
        implements UcoinCurrency {

    private String mCurrencyName;

    private Long mIdentityId;

    private Float mC;
    private Integer mDt;
    private Integer mUd0;
    private Integer mSigDelay;
    private Integer mSigValidity;
    private Integer mSigQty;
    private Integer mSigWoT;
    private Integer mMsValidity;
    private Integer mStepMax;
    private Integer mMedianTimeBlocks;
    private Integer mAvgGenTime;
    private Integer mDtDiffEval;
    private Integer mBlocksRot;
    private Float mPercentRot;

    private Integer mMembersCount;
    private String mFirstBlockSignature;

    private UcoinIdentity mIdentity;
    private UcoinWallet mWallet;
    private UcoinIdentities mIdentities;
    private UcoinWallets mWallets;
    private UcoinPeers mPeers;
    private UcoinMembers mMembers;

    public Currency(Context context, Long id) {
        super(context, Provider.CURRENCY_URI, id);
        if (identityId() != 0) {
            mIdentity = new Identity(context, identityId());
        }

        mIdentities = new Identities(context, id);
        mWallets = new Wallets(context, id);
        mPeers = new Peers(context, id);
        mMembers = new Members(context, id);
    }

    public Currency(BlockchainParameter parameter, BlockchainBlock firstBlock,
                    BlockchainBlock lastBlock) {
        mCurrencyName = parameter.currency;
        mC = parameter.c;
        mDt = parameter.dt;
        mUd0 = parameter.ud0;
        mSigDelay = parameter.sigDelay;
        mSigValidity = parameter.sigValidity;
        mSigQty = parameter.sigQty;
        mSigWoT = parameter.sigWoT;
        mMsValidity = parameter.msValidity;
        mStepMax = parameter.stepMax;
        mMedianTimeBlocks = parameter.medianTimeBlocks;
        mAvgGenTime = parameter.avgGenTime;
        mDtDiffEval = parameter.dtDiffEval;
        mBlocksRot = parameter.blocksRot;
        mPercentRot = parameter.percentRot;

        mFirstBlockSignature = firstBlock.getSignature();
        mMembersCount = lastBlock.getMembersCount();

        mIdentity = null;
        mWallet = null;
        mIdentities = new Identities(mId, null);
        mWallets = new Wallets(mId, null);
        mPeers = new Peers(mId, null);
        mMembers = new Members(mId, null);
    }

    @Override
    public Long identityId() {
        return (this.mId == null) ? mIdentityId : getLong(Contract.Currency.IDENTITY_ID);
    }

    @Override
    public String currencyName() {
        return (this.mId == null) ? mCurrencyName : getString(Contract.Currency.CURRENCY_NAME);
    }

    @Override
    public Float c() {
        return (this.mId == null) ? mC : getFloat(Contract.Currency.C);
    }

    @Override
    public Integer dt() {
        return (this.mId == null) ? mDt : getInt(Contract.Currency.DT);
    }

    @Override
    public Integer ud0() {
        return (this.mId == null) ? mUd0 : getInt(Contract.Currency.UD0);
    }

    @Override
    public Integer sigDelay() {
        return (this.mId == null) ? mSigDelay : getInt(Contract.Currency.SIGDELAY);
    }

    @Override
    public Integer sigValidity() {
        return (this.mId == null) ? mSigValidity : getInt(Contract.Currency.SIGVALIDITY);
    }

    @Override
    public Integer sigQty() {
        return (this.mId == null) ? mSigQty : getInt(Contract.Currency.SIGQTY);
    }

    @Override
    public Integer sigWoT() {
        return (this.mId == null) ? mSigWoT : getInt(Contract.Currency.SIGWOT);
    }

    @Override
    public Integer msValidity() {
        return (this.mId == null) ? mMsValidity : getInt(Contract.Currency.MSVALIDITY);
    }

    @Override
    public Integer stepMax() {
        return (this.mId == null) ? mStepMax : getInt(Contract.Currency.STEPMAX);
    }

    @Override
    public Integer medianTimeBlocks() {
        return (this.mId == null) ? mMedianTimeBlocks : getInt(Contract.Currency.MEDIANTIMEBLOCKS);
    }

    @Override
    public Integer avgGenTime() {
        return (this.mId == null) ? mAvgGenTime : getInt(Contract.Currency.AVGGENTIME);
    }

    @Override
    public Integer dtDiffEval() {
        return (this.mId == null) ? mDtDiffEval : getInt(Contract.Currency.DTDIFFEVAL);
    }

    @Override
    public Integer blocksRot() {
        return (this.mId == null) ? mBlocksRot : getInt(Contract.Currency.BLOCKSROT);
    }

    @Override
    public Float percentRot() {
        return (this.mId == null) ? mPercentRot : getFloat(Contract.Currency.PERCENTROT);
    }

    @Override
    public Long membersCount() {
        return (this.mId == null) ? mMembersCount : getLong(Contract.Currency.MEMBERS_COUNT);
    }

    @Override
    public String firstBlockSignature() {
        return (this.mId == null) ? mFirstBlockSignature : getString(Contract.Currency.FIRST_BLOCK_SIGNATURE);
    }

    @Override
    public UcoinIdentity identity() {
        return mIdentity;
    }

    @Override
    public UcoinPeers peers() {
        return mPeers;
    }

    @Override
    public UcoinMembers members() {
        return mMembers;
    }

    @Override
    public void identityId(Long id) {
        if (this.mId == null) {
            mIdentityId = id;
        } else {
            setLong(Contract.Currency.IDENTITY_ID, id);
        }
    }

    @Override
    public UcoinIdentity newIdentity(Long walletId, String uid) {
        return mIdentities.newIdentity(walletId, uid);
    }

    @Override
    public UcoinIdentity setIdentity(UcoinIdentity identity) {
        return mIdentities.add(identity);
    }

    @Override
    public UcoinWallets wallets() {
        return mWallets;
    }

    @Override
    public String toString() {
        String s = "CURRENCY id=" + ((id() == null) ? "not in database" : id()) + "\n";
        s += "\ncurrency_name=" + currencyName();
        s += "\nc=" + c();
        s += "\ndt=" + dt();
        s += "\nud0=" + ud0();
        s += "\nsigDelay=" + sigDelay();
        s += "\nsigValidity=" + sigValidity();
        s += "\nsigQty=" + sigQty();
        s += "\nsigWoT=" + sigWoT();
        s += "\nmsValidity=" + msValidity();
        s += "\nstepMax=" + stepMax();
        s += "\nmedianTimeBlocks=" + medianTimeBlocks();
        s += "\navgGenTime=" + avgGenTime();
        s += "\ndtDiffEval=" + dtDiffEval();
        s += "\nblocksRot=" + blocksRot();
        s += "\npercentRot=" + percentRot();
        s += "\nmembers_count=" + membersCount();
        s += "\nfirst_block_signature=" + firstBlockSignature();
        s += "\nidentityId=" + identityId();
        s += "\nidentity=" + identity();

        for (UcoinWallet wallet : mWallets) {
            s += "\n\t" + wallet.toString();
        }

        for (UcoinPeer peer : mPeers) {
            s += "\n\t" + peer.toString();
        }

        for (UcoinMember member : mMembers) {
            s += "\n\t" + member.toString();
        }
        return s;
    }

    protected Currency(Parcel in) {
        mIdentityId = in.readByte() == 0x00 ? null : in.readLong();
        mCurrencyName = in.readString();
        mC = in.readByte() == 0x00 ? null : in.readFloat();
        mDt = in.readByte() == 0x00 ? null : in.readInt();
        mUd0 = in.readByte() == 0x00 ? null : in.readInt();
        mSigDelay = in.readByte() == 0x00 ? null : in.readInt();
        mSigValidity = in.readByte() == 0x00 ? null : in.readInt();
        mSigQty = in.readByte() == 0x00 ? null : in.readInt();
        mSigWoT = in.readByte() == 0x00 ? null : in.readInt();
        mMsValidity = in.readByte() == 0x00 ? null : in.readInt();
        mStepMax = in.readByte() == 0x00 ? null : in.readInt();
        mMedianTimeBlocks = in.readByte() == 0x00 ? null : in.readInt();
        mAvgGenTime = in.readByte() == 0x00 ? null : in.readInt();
        mDtDiffEval = in.readByte() == 0x00 ? null : in.readInt();
        mBlocksRot = in.readByte() == 0x00 ? null : in.readInt();
        mPercentRot = in.readByte() == 0x00 ? null : in.readFloat();
        mMembersCount = in.readByte() == 0x00 ? null : in.readInt();
        mFirstBlockSignature = in.readString();
        mIdentity = (UcoinIdentity) in.readValue(UcoinIdentity.class.getClassLoader());
        mWallet = (UcoinWallet) in.readValue(UcoinWallet.class.getClassLoader());
        mIdentities = (UcoinIdentities) in.readValue(UcoinIdentities.class.getClassLoader());
        mWallets = (UcoinWallets) in.readValue(UcoinWallets.class.getClassLoader());
        mPeers = (UcoinPeers) in.readValue(UcoinPeers.class.getClassLoader());
        mMembers = (UcoinMembers) in.readValue(UcoinMembers.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mIdentityId == null) {
            dest.writeByte((byte) 0x00);
        } else {
            dest.writeByte((byte) 0x01);
            dest.writeLong(mIdentityId);
        }
        dest.writeString(mCurrencyName);
        if (mC == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(mC);
        }
        if (mDt == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mDt);
        }
        if (mUd0 == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mUd0);
        }
        if (mSigDelay == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mSigDelay);
        }
        if (mSigValidity == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mSigValidity);
        }
        if (mSigQty == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mSigQty);
        }
        if (mSigWoT == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mSigWoT);
        }
        if (mMsValidity == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mMsValidity);
        }
        if (mStepMax == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mStepMax);
        }
        if (mMedianTimeBlocks == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mMedianTimeBlocks);
        }
        if (mAvgGenTime == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mAvgGenTime);
        }
        if (mDtDiffEval == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mDtDiffEval);
        }
        if (mBlocksRot == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mBlocksRot);
        }
        if (mPercentRot == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(mPercentRot);
        }
        if (mMembersCount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(mMembersCount);
        }
        dest.writeString(mFirstBlockSignature);

        dest.writeValue(mIdentity);
        dest.writeValue(mWallet);

        dest.writeValue(mIdentities);
        dest.writeValue(mWallets);
        dest.writeValue(mPeers);
        dest.writeValue(mMembers);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Currency> CREATOR = new Parcelable.Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };
}