package org.casbin.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    /**
     * Parse the input string to get the function definitions List
     * @param input
     * @return List of function definitions
     */
    public static List<String> parse(String input) throws IOException {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        List<String> codes = new ArrayList<>();

        // Check if input is an existing file
        File file = new File(input);
        if (file.exists() && file.isFile()) {
            String content =  new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

            String[] lines = content.split("\\[function_definition\\]");
            for (int i = 1; i < lines.length; i++) {
                codes.add(lines[i].trim());
            }
        } else {
            codes.add(input);
        }
        return codes;
    }
}
