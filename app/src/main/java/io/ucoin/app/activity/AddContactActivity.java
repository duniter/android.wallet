package io.ucoin.app.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import io.ucoin.app.Application;
import io.ucoin.app.R;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.http_api.WotLookup;
import io.ucoin.app.model.sql.sqlite.Currencies;
import io.ucoin.app.model.sql.sqlite.Currency;

public class AddContactActivity extends ActionBarActivity {
    private Toolbar mToolbar;
    private EditText mName;
    private EditText mPublicKey;
    private UcoinCurrency currency;


    public final static String CONTACT_PATH = "ucoin://";
    public final static String SEPARATOR1 = ":";
    public final static String SEPARATOR2 = "@";

    public static final int CONTACT = 10003;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_contact);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        ImageButton lookup = (ImageButton) findViewById(R.id.action_lookup);
        lookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddContactActivity.this,
                        LookupActivity.class);
                intent.putExtra(Application.EXTRA_CURRENCY_ID, getIntent().getExtras().getLong(Application.EXTRA_CURRENCY_ID));
                startActivityForResult(intent, Application.ACTIVITY_LOOKUP);
            }
        });

        ImageButton scanQrCode = (ImageButton) findViewById(R.id.action_scan_qrcode);
        scanQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(AddContactActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                integrator.initiateScan();
            }
        });

        try {
            setSupportActionBar(mToolbar);
        } catch (Throwable t) {
            Log.w("setSupportActionBar", t.getMessage());
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mName = (EditText) findViewById(R.id.name);
        mPublicKey = (EditText) findViewById(R.id.public_key);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mToolbar.inflateMenu(R.menu.toolbar_add_contact);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_add_contact:
                actionAddContact();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode != RESULT_OK)
            return;

        if(requestCode == Application.ACTIVITY_LOOKUP) {
            WotLookup.Result result = (WotLookup.Result)intent.getExtras().getSerializable(WotLookup.Result.class.getSimpleName());
            mName.setText(result.uids[0].uid);
            mPublicKey.setText(result.pubkey);
            currency = new Currency(this,result.id);
        } else if(requestCode == CONTACT){
            finish();
            Toast.makeText(this,
                    getString(R.string.contact_added),
                    Toast.LENGTH_SHORT).show();
        } else{
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanResult != null && !scanResult.getContents().isEmpty()) {
                String result = scanResult.getContents();
                String currenyName = result.substring(result.indexOf(":") + 1, result.length());
                currency = new Currencies(this).getByName(currenyName);
                mPublicKey.setText(result.substring(0, result.indexOf(":")));
            }
        }
    }

    public void actionAddContact() {
        String name = mName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, "Name is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        String publicKey = mPublicKey.getText().toString();
        if (publicKey.isEmpty()) {
            Toast.makeText(this, "public key is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        if(currency==null){
            Long currencyId = getIntent().getExtras().getLong(Application.EXTRA_CURRENCY_ID);
            currency = new Currency(this, currencyId);
        }
        currency.contacts().add(name, publicKey);

        askContactInPhone();
    }

    public String createUri(String name, String publicKey, String currency) {
        String result;
        if(currency!=null) {
            result = CONTACT_PATH.concat(name).concat(SEPARATOR1).concat(publicKey).concat(SEPARATOR2).concat(currency);
        }else{
            result = CONTACT_PATH.concat(name).concat(SEPARATOR1).concat(publicKey).concat(SEPARATOR2);
        }
        return result;
    }

    public void addNewContactInPhone(){
        String name = mName.getText().toString();
        String publicKey = mPublicKey.getText().toString();

        String url = createUri(name, publicKey, currency.name());

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);

        ArrayList<ContentValues> data = new ArrayList<ContentValues>();
        ContentValues row1 = new ContentValues();

        row1.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
        row1.put(ContactsContract.CommonDataKinds.Website.URL, url);
        //row1.put(ContactsContract.CommonDataKinds.Website.LABEL, "abc");
        row1.put(ContactsContract.CommonDataKinds.Website.TYPE, ContactsContract.CommonDataKinds.Website.TYPE_HOME);
        data.add(row1);
        intent.putExtra(ContactsContract.Intents.Insert.DATA, data);
        intent.putExtra("finishActivityOnSaveCompleted", true);
//              Uri dataUri = getActivity().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, row1);
        startActivityForResult(intent, CONTACT);
        //------------------------------- end of inserting contact in the phone
    }

    public void askContactInPhone(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Contact");
        alertDialogBuilder
                .setMessage("Do you want to save the contact on your phone ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addNewContactInPhone();
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}