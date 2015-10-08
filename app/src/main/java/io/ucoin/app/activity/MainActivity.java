package io.ucoin.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;

import io.ucoin.app.Application;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Long currencyId = preferences.getLong("currency_id", -1);
        if (currencyId == -1) {
            Intent intent = new Intent(this, CurrencyListActivity.class);
            startActivityForResult(intent, Application.ACTIVITY_CURRENCY_LIST);
        } else {
            Intent intent = new Intent(this, CurrencyActivity.class);
            intent.putExtra(BaseColumns._ID, currencyId);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) {
            finish();
            return;
        }

        Long currencyId = intent.getExtras().getLong(BaseColumns._ID);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("currency_id", currencyId);
        editor.apply();

        intent = new Intent(this, CurrencyActivity.class);
        intent.putExtra(BaseColumns._ID, currencyId);
        startActivity(intent);
        finish();
    }
}
