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
import io.ucoin.app.model.UcoinPendingEndpoints;
import io.ucoin.app.sqlite.PendingEndpoints;

public class AddPeerDialogFragment extends DialogFragment {

    public static AddPeerDialogFragment newInstance() {
        AddPeerDialogFragment fragment = new AddPeerDialogFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_add_peer_dialog, null);
        final EditText addressView = (EditText) view.findViewById(R.id.address);
        final EditText portView = (EditText) view.findViewById(R.id.port);
        final Button posButton = (Button) view.findViewById(R.id.positive_button);

        portView.setOnEditorActionListener(new EditText.OnEditorActionListener() {
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
                String address;
                int port = 0;

                //check address
                address = addressView.getText().toString().trim();
                if (!address.isEmpty()) {
                    if (!InetAddressUtils.isIPv4Address(address) &&
                            !InetAddressUtils.isIPv6Address(address) &&
                            !Patterns.WEB_URL.matcher(address).matches()) {
                        addressView.setError(getString(R.string.invalid_peer_address));
                        return;
                    }
                }

                //check port
                if (portView.getText().toString().trim().isEmpty()) {
                    portView.setError(getString(R.string.port_cannot_be_empty));
                    return;
                } else if (Integer.parseInt(portView.getText().toString()) <= 0 ||
                        65535 <= Integer.parseInt(portView.getText().toString())) {
                    portView.setError(getString(R.string.invalid_peer_port));
                    return;
                } else {
                    port = Integer.parseInt(portView.getText().toString());
                }

                UcoinPendingEndpoints endpoints = new PendingEndpoints(getActivity());
                endpoints.add(address, port);

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