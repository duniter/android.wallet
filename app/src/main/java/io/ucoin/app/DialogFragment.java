package io.ucoin.app;

import io.ucoin.app.model.UcoinCurrencies;

public abstract class DialogFragment extends android.app.DialogFragment {

    public UcoinCurrencies currencies() {
        return ((Application)getActivity().getApplication()).currencies();
    }
}
