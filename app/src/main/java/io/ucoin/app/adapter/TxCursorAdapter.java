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


public class TxCursorAdapter extends CursorAdapter{

    public TxCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return inflater.inflate(R.layout.list_item_tx, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String colon = " : ";

        TextView comment = (TextView) view.findViewById(R.id.comment);
        int commentIndex = cursor.getColumnIndex(SQLiteTable.Tx.COMMENT);
        comment.setText(colon + cursor.getString(commentIndex));

        TextView block = (TextView) view.findViewById(R.id.block);
        int blockIndex = cursor.getColumnIndex(SQLiteTable.Tx.BLOCK);
        block.setText(colon + cursor.getString(blockIndex));

        TextView hash = (TextView) view.findViewById(R.id.hash);
        int hashIndex = cursor.getColumnIndex(SQLiteTable.Tx.HASH);
        hash.setText(cursor.getString(hashIndex));
    }
}
