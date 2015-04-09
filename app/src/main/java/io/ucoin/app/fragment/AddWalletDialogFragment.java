package io.ucoin.app.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.ucoin.app.DialogFragment;
import io.ucoin.app.R;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.service.CryptoService;
import io.ucoin.app.service.ServiceLocator;
import io.ucoin.app.technical.crypto.Base58;
import io.ucoin.app.technical.crypto.KeyPair;

public class AddWalletDialogFragment extends DialogFragment {

    public static AddWalletDialogFragment newInstance(UcoinCurrency currency) {
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putSerializable(UcoinCurrency.class.getSimpleName(), currency);
        AddWalletDialogFragment fragment = new AddWalletDialogFragment();
        fragment.setArguments(newInstanceArgs);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_add_wallet_dialog, null);

        final TextView saltHint = (TextView) view.findViewById(R.id.salt_tip);
        final TextView passwordHint = (TextView) view.findViewById(R.id.password_tip);
        final EditText alias = (EditText) view.findViewById(R.id.alias);
        final EditText salt = (EditText) view.findViewById(R.id.salt);
        final EditText password = (EditText) view.findViewById(R.id.password);
        final EditText confirmPassword = (EditText) view.findViewById(R.id.confirm_password);
        final Button posButton = (Button) view.findViewById(R.id.positive_button);

        salt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    saltHint.setVisibility(View.VISIBLE);
                } else {
                    saltHint.setVisibility(View.GONE);
                }
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    passwordHint.setVisibility(View.VISIBLE);
                } else {
                    passwordHint.setVisibility(View.GONE);
                }
            }
        });

        confirmPassword.setOnFocusChangeListener(password.getOnFocusChangeListener());
        confirmPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    posButton.performClick();
                    return true;
                }
                return false;
            }
        });


        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate salt
                if (salt.getText().toString().isEmpty()) {
                    salt.setError(getString(R.string.salt_cannot_be_empty));
                    return;
                }

                //validate password
                if (password.getText().toString().isEmpty()) {
                    password.setError(getString(R.string.password_cannot_be_empty));
                    return;
                }

                if (confirmPassword.getText().toString().isEmpty()) {
                    confirmPassword.setError(getString(R.string.confirm_password_cannot_be_empty));
                    return;
                }

                if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                    password.setError(getString(R.string.passwords_dont_match));
                    confirmPassword.setError(getString(R.string.passwords_dont_match));
                    return;
                }

                //generate keys
                CryptoService service = ServiceLocator.instance().getCryptoService();
                KeyPair keys = service.getKeyPair(salt.getText().toString(),
                        password.getText().toString());

                String aliasStr = alias.getText().toString();

                Bundle newInstanceArgs = getArguments();
                UcoinCurrency currency =
                        (UcoinCurrency) newInstanceArgs.getSerializable(UcoinCurrency.class.getSimpleName());

                UcoinWallet wallet = currency.wallets().add(
                        salt.getText().toString(),
                        aliasStr,
                        Base58.encode(keys.getPubKey()),
                        Base58.encode(keys.getSecKey()));

                if (wallet == null) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            getString(R.string.wallet_already_exists),
                            Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        });

        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }
}



