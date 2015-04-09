package io.ucoin.app;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;

import io.ucoin.app.model.UcoinCurrencies;
import io.ucoin.app.sqlite.Currencies;

public class Application extends android.app.Application{

    private UcoinCurrencies mCurrencies;

    @Override
    public void onCreate(){
        super.onCreate();
        mCurrencies = new Currencies(this);

        //LOAD account
        AccountManager accountManager = AccountManager.get(this);
        android.accounts.Account[] accounts = accountManager
                .getAccountsByType(getString(R.string.ACCOUNT_TYPE));

        if (accounts.length <  1) {
            Account account = new Account(
                    getString(R.string.app_name),
                    getString(R.string.ACCOUNT_TYPE));

            ContentResolver.setSyncAutomatically(account, getString(R.string.AUTHORITY), true);
            ContentResolver.setIsSyncable(account, getString(R.string.AUTHORITY), 1);
            AccountManager.get(this).addAccountExplicitly(account, null, null);

            accounts = accountManager
                    .getAccountsByType(getString(R.string.ACCOUNT_TYPE));
        }

    }
    public UcoinCurrencies currencies() { return mCurrencies; }
}
