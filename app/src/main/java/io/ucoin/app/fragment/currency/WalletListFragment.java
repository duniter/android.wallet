package io.ucoin.app.fragment.currency;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import io.ucoin.app.Application;
import io.ucoin.app.R;
import io.ucoin.app.UcoinUris;
import io.ucoin.app.activity.CurrencyActivity;
import io.ucoin.app.adapter.WalletCursorAdapter;
import io.ucoin.app.fragment.dialog.AddWalletDialogFragment;
import io.ucoin.app.sqlite.SQLiteTable;

public class WalletListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener{

    private static final String NEW_WALLET = "new_wallet";

    private SwipeRefreshLayout mSwipeLayout;
    private WalletCursorAdapter walletCursorAdapter;

    static public WalletListFragment newInstance(Long currencyId,boolean newWallet) {
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putLong(BaseColumns._ID, currencyId);
        newInstanceArgs.putBoolean(NEW_WALLET,newWallet);
        WalletListFragment fragment = new WalletListFragment();
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
        if(getActivity() instanceof CurrencyActivity){
            ((CurrencyActivity) getActivity()).setDrawerIndicatorEnabled(true);
        }

        return inflater.inflate(R.layout.fragment_wallet_list,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.wallet));
        setHasOptionsMenu(true);

        walletCursorAdapter
                = new WalletCursorAdapter(getActivity(), null, 0, getActivity());
        setListAdapter(walletCursorAdapter);

        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(getArguments().getBoolean(NEW_WALLET)) {
            inflater.inflate(R.menu.toolbar_wallet_list, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Long currencyId = getArguments().getLong(BaseColumns._ID);

        switch (item.getItemId()) {
            case R.id.action_new_wallet:
                actionNewWallet(currencyId);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if(getActivity() instanceof WalletItemClick){
            ((WalletItemClick)getActivity()).walletClick(walletCursorAdapter, position);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Long currencyId = args.getLong(BaseColumns._ID);

        String selection;
        String[] selectionArgs;

        if(currencyId.equals(new Long(-1))){
            selection = null;
            selectionArgs = null;
        }else{
            selection = SQLiteTable.Wallet.CURRENCY_ID + "=?";
            selectionArgs = new String[]{currencyId.toString()};
        }

        return new CursorLoader(
                getActivity(),
                UcoinUris.WALLET_URI,
                null, selection, selectionArgs,
                SQLiteTable.Wallet._ID + " ASC");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((WalletCursorAdapter) this.getListAdapter()).swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((WalletCursorAdapter) this.getListAdapter()).swapCursor(null);
    }

    private void actionNewWallet(Long currencyId) {
        AddWalletDialogFragment addWalletDialogFragment = AddWalletDialogFragment.newInstance(currencyId);
        addWalletDialogFragment.show(getFragmentManager(),
                addWalletDialogFragment.getClass().getSimpleName());
    }

    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(false);
        Application.requestSync();
    }

    public interface WalletItemClick  {
        void walletClick(WalletCursorAdapter walletCursorAdapter,int position);
        void showIdentity(Long walletId);
    }
}
