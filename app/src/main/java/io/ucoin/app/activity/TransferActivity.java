package io.ucoin.app.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import io.ucoin.app.Application;
import io.ucoin.app.R;
import io.ucoin.app.UcoinUris;
import io.ucoin.app.enumeration.SourceState;
import io.ucoin.app.enumeration.TxDirection;
import io.ucoin.app.model.UcoinEndpoint;
import io.ucoin.app.model.UcoinSource;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.document.Transaction;
import io.ucoin.app.model.http_api.TxHistory;
import io.ucoin.app.model.http_api.WotLookup;
import io.ucoin.app.model.sql.sqlite.Wallet;
import io.ucoin.app.sqlite.SQLiteTable;
import io.ucoin.app.sqlite.SQLiteView;
import io.ucoin.app.technical.crypto.AddressFormatException;

public class TransferActivity extends ActionBarActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /*
        https://github.com/ucoin-io/ucoin/blob/master/doc/Protocol.md#validity-1
        Field Comment is a string of maximum 255 characters, exclusively composed of
        alphanumeric characters, space, - _ : / ; * [ ] ( ) ? ! ^ + = @ & ~ # { } | \ < > % .
    */
    private static final String COMMENT_REGEX = "^[\\p{Alnum}\\p{Space}{\\-_:/;\\*\\[\\]\\(\\)\\?\\!\\^\\+=@&~\\#\\{\\}\\|\\\\<>%\\.}]{0,255}";
    private static final String AMOUNT_REGEX = "^[0-9]{1,3}(\\.[0-9]{0,8})?$";
    private static final String PUBLIC_KEY_REGEX = "[123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]{43,44}$";
    private TextView mWalletAlias;
    private TextView mWalletRelAmount;
    private TextView mWalletQtAmount;
    private TextView mReceiverUid;
    private EditText mReceiverPublicKey;
    private EditText mRelAmount;
    private TextView mQtAmount;
    private EditText mComment;
    private BigDecimal mQuantitativeUD;
    private Cursor mWalletCursor;
    private ImageButton mLookupButton;
    private MenuItem mTransferMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transfer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        try {
            setSupportActionBar(toolbar);
        } catch (Throwable t) {
            Log.w("setSupportActionBar", t.getMessage());
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLookupButton = (ImageButton) findViewById(R.id.action_lookup);
        mLookupButton.setOnClickListener(new View.OnClickListener() {
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
        mWalletRelAmount = (TextView) findViewById(R.id.wallet_relative_amount);
        mWalletQtAmount = (TextView) findViewById(R.id.wallet_qt_amount);
        mWalletAlias = (TextView) findViewById(R.id.wallet_alias);
        mReceiverUid = (TextView) findViewById(R.id.receiver_uid);
        mReceiverPublicKey = (EditText) findViewById(R.id.receiver_public_key);
        mQtAmount = (TextView) findViewById(R.id.qt_amount);
        mRelAmount = (EditText) findViewById(R.id.rel_amount);
        mRelAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();

                if (text.isEmpty()) {
                    mQtAmount.setText("");
                    return;
                }

                if (!text.matches(AMOUNT_REGEX)) {
                    s.delete(s.length() - 1, s.length());
                    return;
                }


                if (mQuantitativeUD != null) {
                    BigDecimal amount = new BigDecimal(s.toString());
                    amount = amount.multiply(mQuantitativeUD);

                    DecimalFormat formatter = new DecimalFormat("#,###");
                    mQtAmount.setText(formatter.format(amount.longValue()));
                }
            }
        });
        mComment = (EditText) findViewById(R.id.comment);
        mComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();

                if (text.isEmpty()) {
                    mQtAmount.setText("");
                    return;
                }

                if (!text.matches(COMMENT_REGEX)) {
                    s.delete(s.length() - 1, s.length());
                    return;
                }
            }
        });

        getLoaderManager().initLoader(0, getIntent().getExtras(), this);
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
            WotLookup.Result result = (WotLookup.Result) intent.getExtras().getSerializable(WotLookup.Result.class.getSimpleName());
            if (result.pubkey.matches(PUBLIC_KEY_REGEX)) {

                mReceiverUid.setVisibility(View.VISIBLE);
                mReceiverPublicKey.setText(result.pubkey);
                mReceiverUid.setText(result.uids[0].uid);
            } else {
                mReceiverUid.setVisibility(View.GONE);
                mReceiverPublicKey.setText("");
            }
        } else {
            mReceiverUid.setVisibility(View.GONE);
            if (scanResult.getContents().matches(PUBLIC_KEY_REGEX)) {
                mReceiverPublicKey.setText(scanResult.getContents());
            } else
                mReceiverPublicKey.setText("");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Long walletId = args.getLong(Application.EXTRA_WALLET_ID);
        String selection;
        String selectionArgs[];

        selection = SQLiteView.Wallet._ID + "=?";
        selectionArgs = new String[]{walletId.toString()};

        return new CursorLoader(
                this,
                UcoinUris.WALLET_URI,
                null, selection, selectionArgs,
                SQLiteTable.Wallet._ID + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            mWalletCursor = data;
            mWalletAlias.setText(data.getString(data.getColumnIndex(SQLiteView.Wallet.ALIAS)));
            mWalletRelAmount.setText(String.format("%.8f", data.getDouble(data.getColumnIndex(SQLiteView.Wallet.RELATIVE_AMOUNT))));
            DecimalFormat formatter = new DecimalFormat("#,###");
            mWalletQtAmount.setText(formatter.format(data.getLong(data.getColumnIndex(SQLiteView.Wallet.QUANTITATIVE_AMOUNT))));
            mQuantitativeUD = new BigDecimal(data.getString(data.getColumnIndex(SQLiteView.Wallet.UD_VALUE)));
            setTitle(getResources().getString(R.string.transfer) +
                    " "
                    + data.getString(data.getColumnIndex(SQLiteView.Wallet.CURRENCY_NAME)));
            mLookupButton.setEnabled(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLookupButton.setEnabled(false);
    }

    public void actionLookup() {
        Intent intent = new Intent(this, LookupActivity.class);
        Long currencyId = mWalletCursor.getLong(mWalletCursor.getColumnIndex(SQLiteView.Wallet.CURRENCY_ID));
        intent.putExtra(Application.EXTRA_CURRENCY_ID, currencyId);
        startActivityForResult(intent, Application.ACTIVITY_LOOKUP);
    }

    public void actionScanQrCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        integrator.initiateScan();

    }

    public boolean actionTransfer() {
        Long walletId = getIntent().getExtras().getLong(Application.EXTRA_WALLET_ID);
        Long qtAmount;
        String receiverPublicKey = mReceiverPublicKey.getText().toString();
        String comment;

        if ((receiverPublicKey = validatePublicKey(receiverPublicKey)) == null) return false;
        if ((qtAmount = validateAmount(mRelAmount.getText().toString())) == null) return false;
        if ((comment = validateComment(mComment.getText().toString())) == null) return false;

        final UcoinWallet wallet = new Wallet(this, walletId);

        //Create Tx
        final Transaction transaction = new Transaction();
        transaction.setCurrency(wallet.currency().name());
        transaction.setComment(comment);
        transaction.addIssuer(wallet.publicKey());


        //check funds
        if (qtAmount > wallet.quantitativeAmount()) {
            mRelAmount.setError(getResources().getString(R.string.insufficient_funds));
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
            return null;
        }
        return publicKey;
    }

    private Long validateAmount(String relativeAmount) {
        if (relativeAmount.isEmpty()) {
            mRelAmount.setError(getResources().getString(R.string.amount_is_empty));
            return null;
        }

        BigDecimal amount = new BigDecimal(relativeAmount.toString());
        amount = amount.multiply(mQuantitativeUD);
        return amount.longValue();
    }

    private String validateComment(String comment) {
        if (!comment.matches(COMMENT_REGEX)) {
            mComment.setError(getResources().getString(R.string.comment_is_not_valid));
            return null;
        }

        return comment;
    }
}
