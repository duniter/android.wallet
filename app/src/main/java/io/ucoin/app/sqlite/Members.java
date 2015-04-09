package io.ucoin.app.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinMember;
import io.ucoin.app.model.UcoinMembers;

public class Members extends SQLiteEntities
        implements UcoinMembers {

    @SuppressWarnings("unused")
    public static final Creator<Members> CREATOR = new Creator<Members>() {
        @Override
        public Members createFromParcel(Parcel in) {
            return new Members(in);
        }

        @Override
        public Members[] newArray(int size) {
            return new Members[size];
        }
    };
    private Long mCurrencyId;

    public Members(Context context, Long currencyId) {
        this(context, currencyId, SQLiteTable.Member.CURRENCY_ID + "=?", new String[]{currencyId.toString()});
    }

    private Members(Context context, Long currencyId, String selection, String[] selectionArgs) {
        this(context, currencyId, selection, selectionArgs, null);
    }

    private Members(Context context, Long currencyId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, Provider.MEMBER_URI, selection, selectionArgs, sortOrder);
        mCurrencyId = currencyId;
    }

    protected Members(Parcel in) {
        mCurrencyId = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public UcoinMember newMember(String uid,
                                 String publicKey,
                                 Boolean isMember,
                                 Boolean wasMember,
                                 String self,
                                 Long timestamp) {
        return new Member(mCurrencyId, uid, publicKey, isMember, wasMember, self, timestamp);
    }

    @Override
    public UcoinMember add(UcoinMember member) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Member.CURRENCY_ID, member.currencyId());
        values.put(SQLiteTable.Member.UID, member.uid());
        values.put(SQLiteTable.Member.PUBLIC_KEY, member.publicKey());
        values.put(SQLiteTable.Member.IS_MEMBER, member.isMember());
        values.put(SQLiteTable.Member.WAS_MEMBER, member.wasMember());
        values.put(SQLiteTable.Member.SELF, member.self());
        values.put(SQLiteTable.Member.TIMESTAMP, member.timestamp());

        Uri uri = mContext.getContentResolver().insert(mUri, values);
        return new Member(mContext, Long.parseLong(uri.getLastPathSegment()));
    }

    @Override
    public UcoinMember getById(Long id) {
        return new Member(mContext, id);
    }

    @Override
    public UcoinMember getByUid(String uid) {
        String selection = SQLiteTable.Member.CURRENCY_ID + "=? AND " + SQLiteTable.Member.UID + " LIKE ?";
        String[] selectionArgs = new String[]{mCurrencyId.toString(), uid};
        UcoinMembers members = new Members(mContext, mCurrencyId, selection, selectionArgs);
        if (members.iterator().hasNext()) {
            return members.iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public UcoinMember getByPublicKey(String publicKey) {
        String selection = SQLiteTable.Member.CURRENCY_ID + "=? AND " + SQLiteTable.Member.PUBLIC_KEY + " LIKE ?";
        String[] selectionArgs = new String[]{mCurrencyId.toString(), publicKey};
        UcoinMembers members = new Members(mContext, mCurrencyId, selection, selectionArgs);
        if (members.iterator().hasNext()) {
            return members.iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public Iterator<UcoinMember> iterator() {
        Cursor cursor = fetch();
        ArrayList<UcoinMember> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            data.add(new Member(mContext, id));
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