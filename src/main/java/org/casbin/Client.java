package org.casbin;


import org.apache.commons.cli.*;
import org.casbin.command.*;
import org.casbin.generate.DynamicClassGenerator;
import org.casbin.jcasbin.util.function.CustomFunction;
import org.casbin.util.Util;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Client {

    private static final String RBAC_COMMAND = "rbac";
    private static final String RBAC_WITH_CONDITION_COMMAND = "rbac_with_condition";
    private static final String RBAC_WITH_DOMAINS_COMMAND = "rbac_with_domains";
    private static final String ROLEMANAGER_COMMAND = "role_manager";
    private static final String MANAGEMENT_COMMAND = "management";
    private static final String ENFORCE_COMMAND = "enforce";

    private static final Map<String, AbstractCommand> COMMANDS = new HashMap<>();

    static {
        COMMANDS.put(RBAC_COMMAND, new RBACCommand());
        COMMANDS.put(RBAC_WITH_CONDITION_COMMAND, new RBACWithConditionsCommand());
        COMMANDS.put(RBAC_WITH_DOMAINS_COMMAND, new RBACWithDomainsCommand());
        COMMANDS.put(ROLEMANAGER_COMMAND, new RoleManagerCommand());
        COMMANDS.put(MANAGEMENT_COMMAND, new ManagementCommand());
        COMMANDS.put(ENFORCE_COMMAND, new EnforceCommand());
    }

    public static String run(String... args) {
        String result = "";

        try {
            if(args == null || args.length == 0) {
                printUsageMessageAndExit("");
            }

            Options options = new Options();
            Option option = new Option("m", "model", true, "the path of the model file or model text");
            options.addOption(option);
            option = new Option("p", "policy", true, "the path of the policy file or policy text");
            options.addOption(option);
            option = new Option("af", "addFunction", true, "add custom function");
            option.setRequired(false);
            options.addOption(option);

            boolean hasAddFuntion = false;
            for (String arg : args) {
                if(arg.equals("-af") || arg.equals("-addFunction")) {
                    hasAddFuntion = true;
                    break;
                }
            }

            CommandLineParser parser = new DefaultParser();

            CommandLine cmd = null;
            if(hasAddFuntion) {
                cmd = parser.parse(options, Arrays.stream(args).limit(7).toArray(String[]::new));
            } else {
                cmd = parser.parse(options, Arrays.stream(args).limit(5).toArray(String[]::new));
            }

            if(cmd.hasOption("model") && cmd.hasOption("policy")) {
                String model = cmd.getOptionValue("model");
                String policy = cmd.getOptionValue("policy");
                NewEnforcer enforcer = new NewEnforcer(model, policy);

                if (hasAddFuntion) {
                    String codes = cmd.getOptionValue("addFunction");
                    String methodName = Util.getMethodName(codes);
                    CustomFunction customFunction = DynamicClassGenerator.generateClass(methodName, codes);
                    enforcer.addFunction(methodName, customFunction);
                }

                String commandName = args[0];
                AbstractCommand command = COMMANDS.get(commandName);



                if(command != null) {
                    if(hasAddFuntion) {
                        result = command.run(enforcer, Arrays.copyOfRange(args, 7, args.length));
                    } else {
                        result = command.run(enforcer, Arrays.copyOfRange(args, 5, args.length));
                    }
//                    System.exit(0);
                } else {
                    printUsageMessageAndExit(commandName);
                }

            } else {
                new HelpCommand().run();
                System.exit(1);
            }
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

        new HelpCommand().run();
        System.exit(1);
    }

    public static void main(String[] args) throws ParseException {
        run(args);
    }
}
