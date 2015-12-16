package io.ucoin.app.activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.ucoin.app.Application;
import io.ucoin.app.R;
import io.ucoin.app.enumeration.SourceState;
import io.ucoin.app.enumeration.TxDirection;
import io.ucoin.app.fragment.dialog.ConverterDialog;
import io.ucoin.app.fragment.dialog.ListTransferDialog;
import io.ucoin.app.model.UcoinContact;
import io.ucoin.app.model.UcoinContacts;
import io.ucoin.app.model.UcoinEndpoint;
import io.ucoin.app.model.UcoinSource;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.UcoinWallets;
import io.ucoin.app.model.document.Transaction;
import io.ucoin.app.model.http_api.TxHistory;
import io.ucoin.app.model.http_api.WotLookup;
import io.ucoin.app.model.sql.sqlite.Contacts;
import io.ucoin.app.model.sql.sqlite.Currency;
import io.ucoin.app.model.sql.sqlite.Wallets;
import io.ucoin.app.service.UnitFormat;
import io.ucoin.app.technical.crypto.AddressFormatException;

public class TransferActivity extends ActionBarActivity {

    /*
        https://github.com/ucoin-io/ucoin/blob/master/doc/Protocol.md#validity-1
        Field Comment is a string of maximum 255 characters, exclusively composed of
        alphanumeric characters, space, - _ : / ; * [ ] ( ) ? ! ^ + = @ & ~ # { } | \ < > % .
    */
    private static final String COMMENT_REGEX = "^[\\p{Alnum}\\p{Space}{\\-_:/;\\*\\[\\]\\(\\)\\?\\!\\^\\+=@&~\\#\\{\\}\\|\\\\<>%\\.}]{0,255}";
    private static final String AMOUNT_REGEX = "^[0-9]{1,3}(\\.[0-9]{0,8})?$";
    private static final String PUBLIC_KEY_REGEX = "[123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]{43,44}$";
    public static final String SEARCH_IDENTITY = "search_identity";
    private TextView mWalletAlias;
    private TextView mWalletAmount;
    private TextView mWalletDefaultAmount;
    private Button mContact;
    private EditText mReceiverPublicKey;
    private TextView defaultAmount;
    private EditText amount;
    private EditText mComment;
    private MenuItem mTransferMenuItem;
    private Long mcurrencyId;
    private Spinner spinnerUnit;
    private int unit;
    private int defaultUnit;
    private Currency currency;

    private UcoinWallet walletSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transfer);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        unit = preferences.getInt(Application.UNIT,Application.UNIT_CLASSIC);
        defaultUnit = preferences.getInt(Application.UNIT_DEFAULT, Application.UNIT_CLASSIC);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mcurrencyId = getIntent().getExtras().getLong(Application.EXTRA_CURRENCY_ID);

        try {
            setSupportActionBar(toolbar);
        } catch (Throwable t) {
            Log.w("setSupportActionBar", t.getMessage());
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RelativeLayout mWallet = (RelativeLayout) findViewById(R.id.wallet);
        mWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionWallet();
            }
        });

        mContact = (Button) findViewById(R.id.contact);
        mContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionContact();
            }
        });

        ImageButton mCalculate = (ImageButton) findViewById(R.id.action_calcul);
        mCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionAmount();
            }
        });

        ImageButton mSearchButton = (ImageButton) findViewById(R.id.action_lookup);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionLookup();
            }
        });

        ImageButton scanQrCode = (ImageButton) findViewById(R.id.action_scan_qrcode);
        scanQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionScanQrCode();
            }
        });

        mWalletAlias = (TextView) findViewById(R.id.wallet_alias);
        mWalletAmount = (TextView) findViewById(R.id.wallet_amount);
        mWalletDefaultAmount = (TextView) findViewById(R.id.wallet_default_amount);

        mReceiverPublicKey = (EditText) findViewById(R.id.receiver_public_key);

        amount = (EditText) findViewById(R.id.amount);
        defaultAmount = (TextView) findViewById(R.id.default_amount);
        amount.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                majDefaultAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        spinnerUnit = (Spinner) findViewById(R.id.spinner_unit);
        if(unit==Application.UNIT_TIME){
            List list = Arrays.asList(getResources().getStringArray(R.array.list_unit_time));
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            spinnerUnit.setAdapter(dataAdapter);
            spinnerUnit.setSelection(UnitFormat.MINUTE);
            spinnerUnit.setVisibility(View.VISIBLE);
            spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    majDefaultAmount();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else{
            spinnerUnit.setVisibility(View.GONE);
        }

        mComment = (EditText) findViewById(R.id.comment);

        actionAfterWalletSelected();
    }

    private void majDefaultAmount(){
        String val = amount.getText().toString();
        if(val.equals("") || val.equals(" ") || val.equals(".")){
            val="0";
        }
        if(val.substring(0,1).equals(".")){
            val="0"+val;
        }
        DecimalFormat formatter = new DecimalFormat("#,###");
        if(unit!=defaultUnit) {
            defaultAmount.setVisibility(View.VISIBLE);
            Double res=0.0;
            if(unit==Application.UNIT_TIME){
                val = String.valueOf(
                        UnitFormat.toSecond(
                                Double.parseDouble(val),
                                spinnerUnit.getSelectedItemPosition()
                        )
                );
            }
            long coin = toClassical(Double.parseDouble(val));
            long mUd = Double.valueOf(
                    walletSelected.quantitativeAmount()/walletSelected.relativeAmount())
                    .longValue();
            switch (defaultUnit) {
                case Application.UNIT_CLASSIC:
                    defaultAmount.setText(formatter.format(coin));
                    break;
                case Application.UNIT_DU:
                    res = (double)coin / mUd;
                    defaultAmount.setText(String.format("%.8f",res));
                    break;
                case Application.UNIT_TIME:
                    res = (double)coin*currency.dt()/mUd;
                    defaultAmount.setText(UnitFormat.timeFormatter(this, res));
                    break;
            }
        }else{
            defaultAmount.setVisibility(View.GONE);
        }
    }

    private Long toClassical(Double val){
        Long res = null;
        long mUd;
        if(currency!=null) {
            switch (unit) {
                case Application.UNIT_CLASSIC:
                    res = val.longValue();
                    break;
                case Application.UNIT_DU:
                    mUd = Double.valueOf(
                            walletSelected.quantitativeAmount()/walletSelected.relativeAmount())
                            .longValue();
                    res = Double.valueOf(val*mUd).longValue();
                    break;
                case Application.UNIT_TIME:
                    mUd = Double.valueOf(
                            walletSelected.quantitativeAmount()/walletSelected.relativeAmount())
                            .longValue();
                    res = Double.valueOf(val*mUd/currency.dt()).longValue();
                    break;
            }
        }
        return res;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_transfer, menu);
        mTransferMenuItem = menu.findItem(R.id.action_transfer);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_transfer:
                actionTransfer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK)
            return;

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (requestCode == Application.ACTIVITY_LOOKUP) {
            getIntent().putExtra(Application.EXTRA_IS_CONTACT,false);
            WotLookup.Result result = (WotLookup.Result) intent.getExtras().getSerializable(WotLookup.Result.class.getSimpleName());
            if (result.pubkey.matches(PUBLIC_KEY_REGEX)) {
                mReceiverPublicKey.setText(result.pubkey);
                mContact.setText(result.uids[0].uid);
            } else {
                mReceiverPublicKey.setText("");
            }
        } else {
            if (scanResult.getContents().matches(PUBLIC_KEY_REGEX)) {
                mContact.setText("Find by Qr Code");
                mReceiverPublicKey.setText(scanResult.getContents());
            } else
                mReceiverPublicKey.setText("");
        }
    }

    public void showDialog(int type,Cursor list){
        DialogItemClickListener dialogWallet = new DialogItemClickListener() {
            @Override
            public void onClick(Long walletId) {
                getIntent().putExtra(Application.EXTRA_WALLET_ID,walletId);
                actionAfterWalletSelected();
            }
        };

        DialogItemClickListener dialogContact = new DialogItemClickListener() {
            @Override
            public void onClick(Long contactId) {
                getIntent().putExtra(Application.EXTRA_CONTACT_ID,contactId);
                getIntent().putExtra(Application.EXTRA_IS_CONTACT,true);
                actionAfterWalletSelected();
            }
        };

        DialogFragment dialog= null;
        switch (type){
            case ListTransferDialog.TYPE_WALLET:
                dialog = new ListTransferDialog(type,list,dialogWallet);
                break;
            case ListTransferDialog.TYPE_CONTACT:
                dialog = new ListTransferDialog(type,list,dialogContact);
                break;
            case ListTransferDialog.TYPE_CURRENCY:
                if(walletSelected!=null){

                    long mUniversalDividend = Double.valueOf(
                            walletSelected.quantitativeAmount()/walletSelected.relativeAmount())
                            .longValue();
                    dialog = new ConverterDialog(
                            mUniversalDividend,
                            currency.dt(), amount, spinnerUnit);
                }

                break;
        }
        if(dialog!=null){
            dialog.show(getFragmentManager(), "listDialog");
        }
    }

    public void actionAfterWalletSelected(){
        Long walletId = getIntent().getExtras().getLong(Application.EXTRA_WALLET_ID);
        UcoinWallets wallets = new Wallets(Application.getContext(),mcurrencyId);
        walletSelected = wallets.getById(walletId);

        mWalletAlias.setText(walletSelected.alias());
        DecimalFormat formatter;

        switch (unit){
            case Application.UNIT_CLASSIC:
                formatter = new DecimalFormat("#,###");
                mWalletAmount.setText(formatter.format(walletSelected.quantitativeAmount()));
                break;
            case Application.UNIT_DU:
                mWalletAmount.setText(String.format("%.8f", walletSelected.relativeAmount()));
                break;
            case Application.UNIT_TIME:
                mWalletAmount.setText(UnitFormat.timeFormatter(this, walletSelected.timeAmount()));
                break;
        }
        if(unit!=defaultUnit) {
            mWalletDefaultAmount.setVisibility(View.VISIBLE);
            switch (defaultUnit) {
                case Application.UNIT_CLASSIC:
                    formatter = new DecimalFormat("#,###");
                    mWalletDefaultAmount.setText(formatter.format(walletSelected.quantitativeAmount()));
                    break;
                case Application.UNIT_DU:
                    mWalletDefaultAmount.setText(String.format("%.8f", walletSelected.relativeAmount()));
                    break;
                case Application.UNIT_TIME:
                    mWalletDefaultAmount.setText(UnitFormat.timeFormatter(this, walletSelected.timeAmount()));
                    break;
            }
        }else{
            mWalletDefaultAmount.setVisibility(View.GONE);
        }

        setTitle(getResources().getString(R.string.transfer) +
                " " + walletSelected.currency().name());
        currency = new Currency(this,walletSelected.currencyId());
        //mQuantitativeUD = new BigDecimal(data.getString(data.getColumnIndex(SQLiteView.Wallet.UD_VALUE)));

        //TODO fma metre les valeurs des amount a jour

        boolean isContact = getIntent().getExtras().getBoolean(Application.EXTRA_IS_CONTACT);

        if(isContact){
            Long contactId = getIntent().getExtras().getLong(Application.EXTRA_CONTACT_ID);
            UcoinContacts contacts = new Contacts(Application.getContext(),mcurrencyId);
            UcoinContact contact = contacts.getById(contactId);
            mContact.setText(contact.name());
            mReceiverPublicKey.setText(contact.publicKey());
        }
    }

    public interface DialogItemClickListener{
        void onClick(Long id);
    }

    public void actionWallet(){
        UcoinWallets wallets = new Wallets(Application.getContext(),mcurrencyId);
        showDialog(ListTransferDialog.TYPE_WALLET,wallets.getbyCurrency());
    }

    public void actionContact(){
        UcoinContacts contacts = new Contacts(Application.getContext(),mcurrencyId);
        showDialog(ListTransferDialog.TYPE_CONTACT,contacts.getbyCurrency());
    }

    public void actionAmount(){
        showDialog(ListTransferDialog.TYPE_CURRENCY, (Cursor) null);
    }

    public void actionLookup() {
        Intent intent = new Intent(this, LookupActivity.class);
        Long currencyId = currency.id();
        intent.putExtra(Application.EXTRA_CURRENCY_ID, currencyId);
        intent.putExtra(SEARCH_IDENTITY, mReceiverPublicKey.getText().toString());
        startActivityForResult(intent, Application.ACTIVITY_LOOKUP);
    }

    public void actionScanQrCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        integrator.initiateScan();

    }

    public boolean actionTransfer() {
        Long qtAmount;
        String receiverPublicKey = mReceiverPublicKey.getText().toString();
        String comment;

        if ((receiverPublicKey = validatePublicKey(receiverPublicKey)) == null) return false;
        if ((qtAmount = validateAmount()) == null) return false;
        if ((comment = validateComment()) == null) return false;

        final UcoinWallet wallet = walletSelected;

        //Create Tx
        final Transaction transaction = new Transaction();
        transaction.setCurrency(wallet.currency().name());
        transaction.setComment(comment);
        transaction.addIssuer(wallet.publicKey());


        //check funds
        if (qtAmount > wallet.quantitativeAmount()) {
            defaultAmount.setError(getResources().getString(R.string.insufficient_funds));
            return false;
        }

        //set inputs
        long cumulativeAmount = 0;
        for (UcoinSource source : wallet.sources().getByState(SourceState.AVAILABLE)) {
            transaction.addInput(source);
            source.setState(SourceState.CONSUMED);
            cumulativeAmount += source.amount();
            if (cumulativeAmount > qtAmount) {
                break;
            }
        }

        // set outputs
        // a public address can appear juste once, hence if we send from a wallet to itself,
        // we send the total amount
        if (receiverPublicKey.equals(wallet.publicKey())) {
            transaction.addOuput(receiverPublicKey, cumulativeAmount);
        } else {
            transaction.addOuput(receiverPublicKey, qtAmount);
            Long refundAmount = cumulativeAmount - qtAmount;
            if (refundAmount > 0) {
                transaction.addOuput(wallet.publicKey(), refundAmount);
            }
        }

        //todo prompt for password
        try {
            transaction.addSignature(transaction.sign(wallet.privateKey()));
        } catch (AddressFormatException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        UcoinEndpoint endpoint = wallet.currency().peers().at(0).endpoints().at(0);
        String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/tx/process/";

        mTransferMenuItem.setEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_send_grey600_24dp, null);
        mTransferMenuItem.setIcon(drawable);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        TxHistory.PendingTx tx = TxHistory.PendingTx.fromJson(response);
                        wallet.txs().add(tx, TxDirection.OUT);
                        Toast.makeText(TransferActivity.this, getResources().getString(R.string.transaction_sent), Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTransferMenuItem.setEnabled(true);
                        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_send_white_24dp, null);
                        mTransferMenuItem.setIcon(drawable);

                        for(UcoinSource source : transaction.getSources()) {
                            source.setState(SourceState.AVAILABLE);
                        }

                        if (error instanceof NoConnectionError) {
                            Toast.makeText(Application.getContext(),
                                    getResources().getString(R.string.no_connection),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Application.getContext(), error.toString(), Toast.LENGTH_LONG).show();
                            Log.d("TRANSFERACTIVITY", new String(error.networkResponse.data));
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("transaction", transaction.toString());
                return params;
            }
        };
        request.setTag(this);
        Application.getRequestQueue().add(request);

        return true;
    }

    private String validatePublicKey(String publicKey) {
        if (!publicKey.matches(PUBLIC_KEY_REGEX)) {
            mReceiverPublicKey.setError(getResources().getString(R.string.public_key_is_not_valid));
            mReceiverPublicKey.requestFocus();
            return null;
        }else{
            mReceiverPublicKey.setError(null);
        }
        return publicKey;
    }

    private Long validateAmount() {
        if (amount.getText().toString().isEmpty()) {
            amount.setError(getResources().getString(R.string.amount_is_empty));
            amount.requestFocus();
            return null;
        }else{
            amount.setError(null);
        }

        Double res = Double.parseDouble(amount.getText().toString());
        return toClassical(res);
    }

    private String validateComment() {
//        if (!comment.matches(COMMENT_REGEX)) {
//            mComment.setError(getResources().getString(R.string.comment_is_not_valid));
//            return null;
//        }

        return mComment.getText().toString();
    }
}
