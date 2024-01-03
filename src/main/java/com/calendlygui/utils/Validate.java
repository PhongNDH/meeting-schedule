package com.calendlygui.utils;

import com.calendlygui.constant.ConstantValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate {
    public static boolean checkEmailFormat(String email) {
        Matcher isEmail = Pattern.compile(ConstantValue.EMAIL_PATTERN).matcher(email);
        return isEmail.matches();
    }

    public static boolean checkName(String name){
        return name.matches("[\\p{L} ]+"); // contain only letters and space with diacritics
    }
}
