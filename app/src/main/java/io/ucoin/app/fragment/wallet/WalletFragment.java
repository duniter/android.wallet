package io.ucoin.app.fragment.wallet;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;

import io.ucoin.app.Application;
import io.ucoin.app.R;
import io.ucoin.app.UcoinUris;
import io.ucoin.app.activity.CurrencyActivity;
import io.ucoin.app.activity.TransferActivity;
import io.ucoin.app.adapter.OperationSectionCursorAdapter;
import io.ucoin.app.fragment.dialog.QrCodeDialogFragment;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.sql.sqlite.Wallet;
import io.ucoin.app.sqlite.SQLiteTable;
import io.ucoin.app.sqlite.SQLiteView;

public class WalletFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        ImageButton.OnClickListener{

    private static final String WALLET_ID = "wallet_id";
    private static int WALLET_LOADER_ID = 0;
    private static int OPERATION_LOADER_ID = 1;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_wallet,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        ((CurrencyActivity) getActivity()).setDrawerIndicatorEnabled(false);

        getLoaderManager().initLoader(WALLET_LOADER_ID, getArguments(), this);


        OperationSectionCursorAdapter operationSectionCursorAdapter
                = new OperationSectionCursorAdapter(getActivity(), null, 0);
        setListAdapter(operationSectionCursorAdapter);
        getLoaderManager().initLoader(OPERATION_LOADER_ID, getArguments(), this);

        ImageButton transferButton = (ImageButton) view.findViewById(R.id.transfer_button);
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransferActivity.class);
                UcoinWallet wallet = new Wallet(getActivity(), getArguments().getLong(WALLET_ID));
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

    private void refreshWalletInfo(Cursor data) {
        View view = getView();
        data.moveToNext();
        TextView alias = (TextView) view.findViewById(R.id.alias);
        alias.setText(data.getString(data.getColumnIndex(SQLiteView.Wallet.ALIAS)));

        //TextView publicKey = (TextView) view.findViewById(R.id.public_key);
        TextView quantitativeAmount = (TextView) view.findViewById(R.id.qt_amount);
        TextView relativeAmount = (TextView) view.findViewById(R.id.relative_amount);

        //publicKey.setText(data.getString(data.getColumnIndex(SQLiteView.Wallet.PUBLIC_KEY)));
        StringBuilder sb = new StringBuilder();
        DecimalFormat formatter = new DecimalFormat("#,###");
        sb.append(formatter.format(data.getLong(data.getColumnIndex(SQLiteView.Wallet.QUANTITATIVE_AMOUNT))));
        sb.append(" ");
        sb.append(data.getString(data.getColumnIndex(SQLiteView.Wallet.CURRENCY_NAME)));
        quantitativeAmount.setText(sb.toString());

        sb.setLength(0);
        sb.append(String.format("%.8f", data.getDouble(data.getColumnIndex(SQLiteView.Wallet.RELATIVE_AMOUNT))));
        sb.append(" ");
        sb.append(getString(R.string.UD));
        relativeAmount.setText(sb.toString());
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
            refreshWalletInfo(data);
        } else {
            ((OperationSectionCursorAdapter)this.getListAdapter()).swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == OPERATION_LOADER_ID) {
            ((OperationSectionCursorAdapter)this.getListAdapter()).swapCursor(null);
        }
    }

    @Override
    public void onClick(View v) {

    }
}
