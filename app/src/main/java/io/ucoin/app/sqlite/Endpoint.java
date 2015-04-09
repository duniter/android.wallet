package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import io.ucoin.app.content.Provider;
import io.ucoin.app.enums.EndpointProtocol;
import io.ucoin.app.model.UcoinEndpoint;

public class Endpoint extends SQLiteEntity
        implements UcoinEndpoint {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Endpoint> CREATOR = new Parcelable.Creator<Endpoint>() {
        @Override
        public Endpoint createFromParcel(Parcel in) {
            return new Endpoint(in);
        }

        @Override
        public Endpoint[] newArray(int size) {
            return new Endpoint[size];
        }
    };
    private Long mPeerId;
    private EndpointProtocol mProtocol;
    private String mUrl;
    private String mIpv4;
    private String mIpv6;
    private int mPort;

    public Endpoint(Context context, Long endpointId) {
        super(context, Provider.ENDPOINT_URI, endpointId);
    }

    protected Endpoint(Parcel in) {
        mPeerId = in.readByte() == 0x00 ? null : in.readLong();
        mProtocol = (EndpointProtocol) in.readValue(EndpointProtocol.class.getClassLoader());
        mUrl = in.readString();
        mIpv4 = in.readString();
        mIpv6 = in.readString();
        mPort = in.readInt();
    }

    @Override
    public Long peerId() {
        return (this.mId == null) ? mPeerId : getLong(SQLiteTable.Endpoint.PEER_ID);
    }

    @Override
    public EndpointProtocol protocol() {
        return (this.mId == null) ? mProtocol : EndpointProtocol.valueOf(getString(SQLiteTable.Endpoint.PROTOCOL));
    }

    @Override
    public String ipv4() {
        return (this.mId == null) ? mIpv4 : getString(SQLiteTable.Endpoint.IPV4);
    }

    @Override
    public String ipv6() {
        return (this.mId == null) ? mIpv6 : getString(SQLiteTable.Endpoint.IPV6);
    }

    @Override
    public String url() {
        return (this.mId == null) ? mUrl : getString(SQLiteTable.Endpoint.URL);
    }

    @Override
    public Integer port() {
        return (this.mId == null) ? mPort : getInt(SQLiteTable.Endpoint.PORT);
    }

    @Override
    public String toString() {
        return "ENDPOINT id=" + ((id() == null) ? "not in database" : id()) + "\n" +
                "peer_id=" + peerId() + "\n" +
                "protocol=" + protocol() + "\n" +
                "url=" + url() + "\n" +
                "ipv4=" + ipv4() + "\n" +
                "ipv6=" + ipv6() + "\n" +
                "port=" + port();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mPeerId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(mPeerId);
        }
        dest.writeValue(mProtocol);
        dest.writeString(mUrl);
        dest.writeString(mIpv4);
        dest.writeString(mIpv6);
        dest.writeInt(mPort);
    }
}