package io.ucoin.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;

import io.ucoin.app.R;
import io.ucoin.app.sqlite.SQLiteTable;
import io.ucoin.app.sqlite.SQLiteView;


public class WalletSpinnerAdapter extends CursorAdapter implements SpinnerAdapter {

    public WalletSpinnerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return inflater.inflate(R.layout.spinner_item_wallet, parent, false);
    }

    @Override
    public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.spinner_item_wallet, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView rAmount = (TextView) view.findViewById(R.id.principal_amount);
        int relAmountIndex = cursor.getColumnIndex(SQLiteView.Wallet.RELATIVE_AMOUNT);
        rAmount.setText(String.format("%.8f", cursor.getFloat(relAmountIndex)));

        TextView alias = (TextView) view.findViewById(R.id.alias);
        int aliasIndex = cursor.getColumnIndex(SQLiteTable.Wallet.ALIAS);
        alias.setText(cursor.getString(aliasIndex));

        TextView qAmount = (TextView) view.findViewById(R.id.second_amount);
        int qtAmountIndex = cursor.getColumnIndex(SQLiteView.Wallet.QUANTITATIVE_AMOUNT);
        DecimalFormat formatter = new DecimalFormat("#,###");


        qAmount.setText(formatter.format((cursor.getLong(qtAmountIndex))));
    }

    // View lookup cache
    private static class ViewHolder {
        TextView alias;
        TextView quantitativeAmount;
    }
}
