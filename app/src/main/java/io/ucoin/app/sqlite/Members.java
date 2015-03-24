package io.ucoin.app.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinMember;
import io.ucoin.app.model.UcoinMembers;

public class Members extends SQLiteEntities
        implements UcoinMembers {

    private Long mCurrencyId;
    private ArrayList<UcoinMember> mMembersArray;

    public Members(Context context, Long currencyId) {
        super(context, Provider.MEMBER_URI);
        mCurrencyId = currencyId;
    }

    public Members(Long currencyId, ArrayList<UcoinMember> members) {
        mCurrencyId = currencyId;
        if (members == null) {
            mMembersArray = new ArrayList<>();
        } else {
            mMembersArray = members;
        }
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
        if (mContext != null) {
            ContentValues values = new ContentValues();
            values.put(Contract.Member.CURRENCY_ID, member.currencyId());
            values.put(Contract.Member.UID, member.uid());
            values.put(Contract.Member.PUBLIC_KEY, member.publicKey());
            values.put(Contract.Member.IS_MEMBER, member.isMember());
            values.put(Contract.Member.WAS_MEMBER, member.wasMember());
            values.put(Contract.Member.SELF, member.self());
            values.put(Contract.Member.TIMESTAMP, member.timestamp());

            Uri uri = mContext.getContentResolver().insert(mUri, values);
            return getById(Long.parseLong(uri.getLastPathSegment()));
        } else {
            mMembersArray.add(member);
            return member;
        }
    }

    @Override
    public UcoinMember getById(Long id) {
        return new Member(mContext, id);
    }

    @Override
    public UcoinMember getByUid(String uid) {
        Long id = queryUnique(
                Contract.Member.CURRENCY_ID + "=? AND " + Contract.Member.UID + "=?",
                new String[]{
                        mCurrencyId.toString(),
                        uid
                });

        if (id == null)
            return null;

        return new Member(mContext, id);
    }

    @Override
    public UcoinMember getByPublicKey(String publicKey) {
        Long id = queryUnique(
                Contract.Member.CURRENCY_ID + "=? AND " + Contract.Member.PUBLIC_KEY + "=?",
                new String[]{
                        mCurrencyId.toString(),
                        publicKey
                });

        if (id == null)
            return null;

        return new Member(mContext, id);
    }

    @Override
    public Iterator<UcoinMember> iterator() {
        if (mContext == null) {
            return mMembersArray.iterator();
        }
        String selection = Contract.Member.CURRENCY_ID + "=?";
        String[] selectionArgs = new String[]{mCurrencyId.toString()};
        final Cursor membersCursor = mContext.getContentResolver().query(mUri, null,
                selection, selectionArgs, null);

        return new Iterator<UcoinMember>() {
            @Override
            public boolean hasNext() {
                if (membersCursor.moveToNext())
                    return true;
                else {
                    membersCursor.close();
                    return false;
                }
            }

            @Override
            public UcoinMember next() {
                Long id = membersCursor.getLong(membersCursor.getColumnIndex(Contract.Member._ID));
                return new Member(mContext, id);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    protected Members(Parcel in) {
        mCurrencyId = in.readByte() == 0x00 ? null : in.readLong();
        if (in.readByte() == 0x01) {
            mMembersArray = new ArrayList<>();
            in.readList(mMembersArray, UcoinMember.class.getClassLoader());
        } else {
            mMembersArray = null;
        }
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
        if (mMembersArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mMembersArray);
        }
    }

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
}