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


public class DrawerCurrencyCursorAdapter extends CursorAdapter{

    public DrawerCurrencyCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return inflater.inflate(R.layout.drawer_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView currency = (TextView) view.findViewById(R.id.drawer_list_currency);
        int currencyIndex = cursor.getColumnIndex(Contract.Currency.CURRENCY_NAME);
        currency.setText(cursor.getString(currencyIndex));
    }
}
