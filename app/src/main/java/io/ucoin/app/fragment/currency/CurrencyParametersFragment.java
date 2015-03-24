package io.ucoin.app.fragment.currency;

import android.app.FragmentManager;
import android.os.Bundle;
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
import io.ucoin.app.model.UcoinCurrency;

public class CurrencyParametersFragment extends Fragment {

    public static CurrencyParametersFragment newInstance(UcoinCurrency currency) {
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putParcelable(UcoinCurrency.class.getSimpleName(), currency);

        CurrencyParametersFragment fragment = new CurrencyParametersFragment();
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
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_currency_parameters,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        UcoinCurrency currency = args.getParcelable(UcoinCurrency.class.getSimpleName());

        TextView currencyName = (TextView) view.findViewById(R.id.currency_name);
        TextView c = (TextView) view.findViewById(R.id.c);
        TextView dt = (TextView) view.findViewById(R.id.dt);
        TextView mUd0 = (TextView) view.findViewById(R.id.ud0);
        TextView mSigDelay = (TextView) view.findViewById(R.id.sig_delay);
        TextView mSigValidity = (TextView) view.findViewById(R.id.sig_validity);
        TextView mSigQty = (TextView) view.findViewById(R.id.sig_qty);
        TextView mSigWoT = (TextView) view.findViewById(R.id.sig_woT);
        TextView mMsValidity = (TextView) view.findViewById(R.id.ms_validity);
        TextView mStepMax = (TextView) view.findViewById(R.id.step_max);
        TextView mMedianTimeBlocks = (TextView) view.findViewById(R.id.median_time_blocks);
        TextView mAvgGenTime = (TextView) view.findViewById(R.id.avg_gen_time);
        TextView mDtDiffEval = (TextView) view.findViewById(R.id.dt_diff_eval);
        TextView mBlocksRot = (TextView) view.findViewById(R.id.blocks_rot);
        TextView mPercentRot = (TextView) view.findViewById(R.id.percent_rot);

        TextView memberCount = (TextView) view.findViewById(R.id.members_count);

        //populate views
        String colon = " : ";
        String space = " ";
        currencyName.setText(colon + currency.currencyName());
        c.setText(colon + currency.c().toString());
        dt.setText(colon + currency.dt().toString() + space + getString(R.string.seconds));
        mUd0.setText(colon + currency.ud0().toString());
        mSigDelay.setText(colon + currency.sigDelay().toString() + space + getString(R.string.seconds));
        mSigValidity.setText(colon + currency.sigValidity().toString() + space + getString(R.string.seconds));
        mSigQty.setText(colon + currency.sigQty().toString());
        mSigWoT.setText(colon + currency.sigWoT().toString());
        mMsValidity.setText(colon + currency.msValidity().toString() + space + getString(R.string.seconds));
        mStepMax.setText(colon + currency.stepMax().toString());
        mMedianTimeBlocks.setText(colon + currency.medianTimeBlocks().toString());
        mAvgGenTime.setText(colon + currency.avgGenTime().toString() + space + getString(R.string.seconds));
        mDtDiffEval.setText(colon + currency.dtDiffEval().toString());
        mBlocksRot.setText(colon + currency.blocksRot().toString());
        mPercentRot.setText(colon + currency.percentRot().toString());

        memberCount.setText(colon + currency.membersCount().toString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_currency_parameters, menu);

        Bundle args = getArguments();
        UcoinCurrency currency = args.getParcelable(UcoinCurrency.class.getSimpleName());

        currency = currencies().getByFirstBlockSignature(currency.firstBlockSignature());

        if (currency != null) {
            menu.removeItem(R.id.action_add);
        } else {
            menu.removeItem(R.id.action_join);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                add();
                return true;
            case R.id.action_join:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void add() {
        Bundle args = getArguments();
        UcoinCurrency currency = args.getParcelable(UcoinCurrency.class.getSimpleName());

        ((MainActivity)getActivity()).clearAllFragments();
        currencies().add(currency);
        ((MainActivity)getActivity()).openDrawer();
    }
}
