package io.ucoin.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;

import io.ucoin.app.R;
import io.ucoin.app.fragment.currency.WalletListFragment;
import io.ucoin.app.model.UcoinCurrency;
import io.ucoin.app.model.UcoinIdentity;
import io.ucoin.app.model.UcoinWallet;
import io.ucoin.app.model.sql.sqlite.Currencies;
import io.ucoin.app.model.sql.sqlite.Wallets;
import io.ucoin.app.service.Format;
import io.ucoin.app.sqlite.SQLiteView;


public class WalletCursorAdapter extends CursorAdapter {

    private int nbSection;
    private Context mContext;
    private Cursor mCursor;
    private HashMap<Integer, String> mSectionPosition;
    private Activity activity;

    public WalletCursorAdapter(Context context, final Cursor c, int flags, Activity activity) {
        super(context, c, flags);
        mContext = context;
        mCursor = c;
        this.activity = activity;
        mSectionPosition = new LinkedHashMap<>(16, (float) 0.75, false);
        nbSection =0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (mSectionPosition.size()>1 && mSectionPosition.containsKey(position)) {
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
        if(position-nbSection==(mCursor.getCount()-1)){
            nbSection=0;
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
    public int getCount() {
        int result;
        if(mSectionPosition.size()>1){
            result =super.getCount() + mSectionPosition.size();
        }else{
            result = super.getCount();
        }
        return result;
    }

    public Long getIdWallet(int position){
        int nbSec = 0;
        if(mSectionPosition.size()>1) {
            for (Integer i : mSectionPosition.keySet()) {
                if (position > i) {
                    nbSec += 1;
                }
            }
        }
        position -= nbSec;
        mCursor.moveToPosition(position);
        return mCursor.getLong(mCursor.getColumnIndex(SQLiteView.Wallet._ID));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return inflater.inflate(R.layout.list_item_wallet, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int idIndex = cursor.getColumnIndex(SQLiteView.Wallet._ID);
        int aliasIndex = cursor.getColumnIndex(SQLiteView.Wallet.ALIAS);
        int publicKeyIndex = cursor.getColumnIndex(SQLiteView.Wallet.PUBLIC_KEY);
        int quantitativeAmountIndex = cursor.getColumnIndex(SQLiteView.Wallet.QUANTITATIVE_AMOUNT);
        int relativeAmountIndex = cursor.getColumnIndex(SQLiteView.Wallet.RELATIVE_AMOUNT);
        int timeAmountIndex = cursor.getColumnIndex(SQLiteView.Wallet.TIME_AMOUNT);
        int currencyNameIndex = cursor.getColumnIndex(SQLiteView.Wallet.CURRENCY_NAME);
        int udValueIndex = cursor.getColumnIndex(SQLiteView.Wallet.UD_VALUE);

        TextView alias = (TextView) view.findViewById(R.id.alias);
        TextView publicKey = (TextView) view.findViewById(R.id.public_key);
        TextView qAmount = (TextView) view.findViewById(R.id.default_amount);
        TextView rAmount = (TextView) view.findViewById(R.id.relative_amount);
        ImageView infoIdentity = (ImageView) view.findViewById(R.id.info_identity);

        final Long walletId = cursor.getLong(idIndex);
        UcoinCurrency cu = new Currencies(context).getByName(cursor.getString(currencyNameIndex));

        UcoinWallet wallet =new Wallets(context,cu.id()).getById(walletId);

        try{
            UcoinIdentity identity = wallet.identity();
            if(identity!=null){
                infoIdentity.setVisibility(View.VISIBLE);
            }else{
                infoIdentity.setVisibility(View.GONE);
            }
        }catch (NullPointerException e){

        }

        infoIdentity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WalletListFragment.WalletItemClick)activity).showIdentity(walletId);
            }
        });



        alias.setText(cursor.getString(aliasIndex));
        publicKey.setText(Format.minifyPubkey(cursor.getString(publicKeyIndex)));

        Format.changeUnit(context,
                new BigInteger(cursor.getString(quantitativeAmountIndex)),
                new BigInteger(cursor.getString(udValueIndex)),
                cu.dt(),
                rAmount,
                qAmount, "");
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
                String name = newCursor.getString(newCursor.getColumnIndex(SQLiteView.Wallet.CURRENCY_NAME));
                if (name == null) name = "UNKNOWN";

                if (!name.equals(section)) {
                    sectionPosition.put(position, name);
                    section = name;
                    position++;
                }
                position++;
            }while (newCursor.moveToNext());
        }
        mSectionPosition = sectionPosition;
        notifyDataSetChanged();

        return newCursor;
    }
}
