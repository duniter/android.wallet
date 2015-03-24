package io.ucoin.app.adapter;

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
import java.net.MalformedURLException;
import java.net.URL;

import io.ucoin.app.R;
import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinCertification;
import io.ucoin.app.model.UcoinMember;
import io.ucoin.app.model.enums.CertificationType;
import io.ucoin.app.model.http_api.Sources;
import io.ucoin.app.model.http_api.WotCertification;
import io.ucoin.app.model.http_api.WotLookup;
import io.ucoin.app.sqlite.Currencies;
import io.ucoin.app.model.UcoinCurrencies;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinSource;
import io.ucoin.app.model.UcoinSources;
import io.ucoin.app.model.UcoinWallet;

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
        //todo Perform synchronisation operations

        Log.d("SYNCADAPTER", "START______________________________________________________________");
        UcoinCurrencies currencies = new Currencies(getContext());
        for (UcoinCurrency currency : currencies) {
            syncWallets(currency);
            syncCertifications(currency, CertificationType.OF);
            syncCertifications(currency, CertificationType.BY);
        }
        Log.d("SYNCADAPTER", "STOP_______________________________________________________________");

    }

    public void syncWallets(UcoinCurrency currency) {
        for (UcoinWallet wallet : currency.wallets()) {

            //todo extract peer from currency
            //todo define a strategy to know which peer to retrieve from the currency
            URL url;
            HttpURLConnection conn;
            InputStream stream;
            Sources sources;
            try {
                url = new URL("http", "metab.ucoin.io", 9201, "/tx/sources/" + wallet.publicKey());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                stream = conn.getInputStream();
                sources = Sources.fromJson(stream);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            UcoinSources apiSources = wallet.newSources(sources);

            // delete the consumed sources
            for (UcoinSource sqlSource : wallet.sources()) {
                boolean found = false;
                for (UcoinSource apiSource : apiSources) {
                    if (sqlSource.fingerprint().equals(apiSource.fingerprint())) {
                        found = true;
                    }
                }

                if (!found) {
                    wallet.sources().delete(sqlSource.id());
                }
            }

            //add the new sources
            boolean sourcesAdded = false;
            for (UcoinSource apiSource : apiSources) {
                boolean found = false;
                for (UcoinSource sqlSource : wallet.sources()) {
                    if (apiSource.fingerprint().equals(sqlSource.fingerprint())) {
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

    //todo this code is dirty I think, should write more suitable API
    public UcoinMember fetchMember(UcoinCurrency currency, String public_key) {
        URL url;
        HttpURLConnection conn;
        InputStream stream;
        WotLookup lookup;
        try {
            url = new URL("http", "metab.ucoin.io", 9201, "/wot/lookup/" +
                    public_key);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            stream = conn.getInputStream();
            lookup = WotLookup.fromJson(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        for(WotLookup.Result result : lookup.results) {
            String publicKey = result.pubkey;
            for(WotLookup.Uid uid : result.uids) {
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
        try {
            if(type == CertificationType.OF) {
                url = new URL("http", "metab.ucoin.io", 9201, "/wot/certifiers-of/" +
                        currency.identity().wallet().publicKey());
            } else {
                url = new URL("http", "metab.ucoin.io", 9201, "/wot/certified-by/" +
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
        for (UcoinCertification sqlCertification : currency.identity().certifications()) {
            boolean found = false;
            for (WotCertification.Certification apiCertification : certifications.certifications) {
                if (sqlCertification.signature().equals(apiCertification.signature)) {
                    found = true;
                }
            }

            if (!found) {
                currency.identity().certifications().delete(sqlCertification.id());
            }
        }

        //add the new certifications
        for (WotCertification.Certification apiCertification : certifications.certifications) {
            boolean found = false;
            for (UcoinCertification sqlCertification : currency.identity().certifications()) {
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
                UcoinCertification certification = currency.identity().certifications().newCertification(
                        member,
                        type,
                        apiCertification.cert_time.block,
                        apiCertification.cert_time.medianTime,
                        apiCertification.signature
                );
                currency.identity().certifications().add(certification);
                certificationAdded = true;
            }

            if (certificationAdded) {
                notifyNewCertification();
            }
        }
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
