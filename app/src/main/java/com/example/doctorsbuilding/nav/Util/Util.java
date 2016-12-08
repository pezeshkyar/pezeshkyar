package com.example.doctorsbuilding.nav.Util;

import android.app.Activity;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;

import com.example.doctorsbuilding.nav.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by hossein on 9/6/2016.
 */
public class Util{
    public static String getCurrency(int number) {
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');


        final DecimalFormat decimalFormat = new DecimalFormat("###,###,###", symbols);
        return decimalFormat.format(number);
    }

    public static String getNumber(String number) {
        number = number.replaceAll("٬", ",");
        String[] numbers = number.split(",");
        String result = "";
        for (int i = 0; i < numbers.length; i++) {
            result = result + numbers[i];
        }
        return result;
    }

    public static boolean IsValidCodeMeli(String code) {
        int sum = 0;
        int R = 0;
        int j = 2;
        int ctrl;
        char[] chars = code.toCharArray();
        if (chars.length != 10) return false;

        if (code.equals("0000000000") ||
                code.equals("1111111111") ||
                code.equals("2222222222") ||
                code.equals("3333333333") ||
                code.equals("4444444444") ||
                code.equals("5555555555") ||
                code.equals("6666666666") ||
                code.equals("7777777777") ||
                code.equals("8888888888") ||
                code.equals("9999999999"))
            return false;

        for (int i = 8; i >= 0; i--) {
            try {
                int num = Character.getNumericValue(chars[i]);
                sum = sum + (num * (j++));
            } catch (Exception ex) {
                return false;
            }
        }
        try {
            ctrl = Character.getNumericValue(chars[9]);
        } catch (Exception ex) {
            return false;
        }
        R = sum % 11;
        if (R >= 2) {
            int num = 11 - R;
            if (num != ctrl) return false;

        } else if (R != ctrl) return false;

        return true;
    }
}
