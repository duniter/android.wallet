package io.ucoin.app.fragment.common;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import io.ucoin.app.ListFragment;
import io.ucoin.app.R;
import io.ucoin.app.adapter.TxCursorAdapter;
import io.ucoin.app.content.Provider;
import io.ucoin.app.enums.TxDirection;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.sqlite.SQLiteTable;


public class TxListFragment extends ListFragment
implements LoaderManager.LoaderCallbacks<Cursor>    {

    static public TxListFragment newInstance(UcoinWallet wallet, TxDirection type) {
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putParcelable(UcoinWallet.class.getSimpleName(), wallet);
        newInstanceArgs.putSerializable(TxDirection.class.getSimpleName(), type);
        TxListFragment fragment = new TxListFragment();
        fragment.setArguments(newInstanceArgs);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_tx_list,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TxCursorAdapter txCursorAdapter
                = new TxCursorAdapter(getActivity(), null, 0);
        setListAdapter(txCursorAdapter);
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        UcoinWallet wallet = args.getParcelable(UcoinWallet.class.getSimpleName());
        TxDirection type = (TxDirection) args.getSerializable(TxDirection.class.getSimpleName());

        String selection = SQLiteTable.Tx.WALLET_ID + "=? AND " + SQLiteTable.Tx.DIRECTION + "=?";
        String selectionArgs[] = new String[]{
                wallet.id().toString(),
                type.name()
        };

        return new CursorLoader(
                getActivity(),
                Provider.TX_URI,
                null, selection, selectionArgs,
                SQLiteTable.Tx.BLOCK +" DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((TxCursorAdapter)this.getListAdapter()).swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((TxCursorAdapter)this.getListAdapter()).swapCursor(null);
    }
}
