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


public class CertificationCursorAdapter extends CursorAdapter{

    public CertificationCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return inflater.inflate(R.layout.list_item_certification, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String colon = " : ";

        TextView memberUid = (TextView) view.findViewById(R.id.member_uid);
        int memberUidIndex = cursor.getColumnIndex(Contract.Member.UID);
        memberUid.setText(colon + cursor.getString(memberUidIndex));

        TextView block = (TextView) view.findViewById(R.id.block);
        int blockIndex = cursor.getColumnIndex(Contract.Certification.BLOCK);
        block.setText(colon + cursor.getString(blockIndex));

        TextView medianTime = (TextView) view.findViewById(R.id.median_time);
        int medianTimeIndex = cursor.getColumnIndex(Contract.Certification.MEDIAN_TIME);
        medianTime.setText(colon + cursor.getString(medianTimeIndex));
    }
}
