package io.ucoin.app.fragment.currency;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.ucoin.app.Fragment;
import io.ucoin.app.R;
import io.ucoin.app.activity.MainActivity;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.view.SlidingTabLayout;

public class CurrencyFragment extends Fragment {

   public static CurrencyFragment newInstance(UcoinCurrency currency) {
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putParcelable(UcoinCurrency.class.getSimpleName(), currency);

        CurrencyFragment fragment = new CurrencyFragment();
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

        return inflater.inflate(R.layout.fragment_currency,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.currency));
        ((MainActivity) getActivity()).setBackButtonEnabled(false);

        Bundle args = getArguments();
        UcoinCurrency currency = args.getParcelable(UcoinCurrency.class.getSimpleName());

        TextView currencyName = (TextView) view.findViewById(R.id.currency_name);
        currencyName.setText(currency.currencyName());

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager;
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new CurrencyPagerAdapter(getChildFragmentManager()));

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        SlidingTabLayout slidingTabLayout;
        slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
    }

    private class CurrencyPagerAdapter extends FragmentPagerAdapter {

        public CurrencyPagerAdapter(FragmentManager fm) {
            super(fm);
        }



        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 4;
        }

        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link io.ucoin.app.view.SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0)
                return "wallets";
            else if(position == 1)
                return "members";
            else if(position == 2)
                return "parameters";
            else
                return "peers";
        }

        @Override
        public android.app.Fragment getItem(int i) {
            Bundle args = getArguments();
            UcoinCurrency currency = args.getParcelable(UcoinCurrency.class.getSimpleName());

            android.app.Fragment fragment;
            if(i == 0) {
                fragment =  WalletListFragment.newInstance(currency);
                fragment.setHasOptionsMenu(true);
            }
            else if(i == 1) {
                fragment = MemberListFragment.newInstance(currency);
                fragment.setHasOptionsMenu(true);
            }
            else if(i == 2) {
                fragment = CurrencyParametersFragment.newInstance(currency);
                fragment.setHasOptionsMenu(true);
            }
            else {
                fragment = PeerListFragment.newInstance(currency);
                fragment.setHasOptionsMenu(true);
            }

            return fragment;
        }
    }
}
