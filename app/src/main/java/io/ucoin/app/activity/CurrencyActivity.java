package io.ucoin.app.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import io.ucoin.app.Application;
import io.ucoin.app.BuildConfig;
import io.ucoin.app.R;
import io.ucoin.app.UcoinUris;
import io.ucoin.app.enumeration.DayOfWeek;
import io.ucoin.app.enumeration.Month;
import io.ucoin.app.fragment.currency.BlockListFragment;
import io.ucoin.app.fragment.currency.ContactListFragment;
import io.ucoin.app.fragment.currency.IdentityFragment;
import io.ucoin.app.fragment.currency.PeerListFragment;
import io.ucoin.app.fragment.currency.WalletListFragment;
import io.ucoin.app.fragment.wallet.WalletFragment;
import io.ucoin.app.sqlite.SQLiteView;


public class CurrencyActivity extends ActionBarActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        TextView.OnClickListener {

    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawerLayout;
    private TextView mDrawerActivatedView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_currency);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        try {
            setSupportActionBar(toolbar);
        } catch (Throwable t) {
            Log.w("setSupportActionBar", t.getMessage());
        }

        ImageButton button = (ImageButton) findViewById(R.id.drawer_switch_currency_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrencyActivity.this, CurrencyListActivity.class);
                startActivityForResult(intent, Application.ACTIVITY_CURRENCY_LIST);
            }
        });

        //Navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        TextView drawerWalletsView = (TextView) mDrawerLayout.findViewById(R.id.drawer_wallets);
        TextView drawerContactsView = (TextView) mDrawerLayout.findViewById(R.id.drawer_contacts);
        TextView drawerIdentityView = (TextView) mDrawerLayout.findViewById(R.id.drawer_identity);
        TextView drawerPeersView = (TextView) mDrawerLayout.findViewById(R.id.drawer_peers);
        TextView drawerBlocksView = (TextView) mDrawerLayout.findViewById(R.id.drawer_blocks);
        drawerWalletsView.setActivated(true);
        mDrawerActivatedView = drawerWalletsView;

        drawerWalletsView.setOnClickListener(this);
        drawerContactsView.setOnClickListener(this);
        drawerIdentityView.setOnClickListener(this);
        drawerPeersView.setOnClickListener(this);
        drawerBlocksView.setOnClickListener(this);

        if (BuildConfig.DEBUG) {
            drawerBlocksView.setVisibility(View.VISIBLE);
        }

        // Set the adapter for the drawer list view
/*
        String[]drawerItems = getResources().getStringArray(R.array.drawer_items);
        ListView drawerListView = (ListView) findViewById(R.id.drawer_listview);
        drawerListView.setAdapter(new ArrayAdapter<>(this,
                R.layout.list_item_drawer, drawerItems));
*/
        //Navigation drawer toggle
        //Please use ActionBarDrawerToggle(Activity, DrawerLayout, int, int)
        // if you are setting the Toolbar as the ActionBar of your activity.
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.open_drawer, R.string.close_drawer);

        TextView settings = (TextView) findViewById(R.id.drawer_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrencyActivity.this,
                        SettingsActivity.class);
                startActivity(intent);
            }
        });

        if (BuildConfig.DEBUG) {
            TextView exportDb = (TextView) findViewById(R.id.export_db);
            exportDb.setVisibility(View.VISIBLE);
            exportDb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exportDB();
                }
            });


            TextView requestSync = (TextView) findViewById(R.id.request_sync);
            requestSync.setVisibility(View.VISIBLE);
            requestSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Application.requestSync();
                }
            });
        }

        Long currencyId = getIntent().getExtras().getLong(Application.EXTRA_CURRENCY_ID);
        Fragment fragment = WalletListFragment.newInstance(currencyId);
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
        getLoaderManager().initLoader(0, getIntent().getExtras(), this);


        if (getIntent().getExtras().containsKey(Application.EXTRA_WALLET_ID)) {
            fragment = WalletFragment.newInstance(getIntent().getExtras().getLong(Application.EXTRA_WALLET_ID));
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
        }


    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
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
            closeDrawer();
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK && requestCode == Application.ACTIVITY_CURRENCY_LIST) {
            Long currencyId = intent.getExtras().getLong(Application.EXTRA_CURRENCY_ID);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("currency_id", currencyId);
            editor.apply();

            intent = new Intent(this, CurrencyActivity.class);
            intent.putExtra(Application.EXTRA_CURRENCY_ID, currencyId);
            startActivity(intent);
            finish();
        }
    }

    public void setDrawerIndicatorEnabled(final boolean enabled) {
        if (mToggle.isDrawerIndicatorEnabled() == enabled) {
            return;
        }

        float start = enabled ? 1f : 0f;
        float end = Math.abs(start - 1);
        ValueAnimator offsetAnimator = ValueAnimator.ofFloat(start, end);
        offsetAnimator.setDuration(300);
        //offsetAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        offsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offset = (Float) animation.getAnimatedValue();
                mToggle.onDrawerSlide(null, offset);
            }
        });

        offsetAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (enabled) {
                    mToggle.setDrawerIndicatorEnabled(enabled);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!enabled) {
                    mToggle.setDrawerIndicatorEnabled(enabled);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        offsetAnimator.start();
    }

    public void clearAllFragments() {
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        setTitle(R.string.ucoin);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(findViewById(R.id.drawer_panel));
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(findViewById(R.id.drawer_panel));
    }


    private void exportDB() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/io.ucoin.android.wallet/databases/ucoin.db";
        String backupDBPath = "ucoin.db";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Long currencyId = args.getLong(Application.EXTRA_CURRENCY_ID);
        String selection;
        String selectionArgs[];

        selection = BaseColumns._ID + "=?";
        selectionArgs = new String[]{currencyId.toString()};

        return new CursorLoader(
                this,
                UcoinUris.CURRENCY_URI,
                null, selection, selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToNext();
        TextView drawerCurrencyName = (TextView) findViewById(R.id.drawer_currency_name);
        drawerCurrencyName.setText(data.getString(data.getColumnIndex(SQLiteView.Currency.NAME)));

        TextView drawerMembersCount = (TextView) findViewById(R.id.drawer_members_count);
        drawerMembersCount.setText(data.getString(data.getColumnIndex(SQLiteView.Currency.MEMBERS_COUNT)) + " " + getResources().getString(R.string.members));

        TextView DrawerBlockNumber = (TextView) findViewById(R.id.drawer_block_number);
        DrawerBlockNumber.setText(getResources().getString(R.string.block) + " " + data.getString(data.getColumnIndex(SQLiteView.Currency.CURRENT_BLOCK)));

        String d = data.getString(data.getColumnIndex(SQLiteView.Currency.BLOCK_DAY_OF_WEEK));
        if (d == null) d = Integer.toString(DayOfWeek.UNKNOWN.ordinal());

        String m = data.getString(data.getColumnIndex(SQLiteView.Currency.BLOCK_MONTH));
        if (m == null) m = Integer.toString(Month.UNKNOWN.ordinal());
        Month month = Month.fromInt(Integer.parseInt(m));

        String dayOfWeek = DayOfWeek.fromInt(Integer.parseInt(d)).toString(this);

        String dateStr = dayOfWeek + " ";
        dateStr += data.getString(data.getColumnIndex(SQLiteView.Currency.BLOCK_DAY)) + " ";
        dateStr += month.toString(this) + " ";
        dateStr += data.getString(data.getColumnIndex(SQLiteView.Currency.BLOCK_YEAR)) + " ";
        dateStr += data.getString(data.getColumnIndex(SQLiteView.Currency.BLOCK_HOUR)) + " ";

        TextView drawerDate = (TextView) findViewById(R.id.drawer_date);
        drawerDate.setText(dateStr);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onClick(View v) {
        mDrawerActivatedView.setActivated(false);
        mDrawerActivatedView = (TextView) v;
        v.setActivated(true);
        closeDrawer();

        Fragment fragment = null;
        Long currencyId = getIntent().getExtras().getLong(Application.EXTRA_CURRENCY_ID);
        switch (v.getId()) {
            case R.id.drawer_wallets:
                fragment = WalletListFragment.newInstance(currencyId);
                fragment.setHasOptionsMenu(true);
                break;
            case R.id.drawer_contacts:
                fragment = ContactListFragment.newInstance(currencyId);
                fragment.setHasOptionsMenu(true);
                break;
            case R.id.drawer_identity:
                fragment = IdentityFragment.newInstance(currencyId);
                fragment.setHasOptionsMenu(false);
                break;
            case R.id.drawer_peers:
                fragment = PeerListFragment.newInstance(currencyId);
                fragment.setHasOptionsMenu(true);
                break;
            case R.id.drawer_blocks:
                fragment = BlockListFragment.newInstance(currencyId);
                fragment.setHasOptionsMenu(true);
                break;
        }

        if (fragment != null) {
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();

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
            closeDrawer();
        } else {
            throw new RuntimeException("Variable fragment has not been initialized");
        }
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
}