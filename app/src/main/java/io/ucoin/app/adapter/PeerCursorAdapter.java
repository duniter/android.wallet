package io.ucoin.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import io.ucoin.app.R;
import io.ucoin.app.sqlite.Contract;


public class PeerCursorAdapter extends CursorAdapter{

    public PeerCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return inflater.inflate(R.layout.list_item_peer, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView publicKey = (TextView) view.findViewById(R.id.public_key);
        int currencyIndex = cursor.getColumnIndex(Contract.Peer.PUBLIC_KEY);
        publicKey.setText(cursor.getString(currencyIndex));

        TextView signature = (TextView) view.findViewById(R.id.signature);
        int membersCountIndex = cursor.getColumnIndex(Contract.Peer.SIGNATURE);
        signature.setText(cursor.getString(membersCountIndex));
    }
}
