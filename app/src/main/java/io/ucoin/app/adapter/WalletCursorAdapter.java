package io.ucoin.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;

import io.ucoin.app.R;
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
        int aliasIndex = cursor.getColumnIndex(SQLiteView.Wallet.ALIAS);
        int publicKeyIndex = cursor.getColumnIndex(SQLiteView.Wallet.PUBLIC_KEY);
        int quantitativeAmountIndex = cursor.getColumnIndex(SQLiteView.Wallet.QUANTITATIVE_AMOUNT);
        int relativeAmountIndex = cursor.getColumnIndex(SQLiteView.Wallet.RELATIVE_AMOUNT);
        int currencyNameIndex = cursor.getColumnIndex(SQLiteView.Wallet.CURRENCY_NAME);

        TextView alias = (TextView) view.findViewById(R.id.alias);
        TextView publicKey = (TextView) view.findViewById(R.id.public_key);
        TextView qAmount = (TextView) view.findViewById(R.id.qt_amount);
        TextView rAmount = (TextView) view.findViewById(R.id.relative_amount);

        alias.setText(cursor.getString(aliasIndex));
        publicKey.setText(cursor.getString(publicKeyIndex));
        DecimalFormat formatter = new DecimalFormat("#,###");
        qAmount.setText(formatter.format(cursor.getLong(quantitativeAmountIndex)));
        rAmount.setText(String.format("%.8f", cursor.getDouble(relativeAmountIndex)));
    }
}
