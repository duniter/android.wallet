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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.ucoin.app.R;
import io.ucoin.app.enums.CertificationType;
import io.ucoin.app.enums.TxDirection;
import io.ucoin.app.model.UcoinBlock;
import io.ucoin.app.model.UcoinCertification;
import io.ucoin.app.model.UcoinCurrencies;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinEndpoint;
import io.ucoin.app.model.UcoinMember;
import io.ucoin.app.model.UcoinPendingEndpoint;
import io.ucoin.app.model.UcoinSource;
import io.ucoin.app.model.UcoinTx;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.http_api.BlockchainBlock;
import io.ucoin.app.model.http_api.BlockchainParameter;
import io.ucoin.app.model.http_api.NetworkPeering;
import io.ucoin.app.model.http_api.TxHistory;
import io.ucoin.app.model.http_api.TxSources;
import io.ucoin.app.model.http_api.WotCertification;
import io.ucoin.app.model.http_api.WotLookup;
import io.ucoin.app.sqlite.Currencies;
import io.ucoin.app.sqlite.PendingEndpoints;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Provider.initUris(context);
    }

    @SuppressWarnings("unused")
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        Provider.initUris(context);
    }

    @Override
    public void onPerformSync(Account androidAccount, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        Log.d("SYNCADAPTER", "START______________________________________________________________");
        for (UcoinPendingEndpoint endpoint : new PendingEndpoints(getContext())) {
            fetchNewCurrency(endpoint);
        }

        UcoinCurrencies currencies = new Currencies(getContext());
        for (UcoinCurrency currency : currencies) {
            syncWallets(currency);
            syncTx(currency);
            syncCertifications(currency, CertificationType.OF);
            syncCertifications(currency, CertificationType.BY);
        }
        Log.d("SYNCADAPTER", "STOP_______________________________________________________________");

    }

    public boolean syncBlocks(UcoinCurrency currency) {
        //todo define a strategy for retrieving a peer from the currency
        URL url;
        HttpURLConnection conn;
        InputStream stream;
        BlockchainBlock remoteBlock;
        UcoinEndpoint endpoint = currency.peers().iterator().next().endpoints().iterator().next();

        try {
            url = new URL("http", "metab.ucoin.io", endpoint.port(), "/blockchain/current/");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            stream = conn.getInputStream();
            remoteBlock = BlockchainBlock.fromJson(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        UcoinBlock sqlBlock = currency.blocks().lastBlock();
        if (sqlBlock != null && sqlBlock.number() < remoteBlock.number) {
            UcoinBlock bl = currency.blocks().add(remoteBlock);

            sqlBlock.delete();
            return true;
        }
        return false;
    }


    public void syncTx(UcoinCurrency currency) {
        for (UcoinWallet wallet : currency.wallets()) {

            URL url;
            HttpURLConnection conn;
            InputStream stream;
            TxHistory txHistory;
            UcoinEndpoint endpoint = currency.peers().iterator().next().endpoints().iterator().next();
            try {
                url = new URL("http", "metab.ucoin.io", endpoint.port(), "/tx/history/" + wallet.publicKey());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                stream = conn.getInputStream();
                txHistory = new TxHistory().fromJson(stream);
            } catch (IOException e) {
                e.printStackTrace();

                return;
            }

            for (TxHistory.Tx sentTx : txHistory.history.sent) {
                wallet.txs().add(sentTx, TxDirection.SENT);
            }

            for (TxHistory.Tx rcvTx : txHistory.history.received) {
                wallet.txs().add(rcvTx, TxDirection.RECEIVED);
            }
        }

    }

    public void syncWallets(UcoinCurrency currency) {
        for (UcoinWallet wallet : currency.wallets()) {

            URL url;
            HttpURLConnection conn;
            InputStream stream;
            TxSources txSources;
            UcoinEndpoint endpoint = currency.peers().iterator().next().endpoints().iterator().next();
            try {
                url = new URL("http", "metab.ucoin.io", endpoint.port(), "/tx/sources/" + wallet.publicKey());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                stream = conn.getInputStream();
                txSources = TxSources.fromJson(stream);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            // delete the consumed sources
            for (UcoinSource sqlSource : wallet.sources()) {
                boolean found = false;

                for (TxSources.Source apiSource : txSources.sources) {
                    if (sqlSource.fingerprint().equals(apiSource.fingerprint)) {
                        found = true;
                    }
                }

                if (!found) {
                    sqlSource.delete();
                }
            }

            //add the new sources
            boolean sourcesAdded = false;
            for (TxSources.Source apiSource : txSources.sources) {
                boolean found = false;
                for (UcoinSource sqlSource : wallet.sources()) {
                    if (apiSource.fingerprint.equals(sqlSource.fingerprint())) {
                        found = true;
                    }
                }

                if (!found) {
                    wallet.sources().add(apiSource);
                    sourcesAdded = true;
                }
            }

            if (sourcesAdded) {
                notifyNewSource();
            }
        }
    }

    public void fetchNewCurrency(UcoinPendingEndpoint endpoint) {
        try {
            //Load Peer
            URL url = new URL("http", endpoint.address(), endpoint.port(), "/network/peering/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            InputStream stream = conn.getInputStream();
            NetworkPeering networkPeering = NetworkPeering.fromJson(stream);

            // Load currency
            url = new URL("http", endpoint.address(), endpoint.port(), "/blockchain/parameters");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            stream = conn.getInputStream();
            BlockchainParameter parameter = BlockchainParameter.fromJson(stream);

            //Load first block
            url = new URL("http", endpoint.address(), endpoint.port(), "/blockchain/block/0");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            stream = conn.getInputStream();
            BlockchainBlock firstBlock = BlockchainBlock.fromJson(stream);

            //Load last block
            url = new URL("http", endpoint.address(), endpoint.port(), "/blockchain/current");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            stream = conn.getInputStream();
            BlockchainBlock currentBlock = BlockchainBlock.fromJson(stream);

            endpoint.delete();
            UcoinCurrencies currencies = new Currencies(getContext());
            UcoinCurrency currency = currencies.add(parameter);
            currency.peers().add(networkPeering);
            currency.blocks().add(firstBlock);
            currency.blocks().add(currentBlock);
            notifyNewCurrency(currency);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }

    //todo this code is dirty I think, should write more suitable API
    public UcoinMember fetchMember(UcoinCurrency currency, String public_key) {
        URL url;
        HttpURLConnection conn;
        InputStream stream;
        WotLookup lookup;
        UcoinEndpoint endpoint = currency.peers().iterator().next().endpoints().iterator().next();
        try {
            url = new URL("http", "metab.ucoin.io", endpoint.port(), "/wot/lookup/" +
                    public_key);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            stream = conn.getInputStream();
            lookup = WotLookup.fromJson(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        for (WotLookup.Result result : lookup.results) {
            String publicKey = result.pubkey;
            for (WotLookup.Uid uid : result.uids) {
                UcoinMember member = currency.members().newMember(
                        uid.uid,
                        publicKey,
                        false,
                        false,
                        uid.self,
                        uid.meta.timestamp);
                return member;
            }
        }
        return null;
    }


    public void syncCertifications(UcoinCurrency currency, CertificationType type) {
        if (currency.identity() == null) {
            return;
        }

        URL url;
        HttpURLConnection conn;
        InputStream stream;
        WotCertification certifications;
        UcoinEndpoint endpoint = currency.peers().iterator().next().endpoints().iterator().next();
        try {
            if (type == CertificationType.OF) {
                url = new URL("http", "metab.ucoin.io", endpoint.port(), "/wot/certifiers-of/" +
                        currency.identity().wallet().publicKey());
            } else {
                url = new URL("http", "metab.ucoin.io", endpoint.port(), "/wot/certified-by/" +
                        currency.identity().wallet().publicKey());
            }
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            stream = conn.getInputStream();
            certifications = WotCertification.fromJson(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        // delete the expired certifications
        for (UcoinCertification sqlCertification : currency.identity().certifications().getByType(type)) {
            boolean found = false;
            for (WotCertification.Certification apiCertification : certifications.certifications) {
                if (sqlCertification.signature().equals(apiCertification.signature)) {
                    found = true;
                }
            }

            if (!found) {
                sqlCertification.delete();
            }
        }

        //add the new certifications
        for (WotCertification.Certification apiCertification : certifications.certifications) {
            boolean found = false;
            for (UcoinCertification sqlCertification : currency.identity().certifications().getByType(type)) {
                if (apiCertification.signature.equals(sqlCertification.signature())) {
                    found = true;
                }
            }

            boolean certificationAdded = false;
            if (!found) {
                UcoinMember member = currency.members().getByUid(apiCertification.uid);
                if (member == null) {
                    member = fetchMember(currency, apiCertification.pubkey);
                    member.isMember(apiCertification.isMember);
                    //todo was member is not available from API
                    member.wasMember(apiCertification.isMember);
                    member = currency.members().add(member);
                }

                currency.identity().certifications().add(member, type, apiCertification);
                certificationAdded = true;
            }

            if (certificationAdded) {
                notifyNewCertification();
            }
        }
    }


    private void notifyNewCurrency(UcoinCurrency currency) {
        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n = new Notification.Builder(getContext())
                .setContentTitle("New currency")
                .setContentText("Currency \"" + currency.currencyName() + "\" succesfully added")
                .setSmallIcon(R.drawable.ic_plus_white_36dp)
                .setAutoCancel(true).build();


        NotificationManager manager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, n);
    }

    private void notifyNewSource() {
        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n = new Notification.Builder(getContext())
                .setContentTitle("New sources")
                .setContentText("Subject")
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
}
