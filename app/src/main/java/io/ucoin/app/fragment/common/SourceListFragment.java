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
import io.ucoin.app.adapter.SourceCursorAdapter;
import io.ucoin.app.content.Provider;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.enums.SourceType;
import io.ucoin.app.sqlite.SQLiteTable;


public class SourceListFragment extends ListFragment
implements LoaderManager.LoaderCallbacks<Cursor>    {

    static public SourceListFragment newInstance(UcoinWallet wallet, SourceType type) {
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putParcelable(UcoinWallet.class.getSimpleName(), wallet);
        newInstanceArgs.putSerializable(SourceType.class.getSimpleName(), type);
        SourceListFragment fragment = new SourceListFragment();
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
        return inflater.inflate(R.layout.fragment_source_list,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SourceCursorAdapter sourceCursorAdapter
                = new SourceCursorAdapter(getActivity(), null, 0);
        setListAdapter(sourceCursorAdapter);
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        UcoinWallet wallet = args.getParcelable(UcoinWallet.class.getSimpleName());
        SourceType type = (SourceType) args.getSerializable(SourceType.class.getSimpleName());

        String selection = SQLiteTable.Source.WALLET_ID + "=? AND " + SQLiteTable.Source.TYPE + "=?";
        String selectionArgs[] = new String[]{
                wallet.id().toString(),
                type.name()
        };

        return new CursorLoader(
                getActivity(),
                Provider.SOURCE_URI,
                null, selection, selectionArgs,
                SQLiteTable.Source._ID +" DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((SourceCursorAdapter)this.getListAdapter()).swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((SourceCursorAdapter)this.getListAdapter()).swapCursor(null);
    }
}
