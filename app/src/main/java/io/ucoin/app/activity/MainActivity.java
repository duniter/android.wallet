package io.ucoin.app.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import io.ucoin.app.Application;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.sql.sqlite.Currencies;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoadContactsTask loadContactsTask = new LoadContactsTask();
        loadContactsTask.execute();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Long currencyId = preferences.getLong("currency_id", -2);
        if (currencyId == -2) {
            startCurrencyListActivity();
        } else if(currencyId == -1){
            startCurrencyActivity(currencyId);
        } else if (new Currencies(this).getById(currencyId) == null) {
            startCurrencyListActivity();
        } else {
            startCurrencyActivity(currencyId);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) {
            finish();
            return;
        }

        Long currencyId = intent.getExtras().getLong(Application.EXTRA_CURRENCY_ID);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("currency_id", currencyId);
        editor.apply();

        startCurrencyActivity(currencyId);
    }

    public void startCurrencyListActivity() {
        Intent intent = new Intent(this, CurrencyListActivity.class);
        startActivityForResult(intent, Application.ACTIVITY_CURRENCY_LIST);
    }

    public void startCurrencyActivity(Long currencyId) {
        Intent intent = new Intent(this, CurrencyActivity.class);
        intent.putExtra(Application.EXTRA_CURRENCY_ID, currencyId);
        startActivity(intent);
        finish();
    }


    public class LoadContactsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... param) {
            retrieveContacts(getContentResolver());
            return null;
        }

        private void retrieveContacts(ContentResolver contentResolver){
            String where = ContactsContract.Data.MIMETYPE + " = ? AND "
                    + ContactsContract.CommonDataKinds.Website.URL + " LIKE ?";
            String[] whereParams = new String[]{
                    ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE,
                    AddContactActivity.CONTACT_PATH+"%"};

            final Cursor cursor = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    where,
                    whereParams,
                    null);


            if (cursor == null){
                Log.i("TAG", "Cannot retrieve the contacts");
                return ;
            }

            if (cursor.moveToFirst()){
                do{
                    //final long id = Long.parseLong(cursor.getString(cursor.getColumnIndex(ContactsContract.Data._ID)));
                    //final String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                    String webSite = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                    webSite = webSite.substring(webSite.indexOf(AddContactActivity.CONTACT_PATH)+AddContactActivity.CONTACT_PATH.length());
                    String name = getName(webSite);
                    String pubkey = getPubKey(webSite);
                    String currencyName = getCurrencyName(webSite);
                    UcoinCurrency currency = new Currencies(getApplicationContext()).getByName(currencyName);
                    if(currency!=null) {
                        currency.contacts().add(name, pubkey);
                    }
                }
                while (cursor.moveToNext());
            }

            if (cursor.isClosed()){
                cursor.close();
            }

            return ;
        }

        private String getName(String uri){
            return uri.substring(
                    0,
                    uri.indexOf(AddContactActivity.SEPARATOR1));
        }

        private String getPubKey(String uri){
            // CONTACT_PATH.concat(name).concat(SEPARATOR1).concat(publicKey).concat(SEPARATOR2).concat(currency)
            return uri.substring(
                    uri.indexOf(AddContactActivity.SEPARATOR1)+AddContactActivity.SEPARATOR1.length(),
                    uri.indexOf(AddContactActivity.SEPARATOR2));
        }

        private String getCurrencyName(String uri){
            return uri.substring(
                    uri.indexOf(AddContactActivity.SEPARATOR2)+AddContactActivity.SEPARATOR2.length());
        }

        private Bitmap getPhoto(ContentResolver contentResolver, long contactId){
            Bitmap photo = null;
            final Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
            final Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            final Cursor cursor = contentResolver.query(photoUri, new String[] { ContactsContract.Contacts.Photo.DATA15 }, null, null, null);

            if (cursor == null)
            {
                return null;
            }

            if (cursor.moveToFirst() == true)
            {
                final byte[] data = cursor.getBlob(0);

                if (data != null)
                {
                    photo = BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                }
            }

            if (cursor.isClosed() == false)
            {
                cursor.close();
            }

            return photo;
        }

        private List<String> getWebSite(ContentResolver contentResolver, long contactId){
            List<String> result = new ArrayList<>();
            String where = ContactsContract.Data.CONTACT_ID + " = ? AND "
                    + ContactsContract.Data.MIMETYPE + " = ? AND "
                    + ContactsContract.CommonDataKinds.Website.URL + " LIKE ?";
            String[] whereParams = new String[]{contactId+"",
                    ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE,
                    AddContactActivity.CONTACT_PATH+"%"};
            Cursor webcur = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, where, whereParams, null);
            if (webcur.moveToFirst()) {
                do {
                    result.add(webcur.getString(webcur.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));
                }
                while (webcur.moveToNext());
            }
            webcur.close();

            return result;
        }
    }

}
