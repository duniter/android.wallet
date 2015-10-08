package io.ucoin.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedHashMap;

import io.ucoin.app.R;
import io.ucoin.app.enumeration.DayOfWeek;
import io.ucoin.app.enumeration.Month;
import io.ucoin.app.sqlite.SQLiteView;

public class UdSectionCursorAdapter extends CursorAdapter {

    private Context mContext;
    private Cursor mCursor;
    private HashMap<Integer, String> mSectionPosition;

    public UdSectionCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        mCursor = c;
        mSectionPosition = new LinkedHashMap<>(16, (float) 0.75, false);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (mSectionPosition.containsKey(position)) {
            v = newSectionView(mContext, parent);
            bindSectionView(v, mContext, mSectionPosition.get(position));
        } else {
            int sectionBeforePosition = 0;
            for(Integer sectionPosition : mSectionPosition.keySet()) {
                if(position > sectionPosition) {
                    sectionBeforePosition++;
                }
            }
            if (!mCursor.moveToPosition(position - sectionBeforePosition)) {
                throw new IllegalStateException("couldn't move cursor to position " + position);
            }
            v = newView(mContext, mCursor, parent);
            bindView(v, mContext, mCursor);
        }
        return v;
    }

    public View newSectionView(Context context, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return inflater.inflate(R.layout.list_item_section_separator, parent, false);
    }

    public void bindSectionView(View v, Context context, String section) {
        ((TextView) v.findViewById(R.id.month_year)).setText(section);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return inflater.inflate(R.layout.list_item_ud, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String day = DayOfWeek.fromInt(cursor.getInt(cursor.getColumnIndex(SQLiteView.Ud.DAY_OF_WEEK))).toString(context);
        day += " " + cursor.getString(cursor.getColumnIndex(SQLiteView.Ud.DAY));

        ((TextView) view.findViewById(R.id.day)).setText(day);
        ((TextView) view.findViewById(R.id.hour)).setText(cursor.getString(cursor.getColumnIndex(SQLiteView.Ud.HOUR)));
        ((TextView) view.findViewById(R.id.qt_amount)).setText(cursor.getString(cursor.getColumnIndex(SQLiteView.Ud.QUANTITATIVE_AMOUNT)));
        ((TextView) view.findViewById(R.id.rel_amount_now)).setText(cursor.getString(cursor.getColumnIndex(SQLiteView.Tx.RELATIVE_AMOUNT_NOW)));
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        super.swapCursor(newCursor);

        if (newCursor == null) {
            return null;
        }

        mCursor = newCursor;
        mSectionPosition.clear();
        int position = 0;
        String section = "";

        newCursor.moveToPosition(-1);
        while (newCursor.moveToNext()) {
            String month = newCursor.getString(newCursor.getColumnIndex(SQLiteView.Ud.MONTH));
            String year = newCursor.getString(newCursor.getColumnIndex(SQLiteView.Ud.YEAR));
            String newSection = Month.fromInt(Integer.parseInt(month)).toString(mContext) + " " + year;

            if (!newSection.equals(section)) {
                mSectionPosition.put(position, newSection);
                section = newSection;
                position++;
            }
            position++;
        }

        return newCursor;
    }

    @Override
    public int getCount() {
        return super.getCount() + mSectionPosition.size();
    }

    @Override
    public boolean isEnabled(int position) {
        return !mSectionPosition.containsKey(position);
    }
}