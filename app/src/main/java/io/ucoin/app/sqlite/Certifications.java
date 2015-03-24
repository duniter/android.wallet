package io.ucoin.app.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinCertifications;
import io.ucoin.app.model.UcoinCertification;
import io.ucoin.app.model.UcoinMember;
import io.ucoin.app.model.enums.CertificationType;

final public class Certifications extends SQLiteEntities
        implements UcoinCertifications {

    private Long mIdentityId;
    private ArrayList<UcoinCertification> mCertificationArray;

    public Certifications(Context context, Long identityId) {
        super(context, Provider.CERTIFICATION_URI);
        mIdentityId = identityId;
    }

    @Override
    public UcoinCertification newCertification(UcoinMember member,
                                               CertificationType type,
                                               Long block,
                                               Long medianTime,
                                               String signature) {
        return new Certification(mIdentityId, member.id(), type, block, medianTime, signature);
    }


    @Override
    public UcoinCertification add(UcoinCertification certification) {

        if (mContext != null) {
            ContentValues values = new ContentValues();
            values.put(Contract.Certification.IDENTITY_ID, certification.identityId());
            values.put(Contract.Certification.MEMBER_ID, certification.memberId());
            values.put(Contract.Certification.TYPE, certification.type().name());
            values.put(Contract.Certification.BLOCK, certification.block());
            values.put(Contract.Certification.MEDIAN_TIME, certification.medianTime());
            values.put(Contract.Certification.SIGNATURE, certification.signature());

            Uri uri = mContext.getContentResolver().insert(mUri, values);
            return getById(Long.parseLong(uri.getLastPathSegment()));
        } else {
            mCertificationArray.add(certification);
            return certification;
        }
    }

    @Override
    public int delete(Long id) {
        return 0;
    }

    @Override
    public UcoinCertification getById(Long id) {
        return new Certification(mContext, id);
    }

    @Override
    public UcoinCertification getBySignature(String signature) {

        Long id = queryUnique(
                Contract.Certification.SIGNATURE + "=?",
                new String[]{signature});

        if (id == null)
            return null;

        return new Certification(mContext, id);
    }

    @Override
    public Iterator<UcoinCertification> iterator() {
        if (mContext == null) {
            return mCertificationArray.iterator();
        }
        String selection = Contract.Certification.IDENTITY_ID + "=?";
        String[] selectionArgs = new String[]{mIdentityId.toString()};
        final Cursor certificationsCursor = mContext.getContentResolver().query(mUri, null,
                selection, selectionArgs, null);

        return new Iterator<UcoinCertification>() {
            @Override
            public boolean hasNext() {
                if (certificationsCursor.moveToNext())
                    return true;
                else {
                    certificationsCursor.close();
                    return false;
                }
            }

            @Override
            public UcoinCertification next() {
                Long id = certificationsCursor.getLong(certificationsCursor.getColumnIndex(Contract.Member._ID));
                return new Certification(mContext, id);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    protected Certifications(Parcel in) {
        mIdentityId = in.readByte() == 0x00 ? null : in.readLong();
        if (in.readByte() == 0x01) {
            mCertificationArray = new ArrayList<>();
            in.readList(mCertificationArray, UcoinCertification.class.getClassLoader());
        } else {
            mCertificationArray = null;
        }
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
        if (mCertificationArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mCertificationArray);
        }
    }

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
}