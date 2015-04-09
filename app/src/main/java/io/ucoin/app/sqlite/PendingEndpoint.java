package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinPendingEndpoint;

public class PendingEndpoint extends SQLiteEntity
        implements UcoinPendingEndpoint {

    @SuppressWarnings("unused")
    public static final Creator<PendingEndpoint> CREATOR = new Creator<PendingEndpoint>() {
        @Override
        public PendingEndpoint createFromParcel(Parcel in) {
            return new PendingEndpoint(in);
        }

        @Override
        public PendingEndpoint[] newArray(int size) {
            return new PendingEndpoint[size];
        }
    };

    private String mAddress;
    private int mPort;

    public PendingEndpoint(Context context, Long endpointId) {
        super(context, Provider.ENDPOINT_PENDING_URI, endpointId);
    }

    protected PendingEndpoint(Parcel in) {
        mAddress = in.readString();
        mPort = in.readInt();
    }

    @Override
    public String address() {
        return (this.mId == null) ? mAddress : getString(SQLiteTable.PendingEndpoint.ADDRESS);
    }

    @Override
    public Integer port() {
        return (this.mId == null) ? mPort : getInt(SQLiteTable.PendingEndpoint.PORT);
    }

    @Override
    public String toString() {
        return "ENDPOINT id=" + id() + "\n" +
                "address=" + address() + "\n" +
                "port=" + port();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAddress);
        dest.writeInt(mPort);
    }
}