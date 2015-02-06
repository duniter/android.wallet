package io.ucoin.app.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import io.ucoin.app.Application;
import io.ucoin.app.R;
import io.ucoin.app.adapter.DrawerCurrencyCursorAdapter;
import io.ucoin.app.config.Configuration;
import io.ucoin.app.content.Provider;
import io.ucoin.app.fragment.AddIdentityDialogFragment;
import io.ucoin.app.fragment.AddPeerDialogFragment;
import io.ucoin.app.fragment.currency.CurrencyFragment;
import io.ucoin.app.fragment.currency.CurrencyParametersFragment;
import io.ucoin.app.fragment.identity.IdentityFragment;
import io.ucoin.app.model.UcoinIdentity;
import io.ucoin.app.model.http_api.BlockchainBlock;
import io.ucoin.app.model.http_api.BlockchainParameter;
import io.ucoin.app.model.http_api.Peer;
import io.ucoin.app.sqlite.Currencies;
import io.ucoin.app.fragment.WotSearchFragment;
import io.ucoin.app.model.oldmodels.Identity;
import io.ucoin.app.model.oldmodels.WotLookupResults;
import io.ucoin.app.service.ServiceLocator;
import io.ucoin.app.service.WotService;
import io.ucoin.app.model.UcoinCurrencies;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinPeer;
import io.ucoin.app.sqlite.Contract;
import io.ucoin.app.technical.AsyncTaskHandleException;
import io.ucoin.app.technical.DateUtils;


public class MainActivity extends ActionBarActivity
        implements ListView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MIN_SEARCH_CHARACTERS = 2;
    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawerLayout;
    private QueryResultListener mQueryResultListener;
    private ListView mDrawerListView;

    private Toolbar mToolbar;

    private UcoinCurrency mCurrency = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //LOAD account
        AccountManager accountManager = AccountManager.get(this);
        android.accounts.Account[] accounts = accountManager
                .getAccountsByType(getString(R.string.ACCOUNT_TYPE));

        if (accounts.length == 0) {
            AccountManager.get(this).addAccountExplicitly(new Account(
                            getString(R.string.app_name),
                            getString(R.string.ACCOUNT_TYPE)),
                    null, null);
        }

        UcoinCurrencies currencies = new Currencies(this);
        ((Application) getApplication()).setCurrencies(currencies);

        // Prepare some utilities
        //Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(this));
        DateUtils.setDefaultMediumDateFormat(getMediumDateFormat());
        DateUtils.setDefaultLongDateFormat(getLongDateFormat());

        setContentView(R.layout.activity_main);

        // Init configuration
        Configuration config = new Configuration();
        Configuration.setInstance(config);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        try {
            setSupportActionBar(mToolbar);
        } catch (Throwable t) {
            Log.w("setSupportActionBar", t.getMessage());
        }

        //Navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Set the adapter for the drawer list view
        DrawerCurrencyCursorAdapter drawerCurrencyCursorAdapter
                = new DrawerCurrencyCursorAdapter(this, null, 0);
        mDrawerListView = (ListView) findViewById(R.id.drawer_listview);
        mDrawerListView.setAdapter(drawerCurrencyCursorAdapter);
        mDrawerListView.setOnItemClickListener(this);
        getLoaderManager().initLoader(0, null, this);

        //Navigation drawer toggle
        //Please use ActionBarDrawerToggle(Activity, DrawerLayout, int, int)
        // if you are setting the Toolbar as the ActionBar of your activity.
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout
                , R.string.open_drawer, R.string.close_drawer);

        TextView addCurrency = (TextView) findViewById(R.id.drawer_add_currency);

        final AddPeerDialogFragment.OnClickListener listener = new AddPeerDialogFragment.OnClickListener() {
            @Override
            public void onPositiveClick(Bundle args) {
                LoadCurrencyTask task = new LoadCurrencyTask();
                task.execute(args);
            }
        };

        addCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPeerDialogFragment fragment = AddPeerDialogFragment.newInstance(listener);
                fragment.show(getFragmentManager(),
                        fragment.getClass().getSimpleName());
            }
        });

        TextView settings = (TextView) findViewById(R.id.drawer_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        SettingsActivity.class);
                startActivity(intent);
            }
        });

        //ContentResolver.setSyncAutomatically(account, getString(R.string.AUTHORITY), true);
/*
        Fragment fragment;
        fragment = WalletListFragment.newInstance();
        fragment.setHasOptionsMenu(true);

        getFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.animator.fade_in,
                        R.animator.fade_out)
                .add(R.id.frame_content, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
                */
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        //todo handle screen orientation change
        //for now it is just discarded by adding
        //android:configChanges="orientation|screenSize" in the manifest
        super.onConfigurationChanged(newConfig);
        mToggle.onConfigurationChanged(newConfig);
    }

    //Called once during the whole activity lifecycle
    // after the first onResume() call
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }

        int bsEntryCount = getFragmentManager().getBackStackEntryCount();
        if (bsEntryCount <= 1) {
            super.onBackPressed();
            return;
        }

        String currentFragment = getFragmentManager()
                .getBackStackEntryAt(bsEntryCount - 1)
                .getName();

        Fragment fragment = getFragmentManager().findFragmentByTag(currentFragment);

        //fragment that need to handle onBackPressed
        //shoud implements MainActivity.OnBackPressedInterface
        if (fragment instanceof OnBackPressed) {
            if (((OnBackPressed) fragment).onBackPressed()) {
                return;
            }
        }

        getFragmentManager().popBackStack();
    }

    public boolean onQueryTextSubmit(MenuItem searchItem, UcoinCurrency currency, String query) {

        searchItem.getActionView().clearFocus();
        WotSearchFragment fragment = WotSearchFragment.newInstance(currency, query);
        fragment.setHasOptionsMenu(true);
        mQueryResultListener = fragment;
        getFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.animator.delayed_fade_in,
                        R.animator.fade_out,
                        R.animator.delayed_fade_in,
                        R.animator.fade_out)
                .replace(R.id.frame_content, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();

        if (query.length() > MIN_SEARCH_CHARACTERS) {
            SearchTask searchTask = new SearchTask(currency, query);
            searchTask.execute((Void) null);
        }

        return true;
    }

    // nav drawer currency items click
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCurrency = ((Application) getApplication()).getCurrencies().getById(id);
        Log.d("CURRENCY", mCurrency.toString());
        Fragment fragment = CurrencyFragment.newInstance(mCurrency);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragment.setHasOptionsMenu(true);
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.animator.delayed_fade_in,
                        R.animator.fade_out,
                        R.animator.delayed_fade_in,
                        R.animator.fade_out)
                .replace(R.id.frame_content, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();

        // close the drawer
        mDrawerLayout.closeDrawer(findViewById(R.id.drawer_panel));

        setDrawerIdentity(mCurrency.identity());
    }

    public void setToolbarColor(int colorRes) {
        mToolbar.setBackgroundColor(colorRes);
    }

    public void setDrawerIdentity(final UcoinIdentity identity) {
        LinearLayout drawerHeader = (LinearLayout) findViewById(R.id.drawer_header);
        drawerHeader.setVisibility(View.VISIBLE);

        ImageButton drawerButton = (ImageButton) findViewById(R.id.drawer_button);
        TextView drawerUid = (TextView) findViewById(R.id.drawer_uid);
        TextView drawerPublicKey = (TextView) findViewById(R.id.drawer_public_key);

        if (identity == null) {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddIdentityDialogFragment fragment =
                            AddIdentityDialogFragment.newInstance(mCurrency);
                    fragment.show(getFragmentManager(),
                            fragment.getClass().getSimpleName());
                }
            };

            drawerHeader.setOnClickListener(listener);
            drawerButton.setImageResource(R.drawable.ic_person_add_white_36dp);
            drawerUid.setText("");
            drawerPublicKey.setText("");
        } else {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = IdentityFragment.newInstance(identity);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(
                                    R.animator.delayed_slide_in_up,
                                    R.animator.fade_out,
                                    R.animator.delayed_fade_in,
                                    R.animator.slide_out_up)
                            .replace(R.id.frame_content, fragment, fragment.getClass().getSimpleName())
                            .addToBackStack(fragment.getClass().getSimpleName())
                            .commit();
                    // close the drawer
                    mDrawerLayout.closeDrawer(findViewById(R.id.drawer_panel));
                }
            };


            drawerHeader.setOnClickListener(listener);
            drawerButton.setImageResource(R.drawable.ic_person_white_36dp);
            drawerUid.setText(identity.uid());
            drawerPublicKey.setText(identity.wallet().publicKey());
        }
    }

    /* -- Internal methods -- */
    protected DateFormat getMediumDateFormat() {
        final String format = Settings.System.getString(getContentResolver(), Settings.System.DATE_FORMAT);
        if (TextUtils.isEmpty(format)) {
            return android.text.format.DateFormat.getMediumDateFormat(getApplicationContext());
        } else {
            return new SimpleDateFormat(format);
        }
    }

    protected DateFormat getLongDateFormat() {
        return android.text.format.DateFormat.getLongDateFormat(getApplicationContext());
    }


    /**
     * Display an an arrow in the toolbar to get to the previous fragment
     * or an hamburger icon to open the navigation drawer
     */
    public void setBackButtonEnabled(boolean enabled) {
        if (enabled) {
            mToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
        } else {
            mToggle.setDrawerIndicatorEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                Provider.CURRENCY_URI,
                null, null, null,
                Contract.Currency.CURRENCY_NAME + " ASC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((DrawerCurrencyCursorAdapter) mDrawerListView.getAdapter()).swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((DrawerCurrencyCursorAdapter) mDrawerListView.getAdapter()).swapCursor(null);
    }


    /**
     * Interface for handling OnBackPressed event in fragments
     */
    public interface OnBackPressed {
        /**
         * @return true if the events has been handled, false otherwise
         */
        public boolean onBackPressed();
    }

    public interface QueryResultListener {
        public void onQuerySuccess(List<Identity> identities);

        public void onQueryFailed();

        public void onQueryCancelled();
    }

    public class SearchTask extends AsyncTaskHandleException<Void, Void, List<Identity>> {

        private final String mSearchQuery;
        //todo check on the currency only
        private final UcoinCurrency mCurrency;

        SearchTask(UcoinCurrency currency, String mSearchQuery) {
            this.mCurrency = currency;
            this.mSearchQuery = mSearchQuery;
        }

        @Override
        protected List<Identity> doInBackgroundHandleException(Void... params) {
            WotService service = ServiceLocator.instance().getWotService();
            WotLookupResults results = service.find(mSearchQuery);

            if (results == null) {
                return null;
            }

            return service.toIdentities(results);
        }

        @Override
        protected void onSuccess(List<Identity> identities) {
            mQueryResultListener.onQuerySuccess(identities);
        }

        @Override
        protected void onFailed(Throwable t) {
            mQueryResultListener.onQueryFailed();
        }

        @Override
        protected void onCancelled() {
            mQueryResultListener.onQueryCancelled();
        }
    }

    public class LoadCurrencyTask extends AsyncTaskHandleException<Bundle, Void, UcoinCurrency> {

        @Override
        protected UcoinCurrency doInBackgroundHandleException(Bundle... args) throws Exception {

            String host = args[0].getString(("address"));
            int port = args[0].getInt(("port"));

            //Load Peer
            URL url = new URL("http", host, port, "/network/peering/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            InputStream stream = conn.getInputStream();
            Peer peer = Peer.fromJson(stream);

            // Load currency
            url = new URL("http", host, port, "/blockchain/parameters");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            stream = conn.getInputStream();
            BlockchainParameter parameter = BlockchainParameter.fromJson(stream);

            //Load first block
            url = new URL("http", host, port, "/blockchain/block/0");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            stream = conn.getInputStream();
            BlockchainBlock firstBlock = BlockchainBlock.fromJson(stream);

            //Load last block
            url = new URL("http", host, port, "/blockchain/current");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            stream = conn.getInputStream();
            BlockchainBlock lastBlock = BlockchainBlock.fromJson(stream);


            UcoinCurrency currency = ((Application) getApplication()).getCurrencies().newCurrency(
                    parameter, firstBlock, lastBlock);
            UcoinPeer node = currency.peers().newPeer(peer);
            currency.peers().add(node);

            return currency;
        }

        @Override
        protected void onSuccess(UcoinCurrency currency) {
            mDrawerLayout.closeDrawer(Gravity.START);
            Fragment fragment = CurrencyParametersFragment.newInstance(currency);
            fragment.setHasOptionsMenu(true);
            setTitle(R.string.add_currency);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.animator.delayed_slide_in_up,
                            R.animator.fade_out,
                            R.animator.delayed_fade_in,
                            R.animator.slide_out_up)
                    .replace(R.id.frame_content, fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();
        }

        @Override
        protected void onFailed(Throwable t) {
            t.printStackTrace();
            Log.d("COMMUNITYLISTFRAGMENT", t.getClass().getSimpleName());
            Toast.makeText(getApplicationContext(),
                    t.toString(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}