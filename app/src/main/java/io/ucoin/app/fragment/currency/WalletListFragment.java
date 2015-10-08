package io.ucoin.app.fragment.currency;

import android.app.Fragment;
import android.app.FragmentManager;
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
import io.ucoin.app.activity.CurrencyActivity;
import io.ucoin.app.adapter.WalletCursorAdapter;
import io.ucoin.app.content.DbProvider;
import io.ucoin.app.fragment.dialog.AddWalletDialogFragment;
import io.ucoin.app.fragment.wallet.WalletFragment;
import io.ucoin.app.sqlite.SQLiteTable;

public class WalletListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout mSwipeLayout;

    static public WalletListFragment newInstance(Long currencyId) {
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putLong(BaseColumns._ID, currencyId);
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
        ((CurrencyActivity) getActivity()).setDrawerIndicatorEnabled(true);

        return inflater.inflate(R.layout.fragment_wallet_list,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.wallet));

        WalletCursorAdapter walletCursorAdapter
                = new WalletCursorAdapter(getActivity(), null, 0);
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
        inflater.inflate(R.menu.toolbar_wallet_list, menu);
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

        Fragment fragment = WalletFragment.newInstance(id);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        android.R.animator.fade_in,
                        android.R.animator.fade_out,
                        android.R.animator.fade_in,
                        android.R.animator.fade_out)
                .replace(R.id.frame_content, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Long currencyId = args.getLong(BaseColumns._ID);

        String selection = SQLiteTable.Wallet.CURRENCY_ID + "=?";
        String[] selectionArgs = new String[]{currencyId.toString()};

        return new CursorLoader(
                getActivity(),
                DbProvider.WALLET_URI,
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
}
