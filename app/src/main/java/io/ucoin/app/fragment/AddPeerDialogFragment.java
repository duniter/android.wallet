package io.ucoin.app.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.conn.util.InetAddressUtils;

import io.ucoin.app.R;
import io.ucoin.app.model.UcoinCurrency;

public class AddPeerDialogFragment extends DialogFragment {

    private OnPeerAddListener mListener;

    public static AddPeerDialogFragment newInstance(OnPeerAddListener listener) {
        AddPeerDialogFragment fragment = new AddPeerDialogFragment();
        fragment.setOnPeerAddListener(listener);
        return fragment;
    }


    public static AddPeerDialogFragment newInstance(OnPeerAddListener listener, UcoinCurrency currency) {
        AddPeerDialogFragment fragment = new AddPeerDialogFragment();
        fragment.setOnPeerAddListener(listener);
        Bundle newInstanceArgs = new Bundle();
        newInstanceArgs.putParcelable(UcoinCurrency.class.getSimpleName(), currency);
        fragment.setArguments(newInstanceArgs);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_add_peer_dialog, null);
        final EditText address = (EditText) view.findViewById(R.id.address);
        final EditText port = (EditText) view.findViewById(R.id.port);
        final Button posButton = (Button) view.findViewById(R.id.positive_button);

        port.setOnEditorActionListener(new EditText.OnEditorActionListener() {
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
                Bundle peerArgs = new Bundle();

                //check address
                if (!address.getText().toString().trim().isEmpty() &&
                        (InetAddressUtils.isIPv4Address(address.getText().toString()) ||
                                InetAddressUtils.isIPv6Address(address.getText().toString()) ||
                                Patterns.WEB_URL.matcher(address.getText().toString()).matches())) {
                    peerArgs.putString("address", address.getText().toString());
                } else {
                    address.setError(getString(R.string.invalid_node_address));
                    return;
                }

                //check port
                if (!port.getText().toString().trim().isEmpty() &&
                        (0 <= Integer.parseInt(port.getText().toString()) ||
                                Integer.parseInt(port.getText().toString()) <= 65535)) {
                    peerArgs.putInt("port", Integer.parseInt(port.getText().toString()));
                } else {
                    port.setError(getString(R.string.invalid_node_port));
                    return;
                }

                //this fragment can be used either for adding a new currency or just a node
                //if a currency has been provided it means we only had a node, thus
                //the currency must be passed along for further peer creation
                Bundle args = getArguments();
                if (args != null) {
                    UcoinCurrency currency = args.getParcelable(UcoinCurrency.class.getSimpleName());
                    peerArgs.putParcelable(UcoinCurrency.class.getSimpleName(), currency);
                }

                dismiss();
                mListener.onPeerAdd(peerArgs);
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

    private void setOnPeerAddListener(OnPeerAddListener listener) {
        mListener = listener;
    }

    public interface OnPeerAddListener {
        public void onPeerAdd(Bundle args);
    }
}



