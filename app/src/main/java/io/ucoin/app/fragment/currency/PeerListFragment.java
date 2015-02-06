package io.ucoin.app.fragment.currency;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.ucoin.app.ListFragment;
import io.ucoin.app.R;
import io.ucoin.app.adapter.PeerCursorAdapter;
import io.ucoin.app.content.Provider;
import io.ucoin.app.fragment.AddPeerDialogFragment;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinPeer;
import io.ucoin.app.model.http_api.Block;
import io.ucoin.app.model.http_api.Peer;
import io.ucoin.app.sqlite.Contract;
import io.ucoin.app.technical.AsyncTaskHandleException;

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

    //todo
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Bundle args = getArguments();

                AddPeerDialogFragment.OnClickListener listener = new AddPeerDialogFragment.OnClickListener() {
                    @Override
                    public void onPositiveClick(Bundle args) {
                        LoadCurrencyTask task = new LoadCurrencyTask();
                        task.execute(args);
                    }
                };

                UcoinCurrency currency =
                        (UcoinCurrency) args.get(UcoinCurrency.class.getSimpleName());
                AddPeerDialogFragment fragment =
                        AddPeerDialogFragment.newInstance(listener, currency);
                fragment.show(getFragmentManager(),
                        fragment.getClass().getSimpleName());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //todo
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Bundle args = getArguments();
        UcoinCurrency currency = args.getParcelable(UcoinCurrency.class.getSimpleName());
        UcoinPeer peer = currency.peers().getById(id);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        UcoinCurrency currency = args.getParcelable(UcoinCurrency.class.getSimpleName());

        String selection = Contract.Peer.CURRENCY_ID + "=?";
        String selectionArgs[] = new String[]{currency.id().toString()};
        return new CursorLoader(
                getActivity(),
                Provider.PEER_URI,
                null, selection, selectionArgs,
                Contract.Wallet._ID + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((PeerCursorAdapter) this.getListAdapter()).swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((PeerCursorAdapter) this.getListAdapter()).swapCursor(null);
    }

    public class LoadCurrencyTask extends AsyncTaskHandleException<Bundle, Void, UcoinPeer> {

        private UcoinCurrency mCurrency;
        @Override
        protected UcoinPeer doInBackgroundHandleException(Bundle... args) throws Exception {

            String host = args[0].getString(("address"));
            int port = args[0].getInt(("port"));
            UcoinCurrency currency = args[0].getParcelable(UcoinCurrency.class.getSimpleName());

            //Load Peer
            URL url = new URL("http", host, port, "/network/peering/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            InputStream stream = conn.getInputStream();
            Peer apiPeer = Peer.fromJson(stream);

            //Load first block
            url = new URL("http", host, port, "/blockchain/block/0");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            stream = conn.getInputStream();
            Block firstBlock = Block.fromJson(stream);

            Log.d("BLOCK", firstBlock.toString());
            //compare firstblock signature to check if we are on the same currency
            if(firstBlock.signature.compareTo(currency.firstBlockSignature()) != 0) {
                throw new Exception(getString(R.string.peer_not_match_currency));
            }

            mCurrency = currency;
            return currency.peers().newPeer(apiPeer);
        }

        @Override
        protected void onSuccess(UcoinPeer peer) {
            mCurrency.peers().add(peer);
        }

        @Override
        protected void onFailed(Throwable t) {
            t.printStackTrace();
            Log.d("PeerListFragment", t.getClass().getSimpleName());
            Toast.makeText(getActivity().getApplicationContext(),
                    t.toString(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}
