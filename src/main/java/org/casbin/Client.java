package org.casbin;


import org.apache.commons.cli.*;
import org.casbin.generate.DynamicClassGenerator;
import org.casbin.jcasbin.util.function.CustomFunction;
import org.casbin.util.Util;

import java.util.*;


public class Client {

    public static String run(String... args) {
        String result = "";

        try {
            if(args == null || args.length == 0) {
                printUsageMessageAndExit("");
            }

            String commandName = args[0];

            CommandLine cmd = getCmd(Arrays.copyOfRange(args, 1, args.length));
            String model = cmd.getOptionValue("model");
            String policy = cmd.getOptionValue("policy");
            NewEnforcer enforcer = new NewEnforcer(model, policy);


            if(cmd.hasOption("AF")) {
                String codes = cmd.getOptionValue("AF");
                String methodName = Util.getMethodName(codes);
                CustomFunction customFunction = DynamicClassGenerator.generateClass(methodName, codes);
                enforcer.addFunction(methodName, customFunction);
            }
            CommandExecutor commandExecutor = new CommandExecutor(enforcer, commandName, cmd.getArgs());
            Object o = commandExecutor.outputResult();
            System.out.println(o);
            return o.toString();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return result;
    }


    private static void printUsageMessageAndExit(String commandName) throws Exception {
        if (commandName.isEmpty()) {
            System.out.println("Error: " + commandName + " not recognised");
        }
//        new HelpCommand().run();
        System.exit(1);
    }

    public static void main(String[] args) throws ParseException {
        run(args);
    }

    private static CommandLine getCmd(String[] args) throws ParseException {
        Options options = new Options();

        Option option = new Option("AF", "AddFunction", true, "add function");
        option.setArgs(1);
        option.setRequired(false);
        options.addOption(option);

        option = new Option("m", "model", true, "the path of the model file or model text");
        option.hasArg();
        options.addOption(option);

        option = new Option("p", "policy", true, "the path of the policy file or policy text");
        option.hasArg();
        options.addOption(option);

        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }
}
