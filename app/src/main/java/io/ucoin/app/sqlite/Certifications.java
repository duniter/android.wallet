package io.ucoin.app.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.content.Provider;
import io.ucoin.app.enums.CertificationType;
import io.ucoin.app.model.UcoinCertification;
import io.ucoin.app.model.UcoinCertifications;
import io.ucoin.app.model.UcoinMember;
import io.ucoin.app.model.http_api.WotCertification;

final public class Certifications extends SQLiteEntities
        implements UcoinCertifications {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Certifications> CREATOR = new Parcelable.Creator<Certifications>() {
        @Override
        public Certifications createFromParcel(Parcel in) {
            return new Certifications(in);
        }

        @Override
        public Certifications[] newArray(int size) {
            return new Certifications[size];
        }
    };
    private Long mIdentityId;

    public Certifications(Context context, Long identityId) {
        this(context, identityId, SQLiteTable.Certification.IDENTITY_ID + "=?", new String[]{identityId.toString()});
    }

    private Certifications(Context context, Long identityId, String selection, String[] selectionArgs) {
        this(context, identityId, selection, selectionArgs, null);
    }

    private Certifications(Context context, Long identityId, String selection, String[] selectionArgs, String sortOrder) {
        super(context, Provider.CERTIFICATION_URI, selection, selectionArgs, sortOrder);
        mIdentityId = identityId;
    }


    protected Certifications(Parcel in) {
        mIdentityId = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public UcoinCertification add(UcoinMember member, CertificationType type, WotCertification.Certification certification) {
        ContentValues values = new ContentValues();
        values.put(SQLiteTable.Certification.IDENTITY_ID, mIdentityId);
        values.put(SQLiteTable.Certification.MEMBER_ID, member.id());
        values.put(SQLiteTable.Certification.TYPE, type.name());
        values.put(SQLiteTable.Certification.BLOCK, certification.cert_time.block);
        values.put(SQLiteTable.Certification.MEDIAN_TIME, certification.cert_time.medianTime);
        values.put(SQLiteTable.Certification.SIGNATURE, certification.signature);

        Uri uri = mContext.getContentResolver().insert(mUri, values);
        return new Certification(mContext, Long.parseLong(uri.getLastPathSegment()));
    }

    @Override
    public UcoinCertification getById(Long id) {
        return new Certification(mContext, id);
    }

    @Override
    public UcoinCertification getBySignature(String signature) {

        Long id = queryUnique(
                SQLiteTable.Certification.SIGNATURE + "=?",
                new String[]{signature});

        if (id == null)
            return null;

        return new Certification(mContext, id);
    }

    @Override
    public UcoinCertifications getByType(CertificationType type) {
        String selection = SQLiteTable.Certification.IDENTITY_ID + "=? AND " +
                SQLiteTable.Certification.TYPE + "=?";
        String[] selectionArgs = new String[]{mIdentityId.toString(), type.name()};
        return new Certifications(mContext, mIdentityId, selection, selectionArgs);
    }

    @Override
    public Iterator<UcoinCertification> iterator() {
        Cursor cursor = fetch();
        ArrayList<UcoinCertification> data = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            data.add(new Certification(mContext, id));
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
        if (mIdentityId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mIdentityId);
        }
    }
}