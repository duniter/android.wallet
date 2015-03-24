package io.ucoin.app.fragment.wallet;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.ucoin.app.Fragment;
import io.ucoin.app.R;
import io.ucoin.app.activity.MainActivity;
import io.ucoin.app.fragment.common.SourceListFragment;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.enums.SourceType;
import io.ucoin.app.view.SlidingTabLayout;

public class WalletFragment extends Fragment {

    private TextView mQuantitativeBalance;

    public static WalletFragment newInstance(UcoinWallet wallet) {
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putParcelable(UcoinWallet.class.getSimpleName(), wallet);

        WalletFragment fragment = new WalletFragment();
        fragment.setArguments(newInstanceArgs);
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

        return inflater.inflate(R.layout.fragment_wallet,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.wallet));
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setBackButtonEnabled(true);

        Bundle args = getArguments();
        UcoinWallet wallet = args.getParcelable(UcoinWallet.class.getSimpleName());

        TextView alias = (TextView) view.findViewById(R.id.alias);
        if (wallet.alias().isEmpty()) {
            alias.setVisibility(View.GONE);
        } else {
            alias.setText(wallet.alias());
        }

        TextView publicKey = (TextView) view.findViewById(R.id.public_key);
        publicKey.setText(wallet.publicKey());
        mQuantitativeBalance = (TextView) view.findViewById(R.id.quantitativeBalance);
        mQuantitativeBalance.setText(wallet.quantitativeBalance().toString());

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager;
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new WalletPagerAdapter(getChildFragmentManager()));

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        SlidingTabLayout slidingTabLayout;
        slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_wallet, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                Bundle args = getArguments();
                UcoinWallet wallet = args.getParcelable(UcoinWallet.class.getSimpleName());
                UcoinCurrency currency = currencies().getById(wallet.currencyId());
                currency.wallets().delete(wallet.id());
                getFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class WalletPagerAdapter extends FragmentPagerAdapter {

        public WalletPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 2;
        }

        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link io.ucoin.app.view.SlidingTabLayout}.
         * <p/>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.received);
            } else {
                return getString(R.string.dividend);
            }
        }

        @Override
        public android.app.Fragment getItem(int i) {
            Bundle args = getArguments();
            UcoinWallet wallet = args.getParcelable(UcoinWallet.class.getSimpleName());

            android.app.Fragment fragment;
            if (i == 0) {
                fragment = SourceListFragment.newInstance(wallet, SourceType.T);
                fragment.setHasOptionsMenu(false);
            } else {
                fragment = SourceListFragment.newInstance(wallet, SourceType.D);
                fragment.setHasOptionsMenu(false);
            }
            return fragment;
        }
    }
}
