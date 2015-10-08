package io.ucoin.app.fragment.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.ucoin.app.Application;
import io.ucoin.app.R;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.sql.sqlite.Currency;
import io.ucoin.app.task.GenerateKeysTask;
import io.ucoin.app.technical.crypto.Base58;
import io.ucoin.app.technical.crypto.KeyPair;

public class AddWalletDialogFragment extends DialogFragment
        implements Button.OnClickListener {

    private LinearLayout mFieldLayout;
    private RelativeLayout mButtonLayout;
    private LinearLayout mProgressLayout;
    private Activity mActivity;

    private EditText mAlias;
    private EditText mSalt;
    private EditText mPassword;
    private EditText mConfirmPassword;

    public static AddWalletDialogFragment newInstance(Long currencyId) {
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putLong(BaseColumns._ID, currencyId);
        AddWalletDialogFragment fragment = new AddWalletDialogFragment();
        fragment.setArguments(newInstanceArgs);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_add_wallet_dialog, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mActivity = getActivity();
        getDialog().setTitle(R.string .add_wallet);

        mFieldLayout = (LinearLayout) view.findViewById(R.id.field_layout);
        mButtonLayout = (RelativeLayout) view.findViewById(R.id.button_layout);
        mProgressLayout = (LinearLayout) view.findViewById(R.id.progress_layout);

        final TextView saltHint = (TextView) view.findViewById(R.id.salt_tip);
        final TextView passwordHint = (TextView) view.findViewById(R.id.password_tip);

        mAlias = (EditText) view.findViewById(R.id.alias);
        mSalt = (EditText) view.findViewById(R.id.salt);
        mPassword = (EditText) view.findViewById(R.id.password);
        mConfirmPassword = (EditText) view.findViewById(R.id.confirm_password);
        final Button posButton = (Button) view.findViewById(R.id.positive_button);

        mSalt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    saltHint.setVisibility(View.VISIBLE);
                } else {
                    saltHint.setVisibility(View.GONE);
                }
            }
        });
        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    passwordHint.setVisibility(View.VISIBLE);
                } else {
                    passwordHint.setVisibility(View.GONE);
                }
            }
        });

        mConfirmPassword.setOnFocusChangeListener(mPassword.getOnFocusChangeListener());
        mConfirmPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
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


        posButton.setOnClickListener(this);

        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        //validate alias
        if (mAlias.getText().toString().isEmpty()) {
            mAlias.setError(getString(R.string.salt_cannot_be_empty));
            return;
        }

        //validate salt
        if (mSalt.getText().toString().isEmpty()) {
            mSalt.setError(getString(R.string.salt_cannot_be_empty));
            return;
        }

        //validate password
        if (mPassword.getText().toString().isEmpty()) {
            mPassword.setError(getString(R.string.password_cannot_be_empty));
            return;
        }

        if (mConfirmPassword.getText().toString().isEmpty()) {
            mConfirmPassword.setError(getString(R.string.confirm_password_cannot_be_empty));
            return;
        }

        if (!mPassword.getText().toString().equals(mConfirmPassword.getText().toString())) {
            mPassword.setError(getString(R.string.passwords_dont_match));
            mConfirmPassword.setError(getString(R.string.passwords_dont_match));
            return;
        }

        GenerateKeysTask task = new GenerateKeysTask(new GenerateKeysTask.OnTaskFinishedListener() {
            @Override
            public void onTaskFinished(KeyPair keyPair) {
                UcoinCurrency currency = new Currency(mActivity, getArguments().getLong(BaseColumns._ID));

                UcoinWallet wallet = currency.wallets().add(
                        mSalt.getText().toString(),
                        mAlias.getText().toString(),
                        Base58.encode(keyPair.getPubKey()),
                        Base58.encode(keyPair.getSecKey()));

                if (wallet == null) {
                    Toast.makeText(mActivity,
                            getString(R.string.wallet_already_exists),
                            Toast.LENGTH_SHORT).show();
                }

                Application.requestSync();
                dismiss();
            }
        });

        Bundle args = new Bundle();
        args.putString("salt", mSalt.getText().toString());
        args.putString("password", mPassword.getText().toString());
        mFieldLayout.setVisibility(View.GONE);
        mButtonLayout.setVisibility(View.GONE);
        mProgressLayout.setVisibility(View.VISIBLE);
        task.execute(args);
    }
}



