package io.ucoin.app.fragment.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import io.ucoin.app.Application;
import io.ucoin.app.R;
import io.ucoin.app.activity.CurrencyActivity;
import io.ucoin.app.adapter.ListAdapterSimple;

public class ListUnitDialogFragment extends DialogFragment{

    private ListView list;
    private static CurrencyActivity.ChangeUnitforType action;
    private static String type = "";

    public static ListUnitDialogFragment newInstance(CurrencyActivity.ChangeUnitforType a) {
        action = a;
        return new ListUnitDialogFragment();
    }

    public static ListUnitDialogFragment newInstance(CurrencyActivity.ChangeUnitforType a, String t) {
        type =t;
        action = a;
        return new ListUnitDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_list_dialog, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        getDialog().setTitle(R.string.selected_unit);
        ListAdapterSimple adapter;
        if(type.equals("")){
            adapter = new ListAdapterSimple(getActivity(), typeUnit(), 0);
        }else{
            adapter = new ListAdapterSimple(getActivity(), unit(), 0);
        }
        list = (ListView) view.findViewById(R.id.list_item);
        list.setAdapter(adapter);

        if(type.equals("")) {
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            action.selectUnit(Application.UNIT);
                            break;
                        case 1:
                            action.selectUnit(Application.UNIT_DEFAULT);
                    }
                    dismiss();
                }
            });
        }else{
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    action.onChange(type,position);
                    dismiss();
                    type="";
                }
            });
        }
    }

    public String[] unit(){
        String[] res = new String[3];
        res[0] = "Classique";
        res[1] = getActivity().getResources().getString(R.string.universal_dividende);
        res[2] = getActivity().getResources().getString(R.string.mutual_credit);
        return res;
    }

    public String[] typeUnit(){
        String[] res = new String[2];
        res[0] = "Current unit";
        res[1] = "Default unit";
        return res;
    }
}