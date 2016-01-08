package io.ucoin.app.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.ucoin.app.Application;
import io.ucoin.app.R;

/**
 * Created by naivalf27 on 08/12/15.
 */
public class Format {

    public static final int YEAR = 0;
    public static final int DAY = 1;
    public static final int HOUR = 2;
    public static final int MINUTE = 3;
    public static final int SECOND = 4;
    public static final int MILLI_SECOND = 5;

    public static final int IN_YEAR = 31557600;
    public static final int IN_DAY = 86400;
    public static final int IN_HOUR = 3600;
    public static final int IN_MINUTE = 60;

    public static final String CONTACT_PATH = "ucoin://";
    public static final String SEPARATOR1 = ":";
    public static final String SEPARATOR2 = "@";

    public static final String UID = "uid";
    public static final String PUBLICKEY = "public_key";
    public static final String CURRENCY = "currency";

    public static final boolean SIMPLE = true;
    public static final boolean LONG = false;

    public static String timeFormatter(Context context, double amount){
        String result = "";
        long tmp = 31557600;

        long year = (long) (amount / IN_YEAR);
        long day = (long) ((amount / IN_DAY) - (year * 365.25));
        long hour = (long) ((amount / IN_HOUR) - (day * 24) - (year * 8766));
        long minute = (long) ((amount / IN_MINUTE) - (hour * 60) - (day * 1440) - (year * 525960));
        long second = (long) (amount - (minute * 60) - (hour * 3600) - (day * 86400) - (year * IN_YEAR));
        long msecond1 = (long) ((amount * 1000) - (second * 1000) - (minute * 60000) - (hour * 3600000));
        long msecond2 = (day * 86400000) - (year * (tmp * 1000));
        long msecond = msecond1 - msecond2;


        if (year > 0) {
            result += formatWithSmartDecimal(year) + context.getResources().getString(R.string.year) + "  ";
            if (day > 0) result += formatWithSmartDecimal(day) + context.getResources().getString(R.string.day);
        }else {
            if (day > 0) result += formatWithSmartDecimal(day) + context.getResources().getString(R.string.day)+"  ";
            if (hour > 0 || day > 0) result += formatWithSmartDecimal(hour) + "h  ";
            if (minute > 0 || hour > 0 || day > 0) result += formatWithSmartDecimal(minute) + "min  ";
            if (second > 0 && minute >= 0 && day == 0 && hour == 0) result += formatWithSmartDecimal(second) + "s";
            if (msecond >= 1 && second == 0 && day == 0 && hour == 0 && minute == 0)
                result += formatWithSmartDecimal(msecond) + "ms";
            if (msecond < 1 && second == 0 && day == 0 && hour == 0 && minute == 0)
                result = "<1 ms";
            if (msecond ==0 && second == 0 && day == 0 && hour == 0 && minute == 0)
                result = "0 ms";
        }


        return result;
    }

    public static Double toYear(Double second){
        return second / IN_YEAR;
    }
    public static Double toDay(Double second){
        return second / IN_DAY;
    }

    public static Double toHour(Double second){
        return second / IN_HOUR;
    }

    public static Double toMinute(Double second){
        return second / IN_MINUTE;
    }

    public static Double toSecond(Double val,int unit){
        switch (unit){
            case YEAR:
                val *= IN_YEAR;
                break;
            case DAY:
                val *= IN_DAY;
                break;
            case HOUR:
                val *= IN_HOUR;
                break;
            case MINUTE:
                val *= IN_MINUTE;
                break;
            case MILLI_SECOND:
                val /= 1000;
                break;
        }
        return val;
    }

    public static Double toMilliSecond(Double second){
        return second * 1000;
    }

    public static String formatWithSmartDecimal(long amount) {
        if (amount < 0) {
            return "- " + formatWithSmartDecimal(-amount);
        }
        DecimalFormat currencyFormatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.getDefault());
        String resultPartToIgnore = currencyFormatter.getDecimalFormatSymbols().getDecimalSeparator() + "00";

        //String result = currencyFormatter.format(amount);
        String result = String.valueOf(amount);
        return result.replace(resultPartToIgnore, "");
    }


    public static void changeUnit(
            final Context context,
            final Double classiqueValue,
            final Double duValue,
            final Double timeValue,
            final SharedPreferences preferences,
            final TextView currentAmount,
            final TextView defaultAmount,
            final String dir){

        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                changeUnit(context,classiqueValue,duValue,timeValue,preferences,currentAmount,defaultAmount,dir);
            }
        });

        int unit = preferences.getInt(Application.UNIT,Application.UNIT_CLASSIC);
        int defaultUnit = preferences.getInt(Application.UNIT_DEFAULT,Application.UNIT_CLASSIC);
        DecimalFormat formatter = new DecimalFormat("#,###");

        switch (unit){
            case Application.UNIT_CLASSIC:
                currentAmount.setText(dir.concat(formatter.format(classiqueValue)));
                break;
            case Application.UNIT_DU:
                currentAmount.setText(dir.concat(String.format("%.8f", duValue))
                        .concat(" ").concat(context.getResources().getString(R.string.UD)));
                break;
            case Application.UNIT_TIME:
                if (dir.equals("")) {
                    currentAmount.setText(Format.timeFormatter(context, timeValue));
                }else{
                    currentAmount.setText(dir.concat("(").concat(Format.timeFormatter(context, timeValue)).concat(")"));
                }
                break;
        }
        if(defaultUnit == unit){
            defaultAmount.setVisibility(View.GONE);
        }else{
            defaultAmount.setVisibility(View.VISIBLE);
            switch (defaultUnit){
                case Application.UNIT_CLASSIC:
                    defaultAmount.setText(formatter.format(classiqueValue));
                    break;
                case Application.UNIT_DU:
                    defaultAmount.setText(String.format("%.8f", duValue)
                            .concat(" ").concat(context.getResources().getString(R.string.UD)));
                    break;
                case Application.UNIT_TIME:
                    defaultAmount.setText(Format.timeFormatter(context, timeValue));
                    break;
            }
        }
    }

    public static String minifyPubkey(String pubkey) {
        return (pubkey == null || pubkey.length() < 6)? pubkey : pubkey.substring(0, 6);
    }

    public static String createUri(boolean simple, String uid, String publicKey, String currency) {
        String result;
        if(simple){
            result = publicKey;
        }else {
            result = CONTACT_PATH;
            if (uid.isEmpty() || uid.equals(" ")) {
                result = result.concat(SEPARATOR1);
            } else {
                result = result.concat(uid).concat(SEPARATOR1);
            }

            if (publicKey.isEmpty() || publicKey.equals(" ")) {
                result = result.concat(SEPARATOR2);
            } else {
                result = result.concat(publicKey).concat(SEPARATOR2);
            }

            if (!currency.isEmpty() && !currency.equals(" ")) {
                result = result.concat(currency);
            }
        }
        return result;
    }

    public static Map<String, String> parseUri(String uri){
        Map<String, String> result = new HashMap<>();
        if(uri.substring(0,CONTACT_PATH.length()).equals(CONTACT_PATH)){
            int index1 = uri.indexOf(SEPARATOR1,CONTACT_PATH.length());
            int index2 = uri.indexOf(SEPARATOR2,index1+SEPARATOR1.length());

            String uid = uri.substring(CONTACT_PATH.length(), index1);
            String publicKey = uri.substring(index1+SEPARATOR1.length(),index2);
            String currency = uri.substring(index2+SEPARATOR2.length());

            if(!uid.isEmpty()){
                result.put(UID,uid);
            }
            if(!publicKey.isEmpty()){
                result.put(PUBLICKEY,publicKey);
            }
            if(!currency.isEmpty()){
                result.put(CURRENCY,currency);
            }
        }else{
            result.put(PUBLICKEY,uri);
        }
        return result;
    }

    public static String isNull(String txt){
        return (txt==null || txt.isEmpty()) ? "" : txt;
    }
}
