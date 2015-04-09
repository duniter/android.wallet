package io.ucoin.app;

import io.ucoin.app.model.UcoinCurrencies;

public abstract class ListFragment extends android.app.ListFragment {

    public UcoinCurrencies currencies() {
        return ((Application)getActivity().getApplication()).currencies();
    }
}
