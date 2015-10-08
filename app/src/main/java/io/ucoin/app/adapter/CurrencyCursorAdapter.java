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
import io.ucoin.app.enumeration.DayOfWeek;
import io.ucoin.app.enumeration.Month;
import io.ucoin.app.sqlite.SQLiteView;


public class CurrencyCursorAdapter extends CursorAdapter{

    public CurrencyCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return inflater.inflate(R.layout.list_item_currency, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView currency = (TextView) view.findViewById(R.id.currency_name);
        int currencyIndex = cursor.getColumnIndex(SQLiteView.Currency.NAME);
        currency.setText(cursor.getString(currencyIndex));

        TextView membersCount = (TextView) view.findViewById(R.id.members_count);
        int membersCountIndex = cursor.getColumnIndex(SQLiteView.Currency.MEMBERS_COUNT);
        membersCount.setText(context.getResources().getString(R.string.members) + " : " + cursor.getString(membersCountIndex));

        TextView monetaryMass = (TextView) view.findViewById(R.id.monetary_mass);
        int monetaryMassIndex = cursor.getColumnIndex(SQLiteView.Currency.MONETARY_MASS);
        DecimalFormat formatter = new DecimalFormat("#,###");
        monetaryMass.setText(context.getResources().getString(R.string.monetary_mass) + " : " +
                formatter.format(cursor.getLong(monetaryMassIndex)));

        TextView quantBalance = (TextView) view.findViewById(R.id.quantitative_balance);
        int quantBalanceIndex = cursor.getColumnIndex(SQLiteView.Currency.QUANT_BALANCE);
        quantBalance.setText(context.getResources().getString(R.string.balance) + " : " +
                formatter.format(cursor.getLong(quantBalanceIndex)));

        TextView currentBlockNumber = (TextView) view.findViewById(R.id.block_number);
        int currentBlockNumberIndex = cursor.getColumnIndex(SQLiteView.Currency.CURRENT_BLOCK);
        currentBlockNumber.setText(context.getResources().getString(R.string.current_block) + " : " + cursor.getString(currentBlockNumberIndex));

        TextView date = (TextView) view.findViewById(R.id.date);
        String d = cursor.getString(cursor.getColumnIndex(SQLiteView.Currency.BLOCK_DAY_OF_WEEK));
        if (d == null) d = Integer.toString(DayOfWeek.UNKNOWN.ordinal());

        String m = cursor.getString(cursor.getColumnIndex(SQLiteView.Currency.BLOCK_MONTH));
        if (m == null) m = Integer.toString(Month.UNKNOWN.ordinal());
        Month month = Month.fromInt(Integer.parseInt(m));

        String dayOfWeek = DayOfWeek.fromInt(Integer.parseInt(d)).toString(context);

        String dateStr = dayOfWeek + " ";
        dateStr += cursor.getString(cursor.getColumnIndex(SQLiteView.Currency.BLOCK_DAY)) + " ";
        dateStr += month.toString(context) + " ";
        dateStr += cursor.getString(cursor.getColumnIndex(SQLiteView.Currency.BLOCK_YEAR)) + " ";
        dateStr += cursor.getString(cursor.getColumnIndex(SQLiteView.Currency.BLOCK_HOUR)) + " ";

        date.setText(dateStr);
    }
}