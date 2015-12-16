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
import io.ucoin.app.sqlite.SQLiteTable;

public class ContactSectionCursorAdapter extends CursorAdapter {

    private int nbSection;
    private Context mContext;
    private Cursor mCursor;
    private HashMap<Integer, String> mSectionPosition;

    public ContactSectionCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        mCursor = c;
        mSectionPosition = new LinkedHashMap<>(16, (float) 0.75, false);
        nbSection =0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if(position==0){
            nbSection=0;
        }
        if (mSectionPosition.containsKey(position)) {
            v = newSectionView(mContext, parent);
            bindSectionView(v, mContext, mSectionPosition.get(position));
            nbSection+=1;
        } else {
            if (!mCursor.moveToPosition(position - nbSection)) {
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
        ((TextView) v.findViewById(R.id.section_name)).setText(section);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return inflater.inflate(R.layout.list_item_contact, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.name)).setText(cursor.getString(cursor.getColumnIndex(SQLiteTable.Contact.NAME)));
        ((TextView) view.findViewById(R.id.public_key)).setText(cursor.getString(cursor.getColumnIndex(SQLiteTable.Contact.PUBLIC_KEY)));
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

        HashMap<Integer, String> sectionPosition = new LinkedHashMap<>(16, (float) 0.75, false);
        if(newCursor.moveToFirst()){
            do{
                String newSection = newCursor.getString(newCursor.getColumnIndex(SQLiteTable.Contact.NAME)).substring(0, 1).toUpperCase();
                if (!newSection.equals(section)) {
                    sectionPosition.put(position, newSection);
                    section = newSection;
                    position++;
                }
                position++;
            }while (newCursor.moveToNext());
        }

        mSectionPosition = sectionPosition;
        notifyDataSetChanged();

        return newCursor;
    }

    @Override
    public int getCount() {
        return super.getCount() + mSectionPosition.size();
    }

    public int getRealPosition(int position){
        int nbSec = 0;
        if(mSectionPosition.size()>1) {
            for (Integer i : mSectionPosition.keySet()) {
                if (position > i) {
                    nbSec += 1;
                }
            }
        }
        position -= nbSec;
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return !mSectionPosition.containsKey(position);
    }
}