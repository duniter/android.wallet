package io.ucoin.app;

import io.ucoin.app.model.UcoinCurrencies;

public abstract class Fragment extends android.app.Fragment {

    public UcoinCurrencies currencies() {
        return ((Application)getActivity().getApplication()).currencies();
    }
}