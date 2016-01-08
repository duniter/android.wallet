package io.ucoin.app.fragment.currency;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import io.ucoin.app.R;
import io.ucoin.app.UcoinUris;
import io.ucoin.app.activity.CurrencyActivity;
import io.ucoin.app.fragment.identity.MemberListFragment;
import io.ucoin.app.fragment.identity.MembershipListFragment;
import io.ucoin.app.fragment.identity.SelfCertificationListFragment;
import io.ucoin.app.model.UcoinContact;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinIdentity;
import io.ucoin.app.model.http_api.WotLookup;
import io.ucoin.app.model.sql.sqlite.Contacts;
import io.ucoin.app.model.sql.sqlite.Currency;
import io.ucoin.app.service.Format;
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
    private ImageButton mContactButton;
    private UcoinContact mContact;

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
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.identity));
        ((CurrencyActivity) getActivity()).setDrawerIndicatorEnabled(false);

        mHeaderLayout = (LinearLayout) getView().findViewById(R.id.header);
        mViewPager = (ViewPager) getView().findViewById(R.id.viewpager);
        mSlidingTabLayout = (SlidingTabLayout) getView().findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);

        mUid = (TextView) view.findViewById(R.id.uid);

        mContactButton = (ImageButton) getView().findViewById(R.id.contact_button);
        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionContact();
            }
        });

        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_identity, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteItem = menu.findItem(R.id.action_delete);

        if (mContact==null) {
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
        try {
            UcoinIdentity identity = currency.addIdentity(uid, publicKey);
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }
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
            String uid = data.getString(data.getColumnIndex(SQLiteView.Identity.UID));
            Long currencyId = data.getLong(data.getColumnIndex(SQLiteView.Identity.CURRENCY_ID));
            String publicKey = data.getString(data.getColumnIndex(SQLiteView.Identity.PUBLIC_KEY));
            mContact = new Contacts(getActivity(),currencyId).getByPublicKey(publicKey);
            if(mContact!=null){
                mUid.setText(mContact.name().concat(" (").concat(mContact.uid()).concat(")"));
                mContactButton.setVisibility(View.GONE);
            }else{
                mUid.setText(uid);
                mContactButton.setVisibility(View.VISIBLE);
            }
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

    public void actionRevoke() {
        /*
        UcoinIdentity identity = getArguments().getParcelable(UcoinIdentity.class.getSimpleName());
        identity.setSync(IdentityState.SEND_REVOKE);
        Application.requestSync(getActivity());
        */
    }

    public void actionContact(){
        final String uid = mCursor.getString(mCursor.getColumnIndex(SQLiteView.Identity.UID));
        final String pubKey = mCursor.getString(mCursor.getColumnIndex(SQLiteView.Identity.PUBLIC_KEY));
        final Currency currency = new Currency(
                getActivity(),
                mCursor.getLong(mCursor.getColumnIndex(SQLiteView.Identity.CURRENCY_ID)));

        if (uid.isEmpty()) {
            return;
        }
        if (pubKey.isEmpty()) {
            return;
        }
        if(currency==null){
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Contact");
        alertDialogBuilder.setMessage("Name of contact :");

        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint(uid);
        alertDialogBuilder.setView(input);

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String name = input.getText().toString();
                if (name.length() == 0 || name.equals(" ")) {
                    name = uid;
                }
                currency.contacts().add(name, uid, pubKey);
                askContactInPhone(name, uid, pubKey, currency);
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void askContactInPhone(final String name, final String uid, final String pubKey, final Currency currency){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Contact");
        alertDialogBuilder
                .setMessage("Do you want to save the contact on your phone ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addNewContactInPhone(name,uid,pubKey,currency);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void addNewContactInPhone(String name, String uid, String pubKey, Currency currency){
        String url = Format.createUri(Format.LONG,uid, pubKey, currency.name());

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);

        ArrayList<ContentValues> data = new ArrayList<ContentValues>();
        ContentValues row1 = new ContentValues();

        row1.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
        row1.put(ContactsContract.CommonDataKinds.Website.URL, url);
        //row1.put(ContactsContract.CommonDataKinds.Website.LABEL, "abc");
        row1.put(ContactsContract.CommonDataKinds.Website.TYPE, ContactsContract.CommonDataKinds.Website.TYPE_HOME);
        data.add(row1);
        intent.putExtra(ContactsContract.Intents.Insert.DATA, data);
        intent.putExtra("finishActivityOnSaveCompleted", true);
//              Uri dataUri = getActivity().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, row1);
        startActivity(intent);
        //------------------------------- end of inserting contact in the phone
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
