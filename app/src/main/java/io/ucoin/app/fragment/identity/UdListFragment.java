package io.ucoin.app.fragment.identity;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import io.ucoin.app.R;
import io.ucoin.app.adapter.UdSectionCursorAdapter;
import io.ucoin.app.content.DbProvider;
import io.ucoin.app.sqlite.SQLiteTable;
import io.ucoin.app.sqlite.SQLiteView;


public class UdListFragment extends ListFragment
implements LoaderManager.LoaderCallbacks<Cursor>    {

    static public UdListFragment newInstance(Long walletId) {
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putLong(BaseColumns._ID, walletId);
        UdListFragment fragment = new UdListFragment();
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
        return inflater.inflate(R.layout.fragment_ud_list,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UdSectionCursorAdapter udSectionCursorAdapter
                = new UdSectionCursorAdapter(getActivity(), null, 0);
        setListAdapter(udSectionCursorAdapter);
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Long walletId = args.getLong(BaseColumns._ID);

        String selection = SQLiteView.Ud.WALLET_ID + "=? ";
        String selectionArgs[] = new String[]{
                walletId.toString()
        };

        return new CursorLoader(
                getActivity(),
                DbProvider.UD_URI,
                null, selection, selectionArgs,
                SQLiteTable.Ud.BLOCK +" DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((UdSectionCursorAdapter)this.getListAdapter()).swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((UdSectionCursorAdapter)this.getListAdapter()).swapCursor(null);
    }
}
