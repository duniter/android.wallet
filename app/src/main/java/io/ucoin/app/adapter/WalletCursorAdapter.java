package io.ucoin.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import io.ucoin.app.R;
import io.ucoin.app.sqlite.SQLiteTable;
import io.ucoin.app.sqlite.SQLiteView;


public class WalletCursorAdapter extends CursorAdapter {

    public WalletCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return inflater.inflate(R.layout.list_item_wallet, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView alias = (TextView) view.findViewById(R.id.alias);
        int currencyIndex = cursor.getColumnIndex(SQLiteTable.Wallet.ALIAS);
        String strAlias = cursor.getString(currencyIndex);
        if (strAlias.isEmpty()) {
            alias.setVisibility(View.GONE);
        } else {
            alias.setText(cursor.getString(currencyIndex));
        }
        TextView publicKey = (TextView) view.findViewById(R.id.public_key);
        int publicKeyIndex = cursor.getColumnIndex(SQLiteView.Wallet.PUBLIC_KEY);
        publicKey.setText(cursor.getString(publicKeyIndex));

        TextView qAmount = (TextView) view.findViewById(R.id.quantitative_amount);
        int amountIndex = cursor.getColumnIndex(SQLiteView.Wallet.QUANTITATIVE_AMOUNT);
        String amountString;
        if(cursor.isNull(amountIndex)) {
            amountString = context.getString(R.string.empty_wallet);
        } else {
            int currencyNameIndex = cursor.getColumnIndex(SQLiteView.Wallet.CURRENCY_NAME);
            amountString = Long.toString(cursor.getLong(amountIndex));
            amountString += " " + cursor.getString(currencyNameIndex);
        }
        qAmount.setText(amountString);

        TextView rAmount = (TextView) view.findViewById(R.id.relative_amount);
        amountIndex = cursor.getColumnIndex(SQLiteView.Wallet.RELATIVE_AMOUNT);
        if(cursor.isNull(amountIndex)) {
            amountString = context.getString(R.string.empty_wallet);
        } else {
            amountString = Double.toString(cursor.getFloat(amountIndex));
            amountString += " U.D.";
        }
        rAmount.setText(amountString);
    }
}
