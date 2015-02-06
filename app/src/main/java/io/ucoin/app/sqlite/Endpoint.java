package io.ucoin.app.sqlite;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Patterns;

import org.apache.http.conn.util.InetAddressUtils;

import java.util.ArrayList;
import java.util.Arrays;

import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinEndpoint;

public class Endpoint extends SQLiteEntity
        implements UcoinEndpoint {

    private Long mPeerId;
    private String mUrl;
    private String mIpv4;
    private String mIpv6;
    private int mPort;

    public Endpoint(Context context, Long endpointId) {
        super(context, Provider.ENDPOINT_URI, endpointId);
    }

    public Endpoint(Long peerId, String endpoint) {
        mPeerId = peerId;

        //extract port and remove from the list and extract port and addresses
        ArrayList<String> words = new ArrayList<>(Arrays.asList(endpoint.split(" ")));
        mPort = Integer.parseInt(words.remove(words.size() - 1));
        //todo Protocol (API_MERKLE, OTHER,...)
        for (String word : words) {
            if (InetAddressUtils.isIPv4Address(word)) {
                mIpv4 = word;
            } else if (InetAddressUtils.isIPv6Address(word)) {
                mIpv6 = word;
            } else if (Patterns.WEB_URL.matcher(word).matches()) {
                mUrl = word;
            }
        }
    }


    public Endpoint(Long peerId, UcoinEndpoint endpoint) {
        mPeerId = peerId;
        mUrl = endpoint.url();
        mIpv4 = endpoint.ipv4();
        mIpv6 = endpoint.ipv6();
        mPort = endpoint.port();
    }

    @Override
    public Long peerId() {
        return (this.mId == null) ? mPeerId : getLong(Contract.Endpoint.PEER_ID);
    }

    @Override
    public String ipv4() {
        return (this.mId == null) ? mIpv4 : getString(Contract.Endpoint.IPV4);
    }

    @Override
    public String ipv6() {
        return (this.mId == null) ? mIpv6 : getString(Contract.Endpoint.IPV6);
    }

    @Override
    public String url() {
        return (this.mId == null) ? mUrl : getString(Contract.Endpoint.URL);
    }

    @Override
    public Integer port() {
        return (this.mId == null) ? mPort : getInt(Contract.Endpoint.PORT);
    }

    @Override
    public String toString() {
        return "ENDPOINT id=" + ((id() == null) ? "not in database" : id()) + "\n" +
                "peer_id=" + peerId() + "\n" +
                "url=" + url() + "\n" +
                "ipv4=" + ipv4() + "\n" +
                "ipv6=" + ipv6() + "\n" +
                "port=" + port();
    }

    protected Endpoint(Parcel in) {
        mPeerId = in.readByte() == 0x00 ? null : in.readLong();
        mUrl = in.readString();
        mIpv4 = in.readString();
        mIpv6 = in.readString();
        mPort = in.readInt();
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
        dest.writeString(mUrl);
        dest.writeString(mIpv4);
        dest.writeString(mIpv6);
        dest.writeInt(mPort);
    }

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
}