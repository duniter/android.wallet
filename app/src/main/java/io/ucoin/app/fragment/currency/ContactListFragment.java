package io.ucoin.app.fragment.currency;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.Arrays;

import io.ucoin.app.Application;
import io.ucoin.app.R;
import io.ucoin.app.UcoinUris;
import io.ucoin.app.activity.AddContactActivity;
import io.ucoin.app.activity.CurrencyActivity;
import io.ucoin.app.adapter.ContactSectionCursorAdapter;
import io.ucoin.app.sqlite.SQLiteTable;

public class ContactListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
SearchView.OnQueryTextListener{

    private final static String CURRENCY_ID = "currency_id";

    static public ContactListFragment newInstance(Long currencyId) {
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putLong(CURRENCY_ID, currencyId);
        ContactListFragment fragment = new ContactListFragment();
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

        return inflater.inflate(R.layout.fragment_contact_list,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.contacts));

        final Long currencyId = getArguments().getLong(CURRENCY_ID);

       ContactSectionCursorAdapter contactSectionCursorAdapter
                = new ContactSectionCursorAdapter(getActivity(), null, 0);
        setListAdapter(contactSectionCursorAdapter);
        getLoaderManager().initLoader(0, getArguments(), this);

        ImageButton addContactButton = (ImageButton) view.findViewById(R.id.add_contact_button);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionAddContact(currencyId);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_contact_list, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        SearchManager searchManager = (SearchManager) getActivity()
                .getSystemService(Activity.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.action_lookup);

        SearchView searchView = (SearchView)searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        //hide the keyboard and remove focus
        searchView.clearFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_lookup:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Long currencyId = args.getLong(CURRENCY_ID);
        String selection = SQLiteTable.Contact.CURRENCY_ID + "=?";
        String[] selectionArgs = new String[]{currencyId.toString()};

        String query = args.getString("query");
        if(query != null) {
            selection += " AND " + SQLiteTable.Contact.NAME + " LIKE ?";
            selectionArgs = Arrays.copyOf(selectionArgs, selectionArgs.length + 1);
            selectionArgs[1] = query + "%";
        }

        return new CursorLoader(
                getActivity(),
                UcoinUris.CONTACT_URI,
                null, selection, selectionArgs,
                SQLiteTable.Contact.NAME + " COLLATE NOCASE ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((ContactSectionCursorAdapter) this.getListAdapter()).swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((ContactSectionCursorAdapter) this.getListAdapter()).swapCursor(null);
    }

    private void actionAddContact(Long currencyId) {
        Intent intent = new Intent(getActivity(), AddContactActivity.class);
        intent.putExtra(Application.EXTRA_CURRENCY_ID, currencyId);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Bundle args = getArguments();
        args.putLong(CURRENCY_ID, getArguments().getLong(CURRENCY_ID));
        args.putString("query", s);
        getLoaderManager().restartLoader(0, args, this);
        return true;
    }
}
