package io.ucoin.app.fragment.currency;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.ucoin.app.Application;
import io.ucoin.app.R;
import io.ucoin.app.UcoinUris;
import io.ucoin.app.activity.CurrencyActivity;
import io.ucoin.app.activity.LookupActivity;
import io.ucoin.app.fragment.identity.MemberListFragment;
import io.ucoin.app.fragment.identity.MembershipListFragment;
import io.ucoin.app.fragment.identity.SelfCertificationListFragment;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinIdentity;
import io.ucoin.app.model.http_api.WotLookup;
import io.ucoin.app.model.sql.sqlite.Currency;
import io.ucoin.app.sqlite.SQLiteTable;
import io.ucoin.app.sqlite.SQLiteView;
import io.ucoin.app.technical.crypto.AddressFormatException;
import io.ucoin.app.widget.SlidingTabLayout;

public class IdentityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    private Cursor mCursor;
    private LinearLayout mHeaderLayout;
    private TextView mUid;
    private ImageButton mSearchIdentityButton;

    public static IdentityFragment newInstance(Bundle args) {
        IdentityFragment fragment = new IdentityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_identity,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.identity));
        setHasOptionsMenu(false);
        ((CurrencyActivity) getActivity()).setDrawerIndicatorEnabled(true);

        mHeaderLayout = (LinearLayout) getView().findViewById(R.id.header);
        mViewPager = (ViewPager) getView().findViewById(R.id.viewpager);
        mSlidingTabLayout = (SlidingTabLayout) getView().findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSearchIdentityButton = (ImageButton) getView().findViewById(R.id.search_identity_button);
        mSearchIdentityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSearch();
            }
        });

        mUid = (TextView) view.findViewById(R.id.uid);


        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_identity, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteItem = menu.findItem(R.id.action_delete);

        if (mCursor == null || mCursor.isClosed() || mCursor.getCount() == 0) {
            deleteItem.setVisible(false);
        } else {
            deleteItem.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                actionDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        WotLookup.Result result = (WotLookup.Result) args.getSerializable(WotLookup.Result.class.getSimpleName());
        String publicKey = result.pubkey;
        String uid = result.uids[0].uid;
        UcoinCurrency currency = new Currency(getActivity(), getArguments().getLong(BaseColumns._ID));
        UcoinIdentity identity = null;
        try {
            identity = currency.addIdentity(uid, publicKey);
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }
        Long currencyId = args.getLong(BaseColumns._ID);
        String selection = SQLiteTable.Identity.CURRENCY_ID + "=?" +
                " AND " + SQLiteTable.Identity.PUBLIC_KEY + "=?";
        String[] selectionArgs = new String[]{currency.id().toString(),publicKey};
        return new CursorLoader(
                getActivity(),
                UcoinUris.IDENTITY_URI,
                null, selection, selectionArgs,
                BaseColumns._ID + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            mCursor = data;
            mUid.setText(data.getString(data.getColumnIndex(SQLiteView.Identity.UID)));
            mHeaderLayout.setVisibility(View.VISIBLE);
            mSlidingTabLayout.setVisibility(View.VISIBLE);

            // Get the ViewPager and set it's PagerAdapter so that it can display items
            if (mViewPager.getAdapter() == null) {
                mViewPager.setAdapter(new IdentityPagerAdapter(getChildFragmentManager()));

                // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
                // it's PagerAdapter set.
                mSlidingTabLayout.setViewPager(mViewPager);
            }

        } else {
            mViewPager.setAdapter(null);
            mSlidingTabLayout.setViewPager(null);

            mHeaderLayout.setVisibility(View.GONE);
            mSlidingTabLayout.setVisibility(View.GONE);

            mUid.setText("");
        }


        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void actionDelete() {
        UcoinCurrency currency = new Currency(getActivity(), getArguments().getLong(BaseColumns._ID));
        currency.identity().delete();
    }

    public void actionSearch() {
        Intent intent = new Intent(getActivity(), LookupActivity.class);
        intent.putExtra(Application.EXTRA_CURRENCY_ID, getArguments().getLong(BaseColumns._ID));
        intent.putExtra(Application.IDENTITY_LOOKUP,true);
        getActivity().startActivityForResult(intent, Application.ACTIVITY_LOOKUP);
    }

    public void actionRevoke() {
        /*
        UcoinIdentity identity = getArguments().getParcelable(UcoinIdentity.class.getSimpleName());
        identity.setSync(IdentityState.SEND_REVOKE);
        Application.requestSync(getActivity());
        */
    }

    private class IdentityPagerAdapter extends FragmentStatePagerAdapter {

        public IdentityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 3;
        }

        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link io.ucoin.app.widget.SlidingTabLayout}.
         * <p/>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return getString(R.string.certification);
            else if (position == 1)
                return getString(R.string.membership);
            else
                return getString(R.string.self);
        }

        @Override
        public android.app.Fragment getItem(int i) {
            android.app.Fragment fragment;

            if (i == 0) {
                fragment = MemberListFragment.newInstance(
                        getArguments().getLong(BaseColumns._ID),
                        mCursor.getLong(mCursor.getColumnIndex(SQLiteView.Identity._ID)));
                fragment.setHasOptionsMenu(true);
            } else if (i == 1) {
                fragment = MembershipListFragment.newInstance(mCursor.getLong(mCursor.getColumnIndex(SQLiteView.Identity._ID)));
                fragment.setHasOptionsMenu(true);
            } else {
                fragment = SelfCertificationListFragment.newInstance(mCursor.getLong(mCursor.getColumnIndex(SQLiteView.Identity._ID)));
                fragment.setHasOptionsMenu(true);
            }
            return fragment;
        }
    }
}
