package com.example.doctorsbuilding.nav.Util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by hossein on 9/6/2016.
 */
public class Util {
    public static String getCurrency(int number) {
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');


        final DecimalFormat decimalFormat = new DecimalFormat("###,###,###", symbols);
        return decimalFormat.format(number);
    }
    public static String getNumber(String number){
        number = number.replaceAll("٬", ",");
        String[] numbers = number.split(",");
        String result = "";
        for(int i=0;i<numbers.length;i++){
            result = result+numbers[i];
        }
        return result;
    }
}
