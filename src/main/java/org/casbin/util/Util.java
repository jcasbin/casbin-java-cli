package org.casbin.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static List<List<String>> parseNestedLists(String input) {
        String innerString = input.substring(1, input.length() - 1);
        String[] items = innerString.split("\\],\\[");
        List<List<String>> result = new ArrayList<>();
        for (String item : items) {
            String cleanItem = item.replace("[", "").replace("]", "");
            List<String> subList = Arrays.asList(cleanItem.split(","));
            result.add(subList);
        }
        return result;
    }

    public static List<String> parseOrdinary(String input) {
        String trimmedInput = input.substring(1, input.length() - 1);
        return Arrays.asList(trimmedInput.split(","));
    }

    public static String getMethodName(String methodCodes) {
        String regex = "\\b(\\w+)\\s*\\(";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(methodCodes);
        return matcher.find() ? matcher.group(1) : null;
    }

    public static String[] cutString(String str) {
        return str.replace(" ","").split(",");
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
