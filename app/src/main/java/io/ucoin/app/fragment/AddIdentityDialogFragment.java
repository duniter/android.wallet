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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.ucoin.app.DialogFragment;
import io.ucoin.app.R;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinIdentity;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.service.CryptoService;
import io.ucoin.app.service.ServiceLocator;
import io.ucoin.app.technical.AsyncTaskHandleException;
import io.ucoin.app.technical.crypto.Base58;
import io.ucoin.app.technical.crypto.KeyPair;

public class AddIdentityDialogFragment extends DialogFragment {

    private OnIdentityCreatedListener mListener;
    private String mUid;
    private String mSalt;
    private LinearLayout mFieldLayout;
    private RelativeLayout mButtonLayout;
    private LinearLayout mProgressLayout;

    public static AddIdentityDialogFragment newInstance(UcoinCurrency currency, OnIdentityCreatedListener listener) {
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putParcelable(UcoinCurrency.class.getSimpleName(), currency);
        AddIdentityDialogFragment fragment = new AddIdentityDialogFragment();
        fragment.setArguments(newInstanceArgs);
        fragment.setOnIdentityCreatedListener(listener);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_add_identity_dialog, null);

        mFieldLayout = (LinearLayout) view.findViewById(R.id.field_layout);
        mButtonLayout = (RelativeLayout) view.findViewById(R.id.button_layout);
        mProgressLayout = (LinearLayout) view.findViewById(R.id.progress_layout);

        final TextView saltHint = (TextView) view.findViewById(R.id.salt_tip);
        final TextView passwordHint = (TextView) view.findViewById(R.id.password_tip);
        final TextView uid = (EditText) view.findViewById(R.id.uid);
        final TextView salt = (EditText) view.findViewById(R.id.salt);
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
                //validate UIDt
                if (uid.getText().toString().isEmpty()) {
                    uid.setError(getString(R.string.uid_cannot_be_empty));
                    return;
                }
                mUid = uid.getText().toString();
                //validate salt
                if (salt.getText().toString().isEmpty()) {
                    salt.setError(getString(R.string.salt_cannot_be_empty));
                    return;
                }
                mSalt = salt.getText().toString();
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

                GenerateKeysTask task = new GenerateKeysTask();
                Bundle args = new Bundle();
                args.putString("salt", mSalt);
                mFieldLayout.setVisibility(View.GONE);
                mButtonLayout.setVisibility(View.GONE);
                mProgressLayout.setVisibility(View.VISIBLE);
                args.putString("password", password.getText().toString());
                task.execute(args);
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

    private void setOnIdentityCreatedListener(OnIdentityCreatedListener listener) {
        mListener = listener;
    }

    public interface OnIdentityCreatedListener {
        public void onIdentityCreated(UcoinIdentity identity);
    }

    public class GenerateKeysTask extends AsyncTaskHandleException<Bundle, Void, KeyPair> {

        @Override
        protected KeyPair doInBackgroundHandleException(Bundle... args) throws Exception {

            String salt = args[0].getString(("salt"));
            String password = args[0].getString(("password"));
            //generate keys

            CryptoService service = ServiceLocator.instance().getCryptoService();
            return service.getKeyPair(salt, password);
        }

        @Override
        protected void onSuccess(KeyPair keys) {
            Bundle newInstanceArgs = getArguments();
            UcoinCurrency currency = newInstanceArgs.getParcelable(UcoinCurrency.class.getSimpleName());

            UcoinWallet wallet = currency.wallets().newWallet(
                    mSalt,
                    Base58.encode(keys.getPubKey()),
                    Base58.encode(keys.getSecKey()),
                    mUid);
            wallet = currency.wallets().add(wallet);

            UcoinIdentity identity = currency.newIdentity(wallet.id(), mUid);
            identity = currency.setIdentity(identity);
            currency.identityId(identity.id());

            mListener.onIdentityCreated(identity);
            dismiss();
        }

        @Override
        protected void onFailed(Throwable t) {
            mFieldLayout.setVisibility(View.VISIBLE);
            mButtonLayout.setVisibility(View.VISIBLE);
            mProgressLayout.setVisibility(View.GONE);
            t.printStackTrace();
            Toast.makeText(getActivity().getApplicationContext(),
                    t.toString(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}



