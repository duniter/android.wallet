package io.ucoin.app.fragment.wallet;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.math.BigInteger;

import io.ucoin.app.Application;
import io.ucoin.app.R;
import io.ucoin.app.UcoinUris;
import io.ucoin.app.activity.CurrencyActivity;
import io.ucoin.app.activity.TransferActivity;
import io.ucoin.app.adapter.OperationSectionCursorAdapter;
import io.ucoin.app.fragment.dialog.QrCodeDialogFragment;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinEndpoint;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.http_api.TxHistory;
import io.ucoin.app.model.sql.sqlite.Currencies;
import io.ucoin.app.model.sql.sqlite.Wallet;
import io.ucoin.app.service.Format;
import io.ucoin.app.sqlite.SQLiteTable;
import io.ucoin.app.sqlite.SQLiteView;

public class WalletFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String WALLET_ID = "wallet_id";
    private static int WALLET_LOADER_ID = 0;
    private static int OPERATION_LOADER_ID = 1;

    private SwipeRefreshLayout mSwipeLayout;

    public static WalletFragment newInstance(Long walletId) {
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putLong(WALLET_ID, walletId);
        WalletFragment fragment = new WalletFragment();
        fragment.setArguments(newInstanceArgs);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Long walletId = getArguments().getLong(WALLET_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if(savedInstanceState!=null){
            getArguments().putLong(WALLET_ID,savedInstanceState.getLong(WALLET_ID));
        }

        return inflater.inflate(R.layout.fragment_wallet,
                container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(WALLET_ID,getArguments().getLong(WALLET_ID));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        ((CurrencyActivity) getActivity()).setDrawerIndicatorEnabled(false);

        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeLayout.setOnRefreshListener(this);

        TextView emptyView = (TextView) view.findViewById(android.R.id.empty);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeLayout.setRefreshing(true);
                onRefresh();
            }
        });

        getLoaderManager().initLoader(WALLET_LOADER_ID, getArguments(), this);
        UcoinWallet wallet = new Wallet(getActivity(), getArguments().getLong(WALLET_ID));
        OperationSectionCursorAdapter operationSectionCursorAdapter
                = new OperationSectionCursorAdapter(getActivity(), null, 0,wallet.udValue(),wallet.currency().dt());
        setListAdapter(operationSectionCursorAdapter);
        getLoaderManager().initLoader(OPERATION_LOADER_ID, getArguments(), this);


        ImageButton transferButton = (ImageButton) view.findViewById(R.id.transfer_button);
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransferActivity.class);
                UcoinWallet wallet = new Wallet(getActivity(), getArguments().getLong(WALLET_ID));
                Long currencyId = getActivity().getIntent().getExtras().getLong(Application.EXTRA_CURRENCY_ID);
                intent.putExtra(Application.EXTRA_CURRENCY_ID, currencyId);
                intent.putExtra(Application.EXTRA_WALLET_ID, wallet.id());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_wallet, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_qrcode:
                showQrCode();
                return true;
            case R.id.action_delete:
                //too preform deletion asynchronously
                UcoinWallet wallet = new Wallet(getActivity(), getArguments().getLong(WALLET_ID));
                wallet.delete();
                getFragmentManager().popBackStack();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showQrCode() {
        Long walletId = getArguments().getLong(WALLET_ID);
        QrCodeDialogFragment fragment = QrCodeDialogFragment.newInstance(walletId);
        fragment.show(getFragmentManager(),
                fragment.getClass().getSimpleName());

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Long walletId = args.getLong(WALLET_ID);

        if (id == WALLET_LOADER_ID) {
            String selection = SQLiteTable.Wallet._ID + "=?";
            String[] selectionArgs = new String[]{walletId.toString()};

            return new CursorLoader(
                    getActivity(),
                    UcoinUris.WALLET_URI,
                    null, selection, selectionArgs,
                    null);
        } else {
            String selection = SQLiteView.Operation.WALLET_ID + "=?";
            String selectionArgs[] = new String[]{
                    walletId.toString()
            };

            return new CursorLoader(
                    getActivity(),
                    UcoinUris.OPERATION_URI,
                    null, selection, selectionArgs,
                    SQLiteView.Operation.TIME + " DESC");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == WALLET_LOADER_ID) {

            View view = getView();
            data.moveToNext();
            TextView alias = (TextView) view.findViewById(R.id.alias);
            alias.setText(data.getString(data.getColumnIndex(SQLiteView.Wallet.ALIAS)));

            //TextView publicKey = (TextView) view.findViewById(R.id.public_key);
            TextView defaultAmount = (TextView) view.findViewById(R.id.default_amount);
            TextView amount = (TextView) view.findViewById(R.id.relative_amount);

            UcoinCurrency currency = new Currencies(getActivity()).getByName(
                    data.getString(data.getColumnIndex(SQLiteView.Wallet.CURRENCY_NAME)));

            //publicKey.setText(data.getString(data.getColumnIndex(SQLiteView.Wallet.PUBLIC_KEY)));
            StringBuilder sb = new StringBuilder();

            Format.changeUnit(
                    getActivity(),
                    new BigInteger(data.getString(data.getColumnIndex(SQLiteView.Wallet.QUANTITATIVE_AMOUNT))),
                    new BigInteger(data.getString(data.getColumnIndex(SQLiteView.Wallet.UD_VALUE))),
                    currency.dt(),
                    amount,
                    defaultAmount,
                    "");
        } else if(loader.getId() == OPERATION_LOADER_ID){
            ((OperationSectionCursorAdapter) this.getListAdapter()).swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == OPERATION_LOADER_ID) {
            ((OperationSectionCursorAdapter) this.getListAdapter()).swapCursor(null);
        }
    }

    @Override
    public void onRefresh() {
        final UcoinWallet wallet = new Wallet(getActivity(), getArguments().getLong(WALLET_ID));
        UcoinEndpoint endpoint = wallet.currency().peers().at(0).endpoints().at(0);
        String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/tx/history/";
        url += wallet.publicKey();
/*
        UcoinTx lastTx = wallet.txs().getLastConfirmedTx();
        if (lastTx != null) {
            url += "/times/" + lastTx.time() + 1 + "/" + Application.getCurrentTime();
        }
*/
        StringRequest request = new StringRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final TxHistory history = TxHistory.fromJson(response);
                        mSwipeLayout.setRefreshing(false);
                        Thread t = new Thread() {
                            @Override
                            public void run() {
                                wallet.txs().add(history);
                            }
                        };
                        t.start();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mSwipeLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(getActivity()).add(request);
    }
}