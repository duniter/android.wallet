package io.ucoin.app.fragment.identity;

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
import io.ucoin.app.fragment.wallet.SourceListFragment;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinIdentity;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.enums.SourceType;
import io.ucoin.app.view.SlidingTabLayout;

public class IdentityFragment extends Fragment {

    private TextView mQuantitativeBalance;

    public static IdentityFragment newInstance(UcoinIdentity identity) {
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putParcelable(UcoinIdentity.class.getSimpleName(), identity);

        IdentityFragment fragment = new IdentityFragment();
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

        return inflater.inflate(R.layout.fragment_identity,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.identity));
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setBackButtonEnabled(false);

        Bundle args = getArguments();
        UcoinIdentity identity = args.getParcelable(UcoinIdentity.class.getSimpleName());

        TextView uid = (TextView) view.findViewById(R.id.uid);
        uid.setText(identity.uid());

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager;
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new IdentityPagerAdapter(getChildFragmentManager()));

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        SlidingTabLayout slidingTabLayout;
        slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_identity, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private class IdentityPagerAdapter extends FragmentPagerAdapter {

        public IdentityPagerAdapter(FragmentManager fm) {
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
         * <p/>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return getString(R.string.dividend);
            else if (position == 1)
                return getString(R.string.received);
            else if (position == 2)
                return getString(R.string.certifiers);
            else
                return getString(R.string.certifiees);
        }

        @Override
        public android.app.Fragment getItem(int i) {
            Bundle args = getArguments();
            UcoinIdentity identity = args.getParcelable(UcoinIdentity.class.getSimpleName());

            android.app.Fragment fragment;
            if (i == 0) {
                fragment = SourceListFragment.newInstance(identity.wallet(), SourceType.D);
                fragment.setHasOptionsMenu(false);
            } else if (i == 1) {
                fragment = SourceListFragment.newInstance(identity.wallet(), SourceType.T);
                fragment.setHasOptionsMenu(false);
            } else if (i == 2) {
                fragment = SourceListFragment.newInstance(identity.wallet(), SourceType.D);
                fragment.setHasOptionsMenu(false);
            } else {
                fragment = SourceListFragment.newInstance(identity.wallet(), SourceType.D);
                fragment.setHasOptionsMenu(false);
            }
            return fragment;
        }
    }
}
