package org.casbin;

import org.casbin.jcasbin.main.Enforcer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewEnforcer extends Enforcer {

    public NewEnforcer(String modelPath, String policyFile) {
        super(parse(modelPath, ".conf"), parse(policyFile, ".csv"));
    }

    public static String parse(String string, String suffix) {
        boolean isFile = string.endsWith(suffix);
        if(suffix.equals(".conf")) {
            if(isFile) {
                try {
                    simpleCheck(new String(Files.readAllBytes(Paths.get(string)), StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                simpleCheck(string);
            }
        }
        return isFile ? string : writeToTempFile(string, suffix);
    }

    public static String writeToTempFile(String str, String suffix) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("default", suffix);
            tempFile.deleteOnExit();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                writer.write(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile.getAbsolutePath();
    }

    private static void simpleCheck(String fileString) {
        fileString = fileString.replace(" ","");
        String[] requiredSubstrings = {"[request_definition]", "[policy_definition]", "[policy_effect]", "[matchers]", "r=", "p=", "e=", "m="};
        List<String> missingSubstrings = new ArrayList<>();

        for (String substring : requiredSubstrings) {
            Pattern pattern = Pattern.compile(Pattern.quote(substring));
            Matcher matcher = pattern.matcher(fileString);
            if (!matcher.find()) {
                missingSubstrings.add(substring);
            }
        }

        if(!missingSubstrings.isEmpty()) {
            throw new RuntimeException("missing required sections: " + String.join(", ", missingSubstrings));
        }
    }
}