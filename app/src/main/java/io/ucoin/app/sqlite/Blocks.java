package io.ucoin.app.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinBlock;
import io.ucoin.app.model.UcoinBlocks;
import io.ucoin.app.model.http_api.BlockchainBlock;

final public class Blocks extends SQLiteEntities
        implements UcoinBlocks {

    @SuppressWarnings("unused")
    public static final Creator<Blocks> CREATOR = new Creator<Blocks>() {
        @Override
        public Blocks createFromParcel(Parcel in) {
            return new Blocks(in);
        }

        @Override
        public Blocks[] newArray(int size) {
            return new Blocks[size];
        }
    };
    private Long mCurrencyId;

    public Blocks(Context context, Long currencyId) {
        this(context, currencyId, SQLiteTable.Block.CURRENCY_ID + "=?", new String[]{currencyId.toString()});
    }

    public Blocks(Context context, Long currencyId, String selection, String[] selectionArgs) {
        this(context, currencyId, selection, selectionArgs, null);
    }

    public Blocks(Context context, Long currencyId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, Provider.BLOCK_URI, selection, selectionArgs, sortOrder);
        mCurrencyId = currencyId;
    }

    protected Blocks(Parcel in) {
        mCurrencyId = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public UcoinBlock add(BlockchainBlock blockchainBlock) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Block.CURRENCY_ID, mCurrencyId);
        values.put(SQLiteTable.Block.NUMBER, blockchainBlock.number);
        values.put(SQLiteTable.Block.MONETARY_MASS, blockchainBlock.monetaryMass);
        values.put(SQLiteTable.Block.SIGNATURE, blockchainBlock.signature);

        Uri uri = mContext.getContentResolver().insert(mUri, values);
        return new Block(mContext, Long.parseLong(uri.getLastPathSegment()));
    }

    @Override
    public UcoinBlock getById(Long id) {
        return new Block(mContext, id);
    }

    @Override
    public UcoinBlock firstBlock() {
        String selection = SQLiteTable.Block.CURRENCY_ID + "=?";
        String[] selectionArgs = new String[]{mCurrencyId.toString()};
        String sortOrder = SQLiteTable.Block.NUMBER + " ASC LIMIT 1";
        UcoinBlocks blocks = new Blocks(mContext, mCurrencyId, selection, selectionArgs, sortOrder);
        if (blocks.iterator().hasNext()) {
            return blocks.iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public UcoinBlock lastBlock() {
        String selection = SQLiteTable.Block.CURRENCY_ID + "=?";
        String[] selectionArgs = new String[]{mCurrencyId.toString()};
        String sortOrder = SQLiteTable.Block.NUMBER + " DESC LIMIT 1";
        UcoinBlocks blocks = new Blocks(mContext, mCurrencyId, selection, selectionArgs, sortOrder);
        if (blocks.iterator().hasNext()) {
            return blocks.iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public Iterator<UcoinBlock> iterator() {
        ArrayList<UcoinBlock> data = new ArrayList<>();
        final Cursor cursor = mContext.getContentResolver().query(mUri, null,
                mSelection, mSelectionArgs, mSortOrder);

        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(SQLiteTable.Block._ID));
            data.add(new Block(mContext, id));
        }
        cursor.close();

        return data.iterator();
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
    }
}