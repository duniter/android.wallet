package io.ucoin.app.fragment.currency;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import io.ucoin.app.ListFragment;
import io.ucoin.app.R;
import io.ucoin.app.adapter.WalletCursorAdapter;
import io.ucoin.app.content.Provider;
import io.ucoin.app.fragment.AddWalletDialogFragment;
import io.ucoin.app.fragment.wallet.WalletFragment;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.sqlite.Contract;

public class WalletListFragment extends ListFragment
implements LoaderManager.LoaderCallbacks<Cursor>    {

    static public WalletListFragment newInstance(UcoinCurrency currency) {
        Bundle newInstanceargs = new Bundle();
        newInstanceargs.putParcelable(UcoinCurrency.class.getSimpleName(), currency);
        WalletListFragment fragment = new WalletListFragment();
        fragment.setArguments(newInstanceargs);
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
        return inflater.inflate(R.layout.fragment_wallet_list,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WalletCursorAdapter walletCursorAdapter
                = new WalletCursorAdapter(getActivity(), null, 0);
        setListAdapter(walletCursorAdapter);
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_wallet_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                Bundle args = getArguments();
                UcoinCurrency currency = args.getParcelable(UcoinCurrency.class.getSimpleName());
                AddWalletDialogFragment fragment = AddWalletDialogFragment.newInstance(currency);
                fragment.show(getFragmentManager(),
                        fragment.getClass().getSimpleName());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Bundle args = getArguments();
        UcoinCurrency currency = args.getParcelable(UcoinCurrency.class.getSimpleName());
        UcoinWallet wallet = currency.wallets().getById(id);

        Fragment fragment = WalletFragment.newInstance(wallet);
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
        UcoinCurrency currency = args.getParcelable(UcoinCurrency.class.getSimpleName());

        String selection =
                Contract.Wallet.CURRENCY_ID + "=? AND " +
                Contract.Wallet._ID + " !=?";

        String selectionArgs[] = new String[]{
                currency.id().toString(),
                currency.identityId().toString()
        };

        return new CursorLoader(
                getActivity(),
                Provider.WALLET_URI,
                null, selection, selectionArgs,
                Contract.Wallet._ID +" ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((WalletCursorAdapter)this.getListAdapter()).swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((WalletCursorAdapter)this.getListAdapter()).swapCursor(null);
    }
}
