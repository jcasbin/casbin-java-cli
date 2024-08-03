package org.casbin;

import org.apache.commons.cli.*;
import org.casbin.jcasbin.exception.CasbinConfigException;
import org.casbin.jcasbin.main.Enforcer;

public class Client {
    private static void configureOptions(Options options) {
        Option[] cliOptions = {
                addOption("m", "model", true, "the path of the model file"),
                addOption("p", "policy", true, "the path of the policy file"),
                addOption("e", "enforce", true, "enforce"),
                addOption("ex", "enforceEx", true, "enforceEx"),
                addOption("ap", "addPolicy", true, "Add a policy rule to the storage"),
                addOption("rp", "removePolicy", true, "Remove a policy rule from the storage")
        };
        for (Option option : cliOptions) {
            options.addOption(option);
        }
    }
    private static Option addOption(String shortOpt, String longOpt, boolean hasArg, String description) {
        return new Option(shortOpt, longOpt, hasArg, description);
    }

    public static Object run(String[] args) throws ParseException {
        Options options = new Options();
        configureOptions(options);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String modelPath = cmd.getOptionValue("model");
        String policyPath = cmd.getOptionValue("policy");
        Enforcer enforcer = null;
        try {
            enforcer = new Enforcer(modelPath, policyPath);
        } catch (CasbinConfigException ex) {
            ex.printStackTrace();
        }

        if(cmd.hasOption("enforce")) {
            String enforceArgs = cmd.getOptionValue("enforce").replace(" ","");
            boolean result = enforcer.enforce(enforceArgs.split(","));
            System.out.println(result ? "Allow" : "Ban");
            return result;
        } else if (cmd.hasOption("enforceEx")) {
            String enforceArgs = cmd.getOptionValue("enforceEx").replace(" ","");
            boolean result = enforcer.enforceEx(enforceArgs.split(",")).isAllow();
            System.out.println(result ? "Allow" : "Ban");
            return result;
        }else if (cmd.hasOption("addPolicy")){
            String policyArgs = cmd.getOptionValue("addPolicy").replace(" ","");
            boolean result = enforcer.addPolicy(policyArgs.split(","));
            System.out.println(result ? "Add Success" : "Add Failed");
            enforcer.savePolicy();
            return result;
        }else if (cmd.hasOption("removePolicy")){
            String policyArgs = cmd.getOptionValue("removePolicy").replace(" ","");
            boolean result = enforcer.removePolicy(policyArgs.split(","));
            System.out.println(result ? "Remove Success" : "Remove Failed");
            enforcer.savePolicy();
            return result;
        }else {
            System.out.println("Command Error");
            return null;
        }
    }

    public static void main(String[] args) throws ParseException {
        Client cli = new Client();
        Object run = cli.run(args);
        System.out.println(run);
    }
}
