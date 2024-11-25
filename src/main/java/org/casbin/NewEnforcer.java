package org.casbin;

import org.casbin.jcasbin.main.Enforcer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class NewEnforcer extends Enforcer {

    private static final Pattern MODEL_SECTION_PATTERN = Pattern.compile(
        "\\[request_definition\\].*?" +
        "\\[policy_definition\\].*?" +
        "\\[policy_effect\\].*?" +
        "\\[matchers\\]",
        Pattern.DOTALL
    );

    private static final Pattern POLICY_LINE_PATTERN = Pattern.compile(
        "^\\s*(p|g),.*",
        Pattern.MULTILINE
    );

    public NewEnforcer(String modelPath, String policyFile) {
        super(parse(modelPath, true), parse(policyFile, false));
    }

    public static String parse(String input, boolean isModel) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }

        // Check if input is an existing file
        File file = new File(input);
        if (file.exists() && file.isFile()) {
            return input;
        }

        // If not a file, validate content format
        if (isModel) {
            if (!isValidModelContent(input)) {
                throw new IllegalArgumentException("Invalid model format. Model must contain required sections: [request_definition], [policy_definition], [policy_effect], and [matchers]");
            }
        } else {
            if (!input.trim().isEmpty() && !isValidPolicyContent(input)) {
                throw new IllegalArgumentException("Invalid policy format. Policy must contain lines starting with 'p,' or 'g,' or be empty");
            }
        }

        // If content is valid, write to temp file
        return writeToTempFile(input);
    }

    private static boolean isValidModelContent(String content) {
        return MODEL_SECTION_PATTERN.matcher(content).find();
    }

    private static boolean isValidPolicyContent(String content) {
        return content.trim().isEmpty() || POLICY_LINE_PATTERN.matcher(content).find();
    }

    public static String writeToTempFile(String content) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("casbin_temp_", "");
            tempFile.deleteOnExit();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                writer.write(content);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creating temporary file", e);
        }
        return tempFile.getAbsolutePath();
    }
}