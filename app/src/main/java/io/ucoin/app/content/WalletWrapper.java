package io.ucoin.app.content;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;

import io.ucoin.app.BuildConfig;
import io.ucoin.app.enumeration.SourceState;
import io.ucoin.app.enumeration.TxDirection;
import io.ucoin.app.enumeration.TxState;
import io.ucoin.app.model.UcoinEndpoint;
import io.ucoin.app.model.UcoinSource;
import io.ucoin.app.model.UcoinTx;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.http_api.TxHistory;
import io.ucoin.app.model.http_api.TxSources;
import io.ucoin.app.model.http_api.UdHistory;

public class WalletWrapper implements Response.ErrorListener, RequestQueue.RequestFinishedListener {
    private UcoinQueue mRequestQueue;
    private UcoinWallet mWallet;
    private HashMap<Request, Boolean> mRequests;

    WalletWrapper(UcoinQueue queue, UcoinWallet wallet) {
        mRequestQueue = queue;
        mWallet = wallet;
        mRequests = new HashMap<>();
    }

    public void start() {
        mRequests.put(fetchSources(), null);
        mRequests.put(fetchTxs(), null);
        mRequests.put(fetchUds(), null);
    }

    public Request fetchSources() {
        UcoinEndpoint endpoint = mWallet.currency().peers().at(0).endpoints().at(0);
        String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/tx/sources/";
        url += mWallet.publicKey();

        StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        onSourcesRequest(TxSources.fromJson(response));
                    }
                }, this);
        request.setTag(this);
        mRequestQueue.add(request);
        return request;
    }

    public Request fetchTxs() {
        UcoinEndpoint endpoint = mWallet.currency().peers().at(0).endpoints().at(0);
        String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/tx/history/";
//        UcoinTx lastTx = wallet.txs().getLastTx();
//        if (lastTx != null) {
//            url += "/times/" + lastTx.time() + 1 + "/" + Application.getCurrentTime();
        url += mWallet.publicKey();

        StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        onTxRequest(TxHistory.fromJson(response));
                    }
                }, this);
        request.setTag(this);
        mRequestQueue.add(request);
        return request;
    }

    public Request fetchUds() {
        UcoinEndpoint endpoint = mWallet.currency().peers().at(0).endpoints().at(0);
        String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/ud/history/";
        url += mWallet.publicKey();
        StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        onUdsRequest(UdHistory.fromJson(response));
                    }
                }, this);
        request.setTag(this);
        mRequestQueue.add(request);
        return request;
    }


    public void onSourcesRequest(TxSources sources) {
        mWallet.sources().set(sources);
    }

    public void onTxRequest(TxHistory history) {
        for (TxHistory.ReceivedTx tx : history.history.received) {
            UcoinTx localTx = mWallet.txs().getByHash(tx.hash);
            if (localTx == null) {
                boolean isIssuer = false;
                for (String issuer : tx.issuers) {
                    if (issuer.equals(mWallet.publicKey())) {
                        isIssuer = true;
                        break;
                    }
                }
                if (!isIssuer) {
                    if (tx.time != null) mWallet.txs().add(tx, TxDirection.IN);
                }
            } else if (localTx.state() == TxState.PENDING) {
                localTx.setState(TxState.CONFIRMED);
                localTx.setTime(tx.time);
                localTx.setBlock(tx.block_number);
            }
        }

        for (TxHistory.SentTx tx : history.history.sent) {
            UcoinTx localTx = mWallet.txs().getByHash(tx.hash);
            if (localTx == null) {
                boolean isIssuer = false;
                for (String issuer : tx.issuers) {
                    if (issuer.equals(mWallet.publicKey())) {
                        isIssuer = true;
                        break;
                    }
                }
                if (isIssuer) {
                    if (tx.time != null) mWallet.txs().add(tx, TxDirection.OUT);
                }
            } else if (localTx.state() == TxState.PENDING) {
                localTx.setState(TxState.CONFIRMED);
                localTx.setTime(tx.time);
                localTx.setBlock(tx.block_number);
            }
        }


        for (TxHistory.PendingTx tx : history.history.pending) {
            UcoinTx localTx = mWallet.txs().getByHash(tx.hash);
            if (localTx == null) {
                boolean isIssuer = false;
                for (String issuer : tx.issuers) {
                    if (issuer.equals(mWallet.publicKey())) {
                        isIssuer = true;
                        break;
                    }
                }

                UcoinTx newTx;
                if (!isIssuer) {
                    newTx = mWallet.txs().add(tx, TxDirection.IN);
                } else {
                    newTx = mWallet.txs().add(tx, TxDirection.OUT);
                    for (TxHistory.Tx.Input input : tx.inputs) {
                        UcoinSource source = mWallet.sources().getByFingerprint(input.fingerprint);
                        if (source != null) {
                            source.setState(SourceState.CONSUMED);
                        }
                    }
                }
                if (newTx != null) {
                    newTx.setBlock(mWallet.currency().blocks().currentBlock().number());
                    newTx.setTime(mWallet.currency().blocks().currentBlock().time());
                }
            }
        }
    }

    public void onUdsRequest(UdHistory history) {
        for (UdHistory.Ud ud : history.history.history) {
            mWallet.uds().add(ud);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (BuildConfig.DEBUG) Log.d("WalletWrapper", error.toString());
    }

    @Override
    public void onRequestFinished(Request request) {
        if (request.hasHadResponseDelivered()) {
            mRequests.put(request, true);
        } else {
            mRequests.put(request, false);
        }

        //requests are all finished
        if (!mRequests.containsValue(null)) {
            // all operations are success
            if (!mRequests.containsValue(false)) {
                mWallet.setSyncBlock(mWallet.currency().blocks().currentBlock().number());
            }
        }
    }
}