package io.ucoin.app.content;

import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import io.ucoin.app.BuildConfig;
import io.ucoin.app.R;
import io.ucoin.app.enumeration.CertificationType;
import io.ucoin.app.enumeration.SourceState;
import io.ucoin.app.enumeration.SourceType;
import io.ucoin.app.enumeration.TxDirection;
import io.ucoin.app.enumeration.TxState;
import io.ucoin.app.model.UcoinBlock;
import io.ucoin.app.model.UcoinCurrencies;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinEndpoint;
import io.ucoin.app.model.UcoinIdentity;
import io.ucoin.app.model.UcoinMember;
import io.ucoin.app.model.UcoinSource;
import io.ucoin.app.model.UcoinTx;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.http_api.BlockchainBlock;
import io.ucoin.app.model.http_api.BlockchainMemberships;
import io.ucoin.app.model.http_api.BlockchainWithUd;
import io.ucoin.app.model.http_api.TxHistory;
import io.ucoin.app.model.http_api.TxSources;
import io.ucoin.app.model.http_api.UdHistory;
import io.ucoin.app.model.http_api.WotCertification;
import io.ucoin.app.model.http_api.WotLookup;
import io.ucoin.app.model.sql.sqlite.Currencies;

public class SyncAdapter extends AbstractThreadedSyncAdapter implements Response.ErrorListener {

    private RequestQueue mRequestQueue;
    private int mRequestCount = 0;

    public SyncAdapter(Context context, boolean autoInitialize) {
        this(context, autoInitialize, false);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        DbProvider.initUris(context);
        mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.start();
    }

    @Override
    public void onPerformSync(Account androidAccount, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        if (BuildConfig.DEBUG) Log.d("SYNCADAPTER", "START_______________________________________");


        if (mRequestCount != 0) {
            if (BuildConfig.DEBUG) Log.d("SyncAdapter", "ALREADY RUNNING");
            return;
        }

        UcoinCurrencies currencies = new Currencies(getContext());
        for (final UcoinCurrency currency : currencies) {
            fetchCurrentBlock(currency);
        }

        if (BuildConfig.DEBUG) Log.d("SYNCADAPTER", "END_________________________________________");
    }

    public void fetchCurrentBlock(final UcoinCurrency currency) {
        UcoinEndpoint endpoint = currency.peers().at(0).endpoints().at(0);
        String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/blockchain/current/";
        StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mRequestCount--;
                        UcoinBlock localCurrentBlock = currency.blocks().currentBlock();
                        BlockchainBlock currentBlock = BlockchainBlock.fromJson(response);

                        fetchUdBlocks(currency);
                        UcoinBlock newBlock = currency.blocks().add(currentBlock);
                        if (newBlock != null &&
                                localCurrentBlock != null &&
                                localCurrentBlock.dividend() == null) {
                            localCurrentBlock.delete();
                        }

                        syncWallets(currency);
                        if (currency.identity() != null) syncIdentity(currency.identity());
                    }
                }, this);
        request.setTag(this);
        mRequestQueue.add(request);
        mRequestCount++;
    }

    void fetchUds(final UcoinWallet wallet) {
        UcoinEndpoint endpoint = wallet.currency().peers().at(0).endpoints().at(0);
        String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/ud/history/";
        url += wallet.publicKey();
        StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mRequestCount--;
                        UdHistory udHistory = UdHistory.fromJson(response);

                        for (UdHistory.Ud ud : udHistory.history.history) {
                            wallet.uds().add(ud);
                        }
                    }
                }, this);
        request.setTag(this);
        request.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
        mRequestCount++;
    }

    void fetchCert(final UcoinIdentity identity, final CertificationType type) {
        UcoinEndpoint endpoint = identity.currency().peers().at(0).endpoints().at(0);
        String url;
        if (type == CertificationType.OF) {
            url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/wot/certifiers-of/";
        } else {
            url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/wot/certified-by/";

        }
        url += identity.wallet().publicKey();
        final StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mRequestCount--;
                        WotCertification certifications = WotCertification.fromJson(response);
                        for (WotCertification.Certification certification : certifications.certifications) {
                            UcoinMember member = identity.members().getByPublicKey(certification.pubkey);
                            if (member == null) {
                                member = identity.members().add(certification);
                                fetchMember(member);
                            }
                            identity.certifications().add(member, type, certification);
                        }
                    }
                }, this);
        request.setTag(this);
        mRequestQueue.add(request);
        mRequestCount++;
    }

    void fetchMember(final UcoinMember member) {
        UcoinEndpoint endpoint = member.identity().currency().peers().at(0).endpoints().at(0);
        String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/wot/lookup/";
        url += member.publicKey();
        final StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mRequestCount--;
                        WotLookup lookup = WotLookup.fromJson(response);
                        member.setSelf(lookup.results[0].uids[0].self);
                        member.setTimestamp(lookup.results[0].uids[0].meta.timestamp);
                    }
                },
                this);
        request.setTag(this);
        mRequestQueue.add(request);
        mRequestCount++;
    }

    void fetchMemberships(final UcoinIdentity identity) {
        UcoinEndpoint endpoint = identity.currency().peers().at(0).endpoints().at(0);
        String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/blockchain/memberships/";
        url += identity.uid();
        StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mRequestCount--;
                        BlockchainMemberships memberships = BlockchainMemberships.fromJson(response);

                        if(identity.sigDate() == null) {
                            identity.setSigDate(memberships.sigDate);
                        }

                        for (BlockchainMemberships.Membership membership : memberships.memberships) {
                            if (identity.currency().blocks().getByNumber(membership.blockNumber) == null) {
                                fetchBlock(identity.currency(), membership.blockNumber, true);
                            } else {
                                identity.currency().blocks().getByNumber(membership.blockNumber).setIsMembership(true);
                                identity.memberships().add(membership);
                            }
                        }
                    }
                }, this);
        request.setTag(this);
        mRequestQueue.add(request);
        mRequestCount++;
    }

    void fetchSelfCertification(final UcoinIdentity identity) {
        UcoinEndpoint endpoint = identity.currency().peers().at(0).endpoints().at(0);
        String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/wot/lookup/";
        url += identity.wallet().publicKey();
        StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mRequestCount--;
                        WotLookup lookup = WotLookup.fromJson(response);
                        for (WotLookup.Result result : lookup.results) {
                            if (result.pubkey.equals(identity.wallet().publicKey())) {
                                for (WotLookup.Uid uid : result.uids) {
                                    if (uid.uid.equals(identity.uid())) {
                                        if ((identity.selfCertifications().getBySelf(uid.self)) == null) {
                                            identity.selfCertifications().add(uid);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }, this);
        request.setTag(this);
        mRequestQueue.add(request);
        mRequestCount++;
    }

    void syncIdentity(UcoinIdentity identity) {
        fetchCert(identity, CertificationType.OF);
        fetchCert(identity, CertificationType.BY);
        fetchMemberships(identity);
        fetchSelfCertification(identity);
    }

    void fetchUdBlocks(final UcoinCurrency currency) {
        UcoinEndpoint endpoint = currency.peers().at(0).endpoints().at(0);
        String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/blockchain/with/ud";
        StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mRequestCount--;
                        UcoinBlock lastUdBlock = currency.blocks().lastUdBlock();
                        BlockchainWithUd udBlocksNumber = BlockchainWithUd.fromJson(response);
                        for (Long number : udBlocksNumber.result.blocks) {
                            if (lastUdBlock == null || number > lastUdBlock.number()) {
                                fetchBlock(currency, number, false);
                            }
                        }
                    }
                }, this);
        request.setTag(this);
        mRequestQueue.add(request);
        mRequestCount++;
    }

    void fetchBlock(final UcoinCurrency currency, Long number, final boolean isMembership) {
        UcoinEndpoint endpoint = currency.peers().at(0).endpoints().at(0);
        String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/blockchain/block/" + number;
        StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mRequestCount--;
                        BlockchainBlock block = BlockchainBlock.fromJson(response);
                        UcoinBlock newBlock = currency.blocks().add(block);
                        if (newBlock != null) {
                            newBlock.setIsMembership(isMembership);
                        }
                    }
                }, this);
        request.setTag(this);
        mRequestQueue.add(request);
        mRequestCount++;
    }

    public void syncWallets(UcoinCurrency currency) {
        for (UcoinWallet wallet : currency.wallets()) {
            fetchSources(wallet);
        }
    }

    public void fetchSources(final UcoinWallet wallet) {
        UcoinEndpoint endpoint = wallet.currency().peers().at(0).endpoints().at(0);
        String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/tx/sources/";
        url += wallet.publicKey();

        StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mRequestCount--;
                        TxSources sources = TxSources.fromJson(response);
                        boolean sourcesDeleted = false;
                        boolean txSourceAdded = false;
                        boolean udSourceAdded = false;
                        for (UcoinSource source : wallet.sources()) {
                            boolean consumed = true;
                            for (TxSources.Source txSource : sources.sources) {
                                if (source.fingerprint().equals(txSource.fingerprint)) {
                                    consumed = false;
                                    break;
                                }
                            }
                            if (consumed){
                                source.delete();
                                sourcesDeleted = true;
                            }
                        }
                        for (TxSources.Source txSource : sources.sources) {
                            if (wallet.sources().add(txSource) != null) {
                                if (txSource.type == SourceType.D) udSourceAdded = true;
                                if (txSource.type == SourceType.T) txSourceAdded = true;
                            }
                        }

                        if(txSourceAdded) fetchTxs(wallet);
                        if(udSourceAdded) fetchUds(wallet);
                    }
                }, this);

        request.setTag(this);
        mRequestQueue.add(request);
        mRequestCount++;
    }

    public void fetchTxs(final UcoinWallet wallet) {
        UcoinEndpoint endpoint = wallet.currency().peers().at(0).endpoints().at(0);
        String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/tx/history/";
        url += wallet.publicKey();
/*
        UcoinTx lastTx = wallet.txs().getLastTx();
        if (lastTx != null) {
            url += "/times/" + lastTx.time() + 1 + "/" + Application.getCurrentTime();
        }
*/
        StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mRequestCount--;
                        TxHistory txHistory = TxHistory.fromJson(response);
                        for (TxHistory.ReceivedTx tx : txHistory.history.received) {
                            UcoinTx localTx = wallet.txs().getByHash(tx.hash);
                            if (localTx == null) {
                                boolean isIssuer = false;
                                for (String issuer : tx.issuers) {
                                    if (issuer.equals(wallet.publicKey())) {
                                        isIssuer = true;
                                        break;
                                    }
                                }
                                if (!isIssuer) {
                                    if (tx.time != null) wallet.txs().add(tx, TxDirection.IN);
                                }
                            } else if (localTx.state() == TxState.PENDING) {
                                localTx.setState(TxState.CONFIRMED);
                                localTx.setTime(tx.time);
                                localTx.setBlock(tx.block_number);
                            }
                        }

                        for (TxHistory.SentTx tx : txHistory.history.sent) {
                            UcoinTx localTx = wallet.txs().getByHash(tx.hash);
                            if (localTx == null) {
                                boolean isIssuer = false;
                                for (String issuer : tx.issuers) {
                                    if (issuer.equals(wallet.publicKey())) {
                                        isIssuer = true;
                                        break;
                                    }
                                }
                                if (isIssuer) {
                                    if (tx.time != null) wallet.txs().add(tx, TxDirection.OUT);
                                }
                            } else if (localTx.state() == TxState.PENDING) {
                                localTx.setState(TxState.CONFIRMED);
                                localTx.setTime(tx.time);
                                localTx.setBlock(tx.block_number);
                            }
                        }


                        for (TxHistory.PendingTx tx : txHistory.history.pending) {
                            UcoinTx localTx = wallet.txs().getByHash(tx.hash);
                            if (localTx == null) {
                                boolean isIssuer = false;
                                for (String issuer : tx.issuers) {
                                    if (issuer.equals(wallet.publicKey())) {
                                        isIssuer = true;
                                        break;
                                    }
                                }

                                UcoinTx newTx;
                                if (!isIssuer) {
                                    newTx = wallet.txs().add(tx, TxDirection.IN);
                                } else {
                                    newTx = wallet.txs().add(tx, TxDirection.OUT);
                                    for (TxHistory.Tx.Input input : tx.inputs) {
                                        UcoinSource source = wallet.sources().getByFingerprint(input.fingerprint);
                                        if (source != null) {
                                            source.setState(SourceState.CONSUMED);
                                        }
                                    }
                                }
                                if (newTx != null) {
                                    newTx.setBlock(wallet.currency().blocks().currentBlock().number());
                                    newTx.setTime(wallet.currency().blocks().currentBlock().time());
                                }
                            }
                        }
                    }
                }, this);
        request.setTag(this);
        mRequestQueue.add(request);
        mRequestCount++;
    }

    private void notifyNewCurrency(UcoinCurrency currency) {
        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n = new Notification.Builder(getContext())
                .setContentTitle("New currency")
                .setContentText("Currency \"" + currency.name() + "\" succesfully added")
                .setSmallIcon(R.drawable.ic_plus_white_36dp)
                .setAutoCancel(true).build();


        NotificationManager manager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, n);
    }

    private void notifyNewCertification() {
        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n = new Notification.Builder(getContext())
                .setContentTitle("New certifications")
                .setContentText("Subject")
                .setSmallIcon(R.drawable.ic_plus_white_36dp)
                .setAutoCancel(true).build();

        NotificationManager manager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, n);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mRequestCount--;
        if (BuildConfig.DEBUG) Log.d("SyncAdapter:", error.toString());
    }
}