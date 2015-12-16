package io.ucoin.app.fragment.dialog;

import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import io.ucoin.app.Application;
import io.ucoin.app.R;
import io.ucoin.app.activity.CurrencyActivity;
import io.ucoin.app.adapter.CurrencyCursorAdapterSimple;
import io.ucoin.app.model.UcoinCurrencies;
import io.ucoin.app.model.sql.sqlite.Currencies;

public class ListCurrencyDialogFragment extends DialogFragment{

    private ListView list;

    private TextView textButton;

    public ListCurrencyDialogFragment(TextView text) {
        textButton = text;
    }

    public static ListCurrencyDialogFragment newInstance(TextView text) {
        return new ListCurrencyDialogFragment(text);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_list_dialog, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        getDialog().setTitle(R.string.selected_currency);

        UcoinCurrencies currencies = new Currencies(Application.getContext());

        CurrencyCursorAdapterSimple adapter = new CurrencyCursorAdapterSimple(getActivity(), currencies.getAll(), 0);

        list = (ListView) view.findViewById(R.id.list_item);

        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                getActivity().getIntent().putExtra(Application.EXTRA_CURRENCY_ID, cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
                if(getActivity() instanceof CurrencyActivity){
                    textButton.callOnClick();
                    getActivity().getIntent().putExtra(Application.EXTRA_CURRENCY_ID, Long.valueOf(-1));
                }
                dismiss();
            }
        });

//        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
    }
}