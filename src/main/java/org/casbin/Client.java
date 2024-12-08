package org.casbin;

import org.apache.commons.cli.*;
import org.casbin.generate.DynamicClassGenerator;
import org.casbin.jcasbin.util.function.CustomFunction;
import org.casbin.util.DependencyHandler;
import org.casbin.util.Util;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

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
                    System.out.println("casbin-java-cli " + getGitVersion() + "\njcasbin " + getDependencyVersion("org.casbin","jcasbin"));
                }catch (Exception e) {
                    System.out.println("Failed to retrieve version information.");
                    e.printStackTrace();
                    System.out.println("Run './casbin --help or ./casbin -h' for usage.");
                }
                return result;
            }

            // processing line breaks in parameters
            String[] processedArgs = new String[args.length];
            processedArgs[0] = args[0];
            for (int i = 1; i < args.length; i++) {
                processedArgs[i] = args[i] != null ? args[i].replace("\\n", "\n") : null;
            }

            CommandLine cmd = getCmd(Arrays.copyOfRange(processedArgs, 1, processedArgs.length));
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

    /**
     * Retrieves the Git version.
     *
     * @return The latest Git tag if available; otherwise, the short hash of the latest commit.
     * @throws Exception If both the tag and commit hash cannot be retrieved.
     */
    public static String getGitVersion() throws Exception {
        String tag = executeGitCommand("git describe --tags --abbrev=0");
        if (tag == null || tag.isEmpty()) {
            String commitHash = executeGitCommand("git rev-parse --short HEAD");
            if (commitHash == null || commitHash.isEmpty()) {
                throw new RuntimeException("Failed to get Git version (Tag or Commit Hash)");
            }
            return commitHash.trim();
        }
        return tag.trim();
    }

    /**
     * Executes a Git command and retrieves its output.
     *
     * @param command The Git command to execute.
     * @return The first line of the command's output if successful; otherwise, null.
     * @throws Exception If an error occurs while running the command or reading its output.
     */
    private static String executeGitCommand(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String result = reader.readLine();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return null;
            }
            return result;
        }
    }

    /**
     * Retrieves the version of a specific dependency from the Maven POM file located in the project root directory.
     *
     * @param groupId    the groupId of the dependency to search for.
     * @param artifactId the artifactId of the dependency to search for.
     * @return the version of the specified dependency, or null if the dependency is not found.
     * @throws ParserConfigurationException if a configuration error occurs during the creation of the SAX parser.
     * @throws IOException                  if an I/O error occurs while reading the POM file.
     * @throws SAXException                 if a parsing error occurs while processing the POM file.
     */
    public static String getDependencyVersion(String groupId, String artifactId) throws ParserConfigurationException, IOException, SAXException {
        String projectRootPath = System.getProperty("user.dir");
        String pomFilePath = projectRootPath + File.separator + "pom.xml";

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        DependencyHandler handler = new DependencyHandler(groupId, artifactId);

        saxParser.parse(new File(pomFilePath), handler);

        return handler.getVersion();
    }
}
