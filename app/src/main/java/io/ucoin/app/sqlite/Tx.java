package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;

import io.ucoin.app.content.Provider;
import io.ucoin.app.enums.TxDirection;
import io.ucoin.app.model.UcoinTx;
import io.ucoin.app.model.UcoinTxInputs;
import io.ucoin.app.model.UcoinTxIssuers;
import io.ucoin.app.model.UcoinTxOutputs;
import io.ucoin.app.model.UcoinTxSignatures;

public class Tx extends SQLiteEntity
        implements UcoinTx {

    @SuppressWarnings("unused")
    public static final Creator<Tx> CREATOR = new Creator<Tx>() {
        @Override
        public Tx createFromParcel(Parcel in) {
            return new Tx(in);
        }

        @Override
        public Tx[] newArray(int size) {
            return new Tx[size];
        }
    };

    private Long mWalletId;
    private String mComment;
    private String mHash;
    private Long mBlock;
    private Long mTime;
    private TxDirection mTxDirection;

    public Tx(Context context, Long txId) {
        super(context, Provider.TX_URI, txId);
    }

    protected Tx(Parcel in) {
        mWalletId = in.readByte() == 0x00 ? null : in.readLong();
        mComment = in.readString();
        mHash = in.readString();
        mBlock = in.readByte() == 0x00 ? null : in.readLong();
        mTime = in.readByte() == 0x00 ? null : in.readLong();
        mTxDirection = (TxDirection) in.readValue(TxDirection.class.getClassLoader());
    }

    @Override
    public Long walletId() {
        return (this.mId == null) ? mWalletId : getLong(SQLiteTable.Tx.WALLET_ID);
    }

    @Override
    public String comment() {
        return (this.mId == null) ? mComment : getString(SQLiteTable.Tx.COMMENT);
    }

    @Override
    public String hash() {
        return (this.mId == null) ? mHash : getString(SQLiteTable.Tx.HASH);
    }

    @Override
    public Long block() {
        return (this.mId == null) ? mBlock : getLong(SQLiteTable.Tx.BLOCK);
    }

    @Override
    public Long time() {
        return (this.mId == null) ? mTime : getInt(SQLiteTable.Tx.TIME);
    }

    @Override
    public TxDirection direction() {
        return (this.mId == null) ? mTxDirection : TxDirection.valueOf(getString(SQLiteTable.Tx.DIRECTION));
    }


    @Override
    public UcoinTxIssuers issuers() {
        return new TxIssuers(mContext, mId);
    }

    @Override
    public UcoinTxInputs inputs() {
        return new TxInputs(mContext, mId);
    }

    @Override
    public UcoinTxOutputs outputs() {
        return new TxOutputs(mContext, mId);
    }

    @Override
    public UcoinTxSignatures signatures() {
        return new TxSignatures(mContext, mId);
    }

    @Override
    public String toString() {
        return "Tx id=" + id() + "\n" +
                "wallet_id=" + walletId() + "\n" +
                "comment=" + comment() + "\n" +
                "hash=" + hash() + "\n" +
                "block=" + block() + "\n" +
                "time=" + time();
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
        dest.writeValue(mComment);
        dest.writeString(mHash);
        if (mBlock == null) {
            dest.writeByte(((byte) 0x00));
        } else {
            dest.writeByte((byte) 0x01);
            dest.writeLong(mBlock);
        }
        if (mTime == null) {
            dest.writeByte(((byte) 0x00));
        } else {
            dest.writeByte((byte) 0x01);
            dest.writeLong(mTime);
        }
        dest.writeValue(mTxDirection);
    }
}