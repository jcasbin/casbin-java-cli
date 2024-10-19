package org.casbin.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static String getMethodName(String methodCodes) {
        String regex = "\\b(\\w+)\\s*\\(";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(methodCodes);
        return matcher.find() ? matcher.group(1) : null;
    }

    public static int getArgsNum(String methodCodes) {
        String regex = "\\(([^)]*)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(methodCodes);
        if (matcher.find()) {
            String args = matcher.group(1);
            String[] argList = args.split(",");
            return argList.length;
        }
        return 0;
    }
}
