package io.ucoin.app.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import io.ucoin.app.Application;
import io.ucoin.app.R;
import io.ucoin.app.service.Format;

/**
 * Created by naivalf27 on 26/10/15.
 */
public class ConverterDialog extends DialogFragment{



    private int timeSelected = Format.MINUTE;
    private int lastTimeSelected = Format.MINUTE;

    private EditText txt_coin, txt_du, mAmount;

    private EditText txt_time;

    private long mUd;

    private int delay, unit;

    private TextWatcher for_coin, for_du;
    private TextWatcher for_time;
    private Spinner list_Unit_time;
    private Spinner mSpinner;
    private TextView time_converted;

    public ConverterDialog(long mUd, int delay, EditText mAmount, Spinner spinner) {
        this.mUd = mUd;
        this.delay = delay;
        this.mAmount = mAmount;
        this.mSpinner = spinner;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        unit = preferences.getInt(Application.UNIT,Application.UNIT_CLASSIC);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_converter, null);
        builder.setView(view);
        builder.setTitle(getString(R.string.converter));

        txt_coin = (EditText) view.findViewById(R.id.txt_coin);
        txt_du = (EditText) view.findViewById(R.id.txt_du);
        txt_time = (EditText) view.findViewById(R.id.txt_time);



        list_Unit_time = (Spinner) view.findViewById(R.id.list_Unit_time);
        List list = Arrays.asList(getResources().getStringArray(R.array.list_unit_time));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        list_Unit_time.setAdapter(dataAdapter);
        list_Unit_time.setSelection(Format.MINUTE);
        list_Unit_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lastTimeSelected = timeSelected;
                timeSelected = position;
                String val = txt_time.getText().toString();
                if(val.equals("") || val.equals(".") || val.equals(" ")) {
                    val ="0";
                }
                if(val.substring(0,1).equals(".")){
                    val = "0"+val;
                }
                majTime(val,false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        time_converted = (TextView) view.findViewById(R.id.time_converted);

        viewCreated();

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                enterValueInFragment();
                dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        });

        view.clearFocus();
        return builder.create();
    }

    private void majTime(String textview,boolean is_second){
        Double val = Double.parseDouble(textview);
        if(!is_second) {
            val = Format.toSecond(val, timeSelected);
            long coin = timeToCoin(val);
            removeTextWatcher();
            time_converted.setText(Format.timeFormatter(getActivity(), val));
            txt_coin.setText(String.valueOf(coin));
            txt_du.setText(String.valueOf(coinToDu(coin)));
            addTextWatcher();
        }else{
            time_converted.setText(Format.timeFormatter(getActivity(), val));
            val = convertTime(val);
            txt_time.setText(String.valueOf(val));
        }
    }

    private Double convertTime(Double val){
        switch (timeSelected){
            case Format.YEAR:
                val = Format.toYear(val);
                break;
            case Format.DAY:
                val = Format.toDay(val);
                break;
            case Format.HOUR:
                val = Format.toHour(val);
                break;
            case Format.MINUTE:
                val = Format.toMinute(val);
                break;
            case Format.MILLI_SECOND:
                val = Format.toMilliSecond(val);
                break;
        }
        return val;
    }

    private void viewCreated(){
        creatTextWatcher();

        addTextWatcher();

        String val = mAmount.getText().toString();

        if(val.equals("") || val.equals(".") || val.equals(" ")) {
            val ="0";
        }
        if(val.substring(0,1).equals(".")){
            val = "0"+val;
        }
        switch (unit) {
            case Application.UNIT_CLASSIC:
                txt_coin.setText(val);
                break;
            case Application.UNIT_DU:
                txt_du.setText(val);
                break;
            case Application.UNIT_TIME:
                list_Unit_time.setSelection(mSpinner.getSelectedItemPosition());
                txt_time.setText(val);
                break;
        }
    }

    private void removeTextWatcher(){
        txt_coin.removeTextChangedListener(for_coin);
        txt_du.removeTextChangedListener(for_du);
        txt_time.removeTextChangedListener(for_time);
    }

    private void addTextWatcher(){
        txt_coin.addTextChangedListener(for_coin);
        txt_du.addTextChangedListener(for_du);
        txt_time.addTextChangedListener(for_time);
    }

    public void creatTextWatcher(){
        for_coin = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String val = txt_coin.getText().toString();
                if(val.equals("") || val.equals(".") || val.equals(" ")) {
                    val ="0";
                }
                if(val.substring(0,1).equals(".")){
                    val = "0"+val;
                }
                removeTextWatcher();
                txt_du.setText(String.valueOf(coinToDu(Long.parseLong(val))));
                majTime(String.valueOf(coinToTime(Long.parseLong(val))),true);
                addTextWatcher();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        for_du = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String val = txt_du.getText().toString();
                if(val.equals("") || val.equals(".") || val.equals(" ")) {
                    val ="0";
                }
                if(val.substring(0,1).equals(".")){
                    val = "0"+val;
                }
                long coin = duToCoin(Double.parseDouble(val));
                removeTextWatcher();
                txt_coin.setText(String.valueOf(coin));
                majTime(String.valueOf(coinToTime(coin)),true);
                addTextWatcher();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        for_time = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String val = txt_time.getText().toString();
                if(val.equals("") || val.equals(".") || val.equals(" ")) {
                    val ="0";
                }
                if(val.substring(0,1).equals(".")){
                    val = "0"+val;
                }
                majTime(val,false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private long timeToCoin(Double time){

        return Double.valueOf(time*mUd/delay).longValue();
    }

    private long duToCoin(Double du){
        return Double.valueOf(du*mUd).longValue();
    }

    private Double coinToDu(long coin){
        return (double)coin/mUd;
    }

    private Double coinToTime(long coin){
        Double result = (double)coin*delay/mUd;
        return result;
    }


    private void enterValueInFragment(){
        switch (unit){
            case Application.UNIT_CLASSIC:
                mAmount.setText(txt_coin.getText().toString());
                break;
            case Application.UNIT_DU:
                mAmount.setText(txt_du.getText().toString());
                break;
            case Application.UNIT_TIME:
                mAmount.setText(txt_time.getText());
                mSpinner.setSelection(list_Unit_time.getSelectedItemPosition());
                break;
        }
    }
}