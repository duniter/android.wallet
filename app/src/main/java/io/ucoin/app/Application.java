package io.ucoin.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Application extends android.app.Application{

    public static final int ACTIVITY_LOOKUP = 0x1;
    public static final int ACTIVITY_CURRENCY_LIST = 0x2;


    public static final String EXTRA_CURRENCY_ID = "currency_id";
    public static final String EXTRA_WALLET_ID = "wallet_id";
    public static final String EXTRA_SYNC_OP = "sync_op";

    private static Context mContext;
    private static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        //LOAD account
        AccountManager accountManager = AccountManager.get(this);
        android.accounts.Account[] accounts = accountManager
                .getAccountsByType(getString(R.string.ACCOUNT_TYPE));

        if (accounts.length < 1) {
            Account account = new Account(
                    getString(R.string.app_name),
                    getString(R.string.ACCOUNT_TYPE));

            ContentResolver.setSyncAutomatically(account, getString(R.string.AUTHORITY), true);
            ContentResolver.setIsSyncable(account, getString(R.string.AUTHORITY), 1);
            AccountManager.get(this).addAccountExplicitly(account, null, null);

            accounts = accountManager
                    .getAccountsByType(getString(R.string.ACCOUNT_TYPE));
        }

        //initialize Volley request queue
        mRequestQueue = Volley.newRequestQueue(mContext);
        mRequestQueue.start();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mRequestQueue.stop();
    }

    public static RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public static Context getContext() {
        return mContext;
    }

    public static long getCurrentTime() {
        return (long) Math.floor(System.currentTimeMillis() / 1000);
    }

    public static void requestSync() {
        requestSync(new Bundle());

    }

    public static void requestSync(Bundle extras) {
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        AccountManager accountManager = AccountManager.get(mContext);
        android.accounts.Account[] accounts = accountManager
                .getAccountsByType(mContext.getString(R.string.ACCOUNT_TYPE));
        ContentResolver.requestSync(accounts[0], mContext.getString(R.string.AUTHORITY), extras);
    }

    public static void cancelSync() {
        AccountManager accountManager = AccountManager.get(mContext);
        android.accounts.Account[] accounts = accountManager
                .getAccountsByType(mContext.getString(R.string.ACCOUNT_TYPE));
        ContentResolver.cancelSync(accounts[0], mContext.getString(R.string.AUTHORITY));
    }
}
