package org.casbin;

import org.casbin.jcasbin.main.Enforcer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class NewEnforcer extends Enforcer {

    public NewEnforcer(String modelPath, String policyFile) {
        super(parse(modelPath, ".conf"), parse(policyFile, ".csv"));
    }

    public static String parse(String string, String suffix) {
        boolean isFile = string.endsWith(suffix);
        return isFile ? string : writeToTempFile(string, suffix);
    }

    public static String writeToTempFile(String str, String suffix) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("default", suffix);
            tempFile.deleteOnExit();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                writer.write(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile.getAbsolutePath();
    }
}