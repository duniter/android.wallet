package io.ucoin.app.fragment.currency;

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
import io.ucoin.app.adapter.PeerCursorAdapter;
import io.ucoin.app.content.Provider;
import io.ucoin.app.fragment.AddPeerDialogFragment;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinPeer;
import io.ucoin.app.sqlite.SQLiteTable;

public class PeerListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    static public PeerListFragment newInstance(UcoinCurrency currency) {
        Bundle newInstanceargs = new Bundle();
        newInstanceargs.putParcelable(UcoinCurrency.class.getSimpleName(), currency);
        PeerListFragment fragment = new PeerListFragment();
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
        return inflater.inflate(R.layout.fragment_peer_list,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PeerCursorAdapter peerCursorAdapter
                = new PeerCursorAdapter(getActivity(), null, 0);
        setListAdapter(peerCursorAdapter);
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_peer_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Bundle args = getArguments();

                UcoinCurrency currency =
                        (UcoinCurrency) args.get(UcoinCurrency.class.getSimpleName());
                AddPeerDialogFragment fragment =
                        AddPeerDialogFragment.newInstance();
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
        UcoinPeer peer = currency.peers().getById(id);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        UcoinCurrency currency = args.getParcelable(UcoinCurrency.class.getSimpleName());

        String selection = SQLiteTable.Peer.CURRENCY_ID + "=?";
        String selectionArgs[] = new String[]{currency.id().toString()};
        return new CursorLoader(
                getActivity(),
                Provider.PEER_URI,
                null, selection, selectionArgs,
                SQLiteTable.Wallet._ID + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((PeerCursorAdapter) this.getListAdapter()).swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((PeerCursorAdapter) this.getListAdapter()).swapCursor(null);
    }
}
