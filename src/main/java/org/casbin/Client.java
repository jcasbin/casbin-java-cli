package org.casbin;


import org.apache.commons.cli.*;
import org.casbin.generate.DynamicClassGenerator;
import org.casbin.jcasbin.util.function.CustomFunction;
import org.casbin.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;


public class Client {

    public static String run(String... args) {
        String result = "";

        try {
            if(args == null || args.length == 0) {
                printUsageMessageAndExit("");
            }

            String commandName = args[0];
            if(Objects.equals(commandName, "-h") || Objects.equals(commandName, "--help")){
                printHelpMessage();
                return result;
            } else if(Objects.equals(commandName, "-v") || Objects.equals(commandName, "--version")){
                try{
                    System.out.println("casbin-java-cli " + getVersion());
                }catch (IOException e) {
                    System.out.println("Failed to retrieve version information.");
                    e.printStackTrace();
                    System.out.println("Run './casbin --help or ./casbin -h' for usage.");
                }
                return result;
            }

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
            System.out.println("Run './casbin --help or ./casbin -h' for usage.");
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

    private static void printHelpMessage() {
        System.out.println(" Usage: ./casbin [Method] [options] [args]\n" +
                "\n" +
                "    Casbin is a powerful and efficient open-source access control library.\n" +
                "    It provides support for enforcing authorization based on various access control models.\n" +
                "\n" +
                "    Method:\n" +
                "      enforce       Test if a 'subject' can access an 'object' with a given 'action' based on the policy\n" +
                "      enforceEx     Check permissions and get which policy it matches\n" +
                "      addFunction   Add custom function\n" +
                "      addPolicy     Add a policy rule to the policy file\n" +
                "      removePolicy  Remove a policy rule from the policy file\n" +
                "    For more method, visit https://github.com/casbin/jcasbin\n" +
                "\n" +
                "    Options:\n" +
                "      -m, --model <model>          The path of the model file or model text. Please wrap it with \"\" and separate each line with \"|\"\n" +
                "      -p, --policy <policy>        The path of the policy file or policy text. Please wrap it with \"\" and separate each line with \"|\"\n" +
                "      -AF, --addFunction <functionDefinition>       Add custom function. Please wrap it with \"\" and separate each line with \"|\"\n" +
                "      -v, --version                The version of casbin-java-cli" +
                "\n" +
                "    args:\n" +
                "      Parameters required for the method\n" +
                "\n" +
                "    Examples:\n" +
                "      ./casbin enforce -m \"examples/rbac_model.conf\" -p \"examples/rbac_policy.csv\" \"alice\" \"data1\" \"read\"\n" +
                "      ./casbin enforceEx -m \"examples/rbac_model.conf\" -p \"examples/rbac_policy.csv\" \"alice\" \"data1\" \"read\"\n" +
                "      ./casbin addPolicy -m \"examples/rbac_model.conf\" -p \"examples/rbac_policy.csv\" \"alice\" \"data2\" \"write\"\n" +
                "      ./casbin enforce -m  \"your_model.conf\" -p \"examples/keymatch_policy.csv\" -AF \"yourFunctionDefinition\" \"alice\" \"/alice_data/resource1\" \"GET\"\n" +
                "\n" +
                "    For more information, visit https://github.com/casbin/casbin");

    }

    /***
     * Retrieves the version of the project.
     * @return The version of the project, or "Version not found" if the version is not found in the file.
     * @throws Exception If an error occurs while reading the file or loading the properties.
     */
    private static String getVersion() throws Exception {
        String classesPath = Client.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();
        String targetPath = new File(classesPath).getParentFile().getCanonicalPath();
        String propertiesPath = targetPath + File.separator + "maven-archiver" + File.separator + "pom.properties";
        FileInputStream fileInputStream = new FileInputStream(propertiesPath);

        Properties properties = new Properties();
        properties.load(fileInputStream);
        String version = properties.getProperty("version");

        return version != null ? version : "Version not found";
    }
}
