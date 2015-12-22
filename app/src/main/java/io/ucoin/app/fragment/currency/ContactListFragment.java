package io.ucoin.app.fragment.currency;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import io.ucoin.app.Application;
import io.ucoin.app.R;
import io.ucoin.app.UcoinUris;
import io.ucoin.app.activity.AddContactActivity;
import io.ucoin.app.activity.CurrencyActivity;
import io.ucoin.app.activity.LookupActivity;
import io.ucoin.app.adapter.ContactSectionBaseAdapter;
import io.ucoin.app.fragment.identity.MemberListFragment;
import io.ucoin.app.model.UcoinCurrencies;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinEndpoint;
import io.ucoin.app.model.http_api.WotLookup;
import io.ucoin.app.model.sql.sqlite.Currencies;
import io.ucoin.app.model.sql.sqlite.Currency;
import io.ucoin.app.sqlite.SQLiteTable;

public class ContactListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener{

    private final static String CURRENCY_ID = "currency_id";

    String textQuery ="";
    boolean autorisationFindNetwork = false;
    ProgressBar progress;
    Cursor mCursor;
    RequestQueue queue;
    ArrayList<Entity> listEntity;
    protected int firstIndexIdentity;
    LoadIdentityTask loadIdentityTask;
    LinearLayout searchAdvenced;
    SearchView searchView;

    boolean findByPubKey = false;

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
        setHasOptionsMenu(true);

        listEntity = new ArrayList<>();

        queue = Volley.newRequestQueue(getActivity());

        final Long currencyId = getArguments().getLong(CURRENCY_ID);

        progress = (ProgressBar) view.findViewById(R.id.progress_bar);

        ContactSectionBaseAdapter contactSectionBaseAdapter
                = new ContactSectionBaseAdapter(getActivity(), null,this);
        setListAdapter(contactSectionBaseAdapter);
        getLoaderManager().initLoader(0, getArguments(), this);

        ImageButton addContactButton = (ImageButton) view.findViewById(R.id.add_contact_button);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionAddContact(currencyId);
            }
        });

        searchAdvenced = (LinearLayout) view.findViewById(R.id.search_advenced);
        Switch switch1 = (Switch) view.findViewById(R.id.switch1);
        switch1.setChecked(findByPubKey);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findByPubKey = isChecked;
                onQueryTextChange(textQuery);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        queue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        queue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
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

        searchView = (SearchView)searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQuery(textQuery, true);
        searchView.clearFocus();
        if(!textQuery.equals("")){
            searchView.setIconified(false);
            searchView.requestFocus();
        }
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
        int realPosition = ((ContactSectionBaseAdapter) this.getListAdapter()).getRealPosition(position);
        String pubkey = listEntity.get(realPosition).getPublicKey();
        String name = listEntity.get(realPosition).getName();
        Long currencyId= listEntity.get(realPosition).currencyId;
        WotLookup.Result result= MemberListFragment.findIdentity(pubkey, name);
        Intent intent = new Intent(getActivity(), LookupActivity.class);
        intent.putExtra(Application.EXTRA_CURRENCY_ID, currencyId);
        intent.putExtra(WotLookup.Result.class.getSimpleName(), result);
        if(getActivity() instanceof CurrencyActivity){
            ((CurrencyActivity)getActivity()).onActivityRes(Application.ACTIVITY_LOOKUP, Activity.RESULT_OK, intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Long currencyId = args.getLong(CURRENCY_ID);
        String selection = null;
        String[] selectionArgs = null;
        if(!currencyId.equals(Long.valueOf(-1))){
            selection = SQLiteTable.Contact.CURRENCY_ID + "=?";
            selectionArgs = new String[]{currencyId.toString()};
        }

        String query = args.getString("query");
        if(query != null && !query.equals("")) {
            this.textQuery = query;
            if(query.length()>=3){
                autorisationFindNetwork = true;
            }
            if(!findByPubKey){
                if (selection == null) {
                    selection = SQLiteTable.Contact.NAME + " LIKE ?";
                } else {
                    selection += " AND " + SQLiteTable.Contact.NAME + " LIKE ?";
                }
                if (selectionArgs == null) {
                    selectionArgs = new String[]{query + "%"};
                } else {
                    selectionArgs = Arrays.copyOf(selectionArgs, selectionArgs.length + 1);
                    selectionArgs[selectionArgs.length - 1] = query + "%";
                }
            }else {
                if (selection == null) {
                    selection = SQLiteTable.Contact.PUBLIC_KEY + " LIKE ?";
                } else {
                    selection += " AND " + SQLiteTable.Contact.PUBLIC_KEY + " LIKE ?";
                }
                if (selectionArgs == null) {
                    selectionArgs = new String[]{"%" + query + "%"};
                } else {
                    selectionArgs = Arrays.copyOf(selectionArgs, selectionArgs.length + 1);
                    selectionArgs[selectionArgs.length - 1] = "%" + query + "%";
                }
            }
        }else{
            this.textQuery = "";
            autorisationFindNetwork = false;
        }

        return new CursorLoader(
                getActivity(),
                UcoinUris.CONTACT_URI,
                null, selection, selectionArgs,
                SQLiteTable.Contact.NAME + " COLLATE NOCASE ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        listEntity =new ArrayList<>();
        Entity entity;
        if(data.moveToFirst()){
            do{
                Long currencyId = data.getLong(data.getColumnIndex(SQLiteTable.Contact.CURRENCY_ID));
                entity =new Entity(
                        true,
                        data.getString(data.getColumnIndex(SQLiteTable.Contact.NAME)),
                        data.getString(data.getColumnIndex(SQLiteTable.Contact.PUBLIC_KEY)),
                        (new Currency(getActivity(),currencyId)).name(),
                        currencyId);
                listEntity.add(entity);
            }while (data.moveToNext());
        }
        if(listEntity.size()==0 && textQuery.length()>0){
            autorisationFindNetwork = true;
        }
        seeIdentityNetwork();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        autorisationFindNetwork = false;
        onQueryTextChange("");
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
        queue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        if(loadIdentityTask!=null) {
            loadIdentityTask.cancel(true);
        }
        Bundle args = getArguments();
        args.putLong(CURRENCY_ID, getArguments().getLong(CURRENCY_ID));
        args.putString("query", s);
        getLoaderManager().restartLoader(0, args, this);
        if(s.equals("")){
            searchAdvenced.setVisibility(View.GONE);
        }else{
            searchAdvenced.setVisibility(View.VISIBLE);
        }
        return true;
    }

    public void findInNetwork(){
        searchView.clearFocus();
        autorisationFindNetwork =true;
        seeIdentityNetwork();
    }

    public void seeIdentityNetwork(){
        if(autorisationFindNetwork) {
            ((ContactSectionBaseAdapter) getListAdapter()).swapList(new ArrayList<Entity>(), false, "", 0);
        }
        progress.setVisibility(View.VISIBLE);
        if(autorisationFindNetwork && !textQuery.equals("")){
            loadIdentityTask = new LoadIdentityTask(
                    getActivity(),
                    getArguments().getLong(CURRENCY_ID));
            loadIdentityTask.execute();
        }else{
            autorisationFindNetwork = false;
            onLoadIdentityFinish();
        }
    }

    public void onLoadIdentityFinish(){
        sortContactAndIdentity();
        ((ContactSectionBaseAdapter) this.getListAdapter()).swapList(listEntity, autorisationFindNetwork, textQuery, firstIndexIdentity);
        progress.setVisibility(View.GONE);
    }

    public void sortContactAndIdentity(){
        Comparator<Entity> comparator_tab = new Comparator<Entity>() {

            @Override
            public int compare(Entity o1, Entity o2) {
                String i = o1.name;
                String j = o2.name;
                return i.compareTo(j);
            }

        };
        if(listEntity.size()>0) {
            Collections.sort(listEntity.subList(firstIndexIdentity, listEntity.size()), comparator_tab);
        }
    }

    public class LoadIdentityTask extends AsyncTask<String, Void, String> {

        protected Context mContext;
        protected WotLookup.Result[] results;
        protected Long currencyId;

        public LoadIdentityTask(Context context, Long currencyId){
            this.mContext = context;
            this.currencyId = currencyId;
            firstIndexIdentity = listEntity.size();
        }

        @Override
        protected String doInBackground(String... param) {
            results = null;
            retrieveIdentities();
            return null;
        }

        protected void retrieveIdentities(){
            int socketTimeout = 2000;//2 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            if (currencyId.equals(Long.valueOf(-1))) {
                UcoinCurrencies currencies = new Currencies(Application.getContext());
                Cursor cursor = currencies.getAll();
                UcoinEndpoint endpoint;
                if (cursor.moveToFirst()) {
                    do {
                        Long cId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                        UcoinCurrency currency = new Currency(mContext, cId);
                        endpoint = currency.peers().at(0).endpoints().at(0);
                        String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/wot/lookup/" + textQuery;
                        StringRequest request = request(url, cId);
                        request.setTag(this);
                        request.setRetryPolicy(policy);
                        //Application.getRequestQueue().add(request);
                        queue.add(request);
                    } while (cursor.moveToNext());
                }
            } else {
                UcoinCurrency currency = new Currency(mContext, currencyId);
                UcoinEndpoint endpoint = currency.peers().at(0).endpoints().at(0);
                String url = "http://" + endpoint.ipv4() + ":" + endpoint.port() + "/wot/lookup/" + textQuery;
                StringRequest request = request(url, currencyId);
                request.setTag("TAG");
                request.setRetryPolicy(policy);
                //Application.getRequestQueue().add(request);
                queue.add(request);
            }
        }

        public StringRequest request(String url, final Long id){
            final String name=(new Currency(mContext,id)).name();
            StringRequest request = new StringRequest(
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            WotLookup lookup = WotLookup.fromJson(response);
                            lookupToStringTab(lookup, name, id);
                            onLoadIdentityFinish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error instanceof NoConnectionError) {
                                Toast.makeText(Application.getContext(), mContext.getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                            } else if(error instanceof TimeoutError) {
                                Toast.makeText(Application.getContext(), "Error for connection to "+name, Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(Application.getContext(), error.toString(), Toast.LENGTH_LONG).show();
                            }
                            onLoadIdentityFinish();
                        }
                    });
            return request;
        }

        public void lookupToStringTab(WotLookup lookup, String currencyName,Long id) {
            Entity entity;
            for(WotLookup.Result res : lookup.results){
                entity =new Entity(
                        false,
                        res.uids[0].uid,
                        res.pubkey,
                        currencyName,
                        id);
                if(entity.filter(textQuery,findByPubKey) && !listEntity.contains(entity)){
                    listEntity.add(entity);
                }
            }
        }
    }

    public class Entity {
        boolean isContact;
        String name;
        String publicKey;
        String currencyName;
        Long currencyId;

        public Entity(boolean isContact, String name, String publicKey, String currencyName, Long currencyId) {
            this.isContact = isContact;
            this.name = name;
            this.publicKey = publicKey;
            this.currencyName = currencyName;
            this.currencyId = currencyId;
        }

        @Override
        public boolean equals(Object o) {
            return this.name.equals(((Entity)o).name) &&
                    this.publicKey.equals(((Entity)o).publicKey) &&
                            this.currencyName.equals(((Entity)o).currencyName);
        }

        public String getName() {
            return name;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public boolean isContact() {
            return isContact;
        }

        public boolean filter(String query, boolean findByPubKey){
            boolean result;
            if(!findByPubKey){
                result = name.substring(0,query.length()).equals(query);
            }else{
                result = publicKey.contains(query);
            }
            return result;
        }
    }
}
