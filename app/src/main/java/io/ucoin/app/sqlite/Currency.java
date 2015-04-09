package io.ucoin.app.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinBlocks;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinIdentities;
import io.ucoin.app.model.UcoinIdentity;
import io.ucoin.app.model.UcoinMembers;
import io.ucoin.app.model.UcoinPeers;
import io.ucoin.app.model.UcoinWallets;

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

    public Currency(Context context, Long id) {
        this(context, id, false);
    }

    public Currency(Context context, Long id, boolean cacheResult) {
        super(context, Provider.CURRENCY_URI, id);
        if(cacheResult)
            cache();
    }

    public void cache() {
        Cursor c = fetch();
        mCurrencyName = c.getString(c.getColumnIndex(SQLiteTable.Currency.IDENTITY_ID));
        mC = c.getFloat(c.getColumnIndex(SQLiteTable.Currency.C));
        mDt = c.getInt(c.getColumnIndex(SQLiteTable.Currency.DT));
        mUd0 = c.getInt(c.getColumnIndex(SQLiteTable.Currency.UD0));
        mSigDelay = c.getInt(c.getColumnIndex(SQLiteTable.Currency.SIGDELAY));
        mSigValidity = c.getInt(c.getColumnIndex(SQLiteTable.Currency.SIGVALIDITY));
        mSigQty = c.getInt(c.getColumnIndex(SQLiteTable.Currency.SIGQTY));
        mSigWoT = c.getInt(c.getColumnIndex(SQLiteTable.Currency.SIGWOT));
        mMsValidity = c.getInt(c.getColumnIndex(SQLiteTable.Currency.MSVALIDITY));
        mStepMax = c.getInt(c.getColumnIndex(SQLiteTable.Currency.STEPMAX));
        mMedianTimeBlocks = c.getInt(c.getColumnIndex(SQLiteTable.Currency.MEDIANTIMEBLOCKS));
        mAvgGenTime = c.getInt(c.getColumnIndex(SQLiteTable.Currency.AVGGENTIME));
        mDtDiffEval = c.getInt(c.getColumnIndex(SQLiteTable.Currency.DTDIFFEVAL));
        mBlocksRot = c.getInt(c.getColumnIndex(SQLiteTable.Currency.BLOCKSROT));
        mPercentRot = c.getFloat(c.getColumnIndex(SQLiteTable.Currency.PERCENTROT));
        c.close();
        
        mIsCached = true;
    }

    @Override
    public Long identityId() {
        return (this.mIsCached) ? mIdentityId : getLong(SQLiteTable.Currency.IDENTITY_ID);
    }

    @Override
    public String currencyName() {
        return (this.mIsCached) ? mCurrencyName : getString(SQLiteTable.Currency.CURRENCY_NAME);
    }

    @Override
    public Float c() {
        return (this.mIsCached) ? mC : getFloat(SQLiteTable.Currency.C);
    }

    @Override
    public Integer dt() {
        return (this.mIsCached) ? mDt : getInt(SQLiteTable.Currency.DT);
    }

    @Override
    public Integer ud0() {
        return (this.mIsCached) ? mUd0 : getInt(SQLiteTable.Currency.UD0);
    }

    @Override
    public Integer sigDelay() {
        return (this.mIsCached) ? mSigDelay : getInt(SQLiteTable.Currency.SIGDELAY);
    }

    @Override
    public Integer sigValidity() {
        return (this.mIsCached) ? mSigValidity : getInt(SQLiteTable.Currency.SIGVALIDITY);
    }

    @Override
    public Integer sigQty() {
        return (this.mIsCached) ? mSigQty : getInt(SQLiteTable.Currency.SIGQTY);
    }

    @Override
    public Integer sigWoT() {
        return (this.mIsCached) ? mSigWoT : getInt(SQLiteTable.Currency.SIGWOT);
    }

    @Override
    public Integer msValidity() {
        return (this.mIsCached) ? mMsValidity : getInt(SQLiteTable.Currency.MSVALIDITY);
    }

    @Override
    public Integer stepMax() {
        return (this.mIsCached) ? mStepMax : getInt(SQLiteTable.Currency.STEPMAX);
    }

    @Override
    public Integer medianTimeBlocks() {
        return (this.mIsCached) ? mMedianTimeBlocks : getInt(SQLiteTable.Currency.MEDIANTIMEBLOCKS);
    }

    @Override
    public Integer avgGenTime() {
        return (this.mIsCached) ? mAvgGenTime : getInt(SQLiteTable.Currency.AVGGENTIME);
    }

    @Override
    public Integer dtDiffEval() {
        return (this.mIsCached) ? mDtDiffEval : getInt(SQLiteTable.Currency.DTDIFFEVAL);
    }

    @Override
    public Integer blocksRot() {
        return (this.mIsCached) ? mBlocksRot : getInt(SQLiteTable.Currency.BLOCKSROT);
    }

    @Override
    public Float percentRot() {
        return (this.mIsCached) ? mPercentRot : getFloat(SQLiteTable.Currency.PERCENTROT);
    }

    @Override
    public UcoinIdentity identity() {
        if (identityId() != 0) {
            return new Identity(mContext, identityId());
        }
        return null;
    }

    @Override
    public UcoinPeers peers() {
        return new Peers(mContext, mId);
    }

    @Override
    public UcoinMembers members() {
        return new Members(mContext, mId);
    }

    @Override
    public UcoinBlocks blocks() {
        return new Blocks(mContext, mId);
    }

    @Override
    public void identityId(Long id) {
        if (this.mIsCached) {
            mIdentityId = id;
        } else {
            setLong(SQLiteTable.Currency.IDENTITY_ID, id);
        }
    }

    @Override
    public UcoinIdentity newIdentity(Long walletId, String uid) {
        UcoinIdentities ids = new Identities(mContext, mId);
        return ids.newIdentity(walletId, uid);
    }

    @Override
    public UcoinIdentity setIdentity(UcoinIdentity identity) {
        UcoinIdentities ids = new Identities(mContext, mId);
        return ids.add(identity);
    }

    @Override
    public UcoinWallets wallets() {
        return new Wallets(mContext, mId);
    }

    @Override
    public String toString() {
        String s = "CURRENCY id=" + id() + "\n";
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
        s += "\nidentityId=" + identityId();
        s += "\nidentity=" + identity();

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