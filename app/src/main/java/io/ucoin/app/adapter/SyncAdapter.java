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
import io.ucoin.app.model.http_api.Sources;
import io.ucoin.app.sqlite.Currencies;
import io.ucoin.app.model.UcoinCurrencies;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinSource;
import io.ucoin.app.model.UcoinSources;
import io.ucoin.app.model.UcoinWallet;

public class SyncAdapter  extends AbstractThreadedSyncAdapter{

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Provider.initUris(context);
    }

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
        }
        Log.d("SYNCADAPTER", "STOP_______________________________________________________________");

    }

    public void syncWallets(UcoinCurrency currency) {
        UcoinCurrencies currencies = new Currencies(getContext());
        for (UcoinWallet wallet : currency.wallets()) {
            boolean addedSources = false;

            //todo extract peer from currency
            //todo define a strategy to know which peer to retrieve from the currency
            //this is just a test
            String publickKey = wallet.publicKey();
            URL url = null;
            try {
                url = new URL("http", "metab.ucoin.io", 9201, "/tx/sources/" + publickKey);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.setConnectTimeout(5000);
            InputStream stream = null;
            try {
                stream = conn.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Sources sources = Sources.fromJson(stream);


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
            for (UcoinSource apiSource : apiSources) {
                boolean found = false;
                for (UcoinSource sqlSource : wallet.sources()) {
                    if (apiSource.fingerprint().equals(sqlSource.fingerprint())) {
                        found = true;
                    }
                }

                if (!found) {
                    wallet.sources().add(apiSource);
                    addedSources = true;
                }
            }

            if (addedSources) {
                notifyNewSource();
            }
        }
    }


    private void notifyNewSource() {
        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(getContext())
                .setContentTitle("New sources")
                .setContentText("Subject")
                .setSmallIcon(R.drawable.ic_plus_white_36dp)
                .setAutoCancel(true).build();


        NotificationManager manager =
                (NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, n);
    }
}
