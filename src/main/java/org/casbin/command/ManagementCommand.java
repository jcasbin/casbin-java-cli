package org.casbin.command;

import org.apache.commons.cli.*;
import org.casbin.NewEnforcer;

import java.util.HashMap;

import java.util.Map;

import static org.casbin.util.Util.*;

public class ManagementCommand extends AbstractCommand{

    private static final String ENFORCE = "enforce";
    private static final String ENFORCE_WITH_MATCHER = "enforceWithMatcher";
    private static final String ENFORCE_EX = "enforceEx";
    private static final String ENFORCE_EX_WITH_MATCHER = "enforceExWithMatcher";
    private static final String BATCH_ENFORCE = "batchEnforce";
    private static final String GET_ALL_SUBJECTS = "getAllSubjects";
    private static final String GET_ALL_NAMED_SUBJECTS = "getAllNamedSubjects";
    private static final String GET_ALL_OBJECTS = "getAllObjects";
    private static final String GET_ALL_NAMED_OBJECTS = "getAllNamedObjects";
    private static final String GET_ALL_ACTIONS = "getAllActions";
    private static final String GET_ALL_NAMED_ACTIONS = "getAllNamedActions";
    private static final String GET_ALL_ROLES = "getAllRoles";
    private static final String GET_ALL_NAMED_ROLES = "getAllNamedRoles";
    private static final String GET_POLICY = "getPolicy";
    private static final String GET_FILTERED_POLICY = "getFilteredPolicy";
    private static final String GET_NAMED_POLICY = "getNamedPolicy";
    private static final String GET_FILTERED_NAMED_POLICY = "getFilteredNamedPolicy";
    private static final String GET_GROUPING_POLICY = "getGroupingPolicy";
    private static final String GET_FILTERED_GROUPING_POLICY = "getFilteredGroupingPolicy";
    private static final String GET_NAMED_GROUPING_POLICY = "getNamedGroupingPolicy";
    private static final String GET_FILTERED_NAMED_GROUPING_POLICY = "getFilteredNamedGroupingPolicy";
    private static final String HAS_POLICY = "hasPolicy";
    private static final String HAS_NAMED_POLICY = "hasNamedPolicy";
    private static final String ADD_POLICY = "addPolicy";
    private static final String ADD_POLICIES = "addPolicies";
    private static final String ADD_NAMED_POLICY = "addNamedPolicy";
    private static final String ADD_NAMED_POLICIES = "addNamedPolicies";
    private static final String REMOVE_POLICY = "removePolicy";
    private static final String REMOVE_POLICIES = "removePolicies";
    private static final String REMOVE_FILTERED_POLICY = "removeFilteredPolicy";
    private static final String REMOVE_NAMED_POLICY = "removeNamedPolicy";
    private static final String REMOVE_NAMED_POLICIES = "removeNamedPolicies";
    private static final String REMOVE_FILTERED_NAMED_POLICY = "removeFilteredNamedPolicy";
    private static final String HAS_GROUPING_POLICY = "hasGroupingPolicy";
    private static final String HAS_NAMED_GROUPING_POLICY = "hasNamedGroupingPolicy";
    private static final String ADD_GROUPING_POLICY = "addGroupingPolicy";
    private static final String ADD_GROUPING_POLICIES = "addGroupingPolicies";
    private static final String ADD_NAMED_GROUPING_POLICY = "addNamedGroupingPolicy";
    private static final String ADD_NAMED_GROUPING_POLICIES = "addNamedGroupingPolicies";
    private static final String REMOVE_GROUPING_POLICY = "removeGroupingPolicy";
    private static final String REMOVE_GROUPING_POLICIES = "removeGroupingPolicies";
    private static final String REMOVE_FILTERED_GROUPING_POLICY = "removeFilteredGroupingPolicy";
    private static final String REMOVE_NAMED_GROUPING_POLICY = "removeNamedGroupingPolicy";
    private static final String REMOVE_FILTERED_NAMED_GROUPING_POLICY = "removeFilteredNamedGroupingPolicy";
    private static final String UPDATE_POLICY = "updatePolicy";
    private static final String LOAD_FILTERED_POLICY = "loadFilteredPolicy";
    private static final String UPDATE_GROUPING_POLICY = "updateGroupingPolicy";
    private static final String UPDATE_NAMED_GROUPING_POLICY = "updateNamedGroupingPolicy";

    @Override
    public String run(NewEnforcer enforcer, String... args) throws Exception {
        Options options = getOptions();

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        Map<String, OperationHandle> handlers = getStringOperationHandleMap(enforcer);

        try {
            CommandLine cmd = parser.parse(options, args);

            String option = cmd.hasOption(ENFORCE) ? ENFORCE :
                                cmd.hasOption(ENFORCE_WITH_MATCHER) ? ENFORCE_WITH_MATCHER :
                                cmd.hasOption(ENFORCE_EX) ? ENFORCE_EX :
                                cmd.hasOption(ENFORCE_EX_WITH_MATCHER) ? ENFORCE_EX_WITH_MATCHER :
                                cmd.hasOption(BATCH_ENFORCE) ? BATCH_ENFORCE :
                                cmd.hasOption(GET_ALL_SUBJECTS) ? GET_ALL_SUBJECTS :
                                cmd.hasOption(GET_ALL_NAMED_SUBJECTS) ? GET_ALL_NAMED_SUBJECTS :
                                cmd.hasOption(GET_ALL_OBJECTS) ? GET_ALL_OBJECTS :
                                cmd.hasOption(GET_ALL_NAMED_OBJECTS) ? GET_ALL_NAMED_OBJECTS :
                                cmd.hasOption(GET_ALL_ACTIONS) ? GET_ALL_ACTIONS :
                                cmd.hasOption(GET_ALL_NAMED_ACTIONS) ? GET_ALL_NAMED_ACTIONS :
                                cmd.hasOption(GET_ALL_ROLES) ? GET_ALL_ROLES :
                                cmd.hasOption(GET_ALL_NAMED_ROLES) ? GET_ALL_NAMED_ROLES :
                                cmd.hasOption(GET_POLICY) ? GET_POLICY :
                                cmd.hasOption(GET_FILTERED_POLICY) ? GET_FILTERED_POLICY :
                                cmd.hasOption(GET_NAMED_POLICY) ? GET_NAMED_POLICY :
                                cmd.hasOption(GET_FILTERED_NAMED_POLICY) ? GET_FILTERED_NAMED_POLICY :
                                cmd.hasOption(GET_GROUPING_POLICY) ? GET_GROUPING_POLICY :
                                cmd.hasOption(GET_FILTERED_GROUPING_POLICY) ? GET_FILTERED_GROUPING_POLICY :
                                cmd.hasOption(GET_NAMED_GROUPING_POLICY) ? GET_NAMED_GROUPING_POLICY :
                                cmd.hasOption(GET_FILTERED_NAMED_GROUPING_POLICY) ? GET_FILTERED_NAMED_GROUPING_POLICY :
                                cmd.hasOption(HAS_POLICY) ? HAS_POLICY :
                                cmd.hasOption(HAS_NAMED_POLICY) ? HAS_NAMED_POLICY :
                                cmd.hasOption(ADD_POLICY) ? ADD_POLICY :
                                cmd.hasOption(ADD_POLICIES) ? ADD_POLICIES :
                                cmd.hasOption(ADD_NAMED_POLICY) ? ADD_NAMED_POLICY :
                                cmd.hasOption(ADD_NAMED_POLICIES) ? ADD_NAMED_POLICIES :
                                cmd.hasOption(REMOVE_POLICY) ? REMOVE_POLICY :
                                cmd.hasOption(REMOVE_POLICIES) ? REMOVE_POLICIES :
                                cmd.hasOption(REMOVE_FILTERED_POLICY) ? REMOVE_FILTERED_POLICY :
                                cmd.hasOption(REMOVE_NAMED_POLICY) ? REMOVE_NAMED_POLICY :
                                cmd.hasOption(REMOVE_NAMED_POLICIES) ? REMOVE_NAMED_POLICIES :
                                cmd.hasOption(REMOVE_FILTERED_NAMED_POLICY) ? REMOVE_FILTERED_NAMED_POLICY :
                                cmd.hasOption(HAS_GROUPING_POLICY) ? HAS_GROUPING_POLICY :
                                cmd.hasOption(HAS_NAMED_GROUPING_POLICY) ? HAS_NAMED_GROUPING_POLICY :
                                cmd.hasOption(ADD_GROUPING_POLICY) ? ADD_GROUPING_POLICY :
                                cmd.hasOption(ADD_GROUPING_POLICIES) ? ADD_GROUPING_POLICIES :
                                cmd.hasOption(ADD_NAMED_GROUPING_POLICY) ? ADD_NAMED_GROUPING_POLICY :
                                cmd.hasOption(ADD_NAMED_GROUPING_POLICIES) ? ADD_NAMED_GROUPING_POLICIES :
                                cmd.hasOption(REMOVE_GROUPING_POLICY) ? REMOVE_GROUPING_POLICY :
                                cmd.hasOption(REMOVE_GROUPING_POLICIES) ? REMOVE_GROUPING_POLICIES :
                                cmd.hasOption(REMOVE_FILTERED_GROUPING_POLICY) ? REMOVE_FILTERED_GROUPING_POLICY :
                                cmd.hasOption(REMOVE_NAMED_GROUPING_POLICY) ? REMOVE_NAMED_GROUPING_POLICY :
                                cmd.hasOption(REMOVE_FILTERED_NAMED_GROUPING_POLICY) ? REMOVE_FILTERED_NAMED_GROUPING_POLICY :
                                cmd.hasOption(UPDATE_POLICY) ? UPDATE_POLICY :
                                cmd.hasOption(LOAD_FILTERED_POLICY) ? LOAD_FILTERED_POLICY :
                                cmd.hasOption(UPDATE_GROUPING_POLICY) ? UPDATE_GROUPING_POLICY :
                                cmd.hasOption(UPDATE_NAMED_GROUPING_POLICY) ? UPDATE_NAMED_GROUPING_POLICY : null;
            OperationHandle handle = handlers.get(option);
            String[] params = cmd.getOptionValues(option);
            String res = handle.handle(params);
            enforcer.savePolicy();
            System.out.println(res);
            return res;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            formatter.printHelp("management", options);
        }
        return "";
    }

    private static Map<String, OperationHandle> getStringOperationHandleMap(NewEnforcer enforcer) {
        Map<String, OperationHandle> handlers = new HashMap<>();

        handlers.put(ENFORCE, params -> String.valueOf(enforcer.enforce(cutString(params[0]))));
        handlers.put(ENFORCE_WITH_MATCHER, (params) -> String.valueOf(enforcer.enforceWithMatcher(params[0], cutString(params[1]))));
        handlers.put(ENFORCE_EX, (params) -> String.valueOf(enforcer.enforceEx(cutString(params[0]))));
        handlers.put(ENFORCE_EX_WITH_MATCHER, (params) -> String.valueOf(enforcer.enforceExWithMatcher(params[0], cutString(params[1]))));
        handlers.put(BATCH_ENFORCE, (params) -> String.valueOf(enforcer.batchEnforce(parseNestedLists(params[0]))));
        handlers.put(GET_ALL_SUBJECTS, (params) -> String.valueOf(enforcer.getAllSubjects()));
        handlers.put(GET_ALL_NAMED_SUBJECTS, (params) -> String.valueOf(enforcer.getAllNamedSubjects(params[0])));
        handlers.put(GET_ALL_OBJECTS, (params) -> String.valueOf(enforcer.getAllObjects()));
        handlers.put(GET_ALL_NAMED_OBJECTS, (params) -> String.valueOf(enforcer.getAllNamedObjects(params[0])));
        handlers.put(GET_ALL_ACTIONS, (params) -> String.valueOf(enforcer.getAllActions()));
        handlers.put(GET_ALL_NAMED_ACTIONS, (params) -> String.valueOf(enforcer.getAllNamedActions(params[0])));
        handlers.put(GET_ALL_ROLES, (params) -> String.valueOf(enforcer.getAllRoles()));
        handlers.put(GET_ALL_NAMED_ROLES, (params) -> String.valueOf(enforcer.getAllNamedRoles(params[0])));
        handlers.put(GET_POLICY, (params) -> String.valueOf(enforcer.getPolicy()));
        handlers.put(GET_FILTERED_POLICY, (params) -> String.valueOf(enforcer.getFilteredPolicy(Integer.parseInt(params[0]), cutString(params[1]))));
        handlers.put(GET_NAMED_POLICY, (params) -> String.valueOf(enforcer.getNamedPolicy(params[0])));
        handlers.put(GET_FILTERED_NAMED_POLICY, (params) -> String.valueOf(enforcer.getFilteredNamedPolicy(params[0], Integer.parseInt(params[1]), cutString(params[2]))));
        handlers.put(GET_GROUPING_POLICY, (params) -> String.valueOf(enforcer.getGroupingPolicy()));
        handlers.put(GET_FILTERED_GROUPING_POLICY, (params) -> String.valueOf(enforcer.getFilteredGroupingPolicy(Integer.parseInt(params[0]), cutString(params[1]))));
        handlers.put(GET_NAMED_GROUPING_POLICY, (params) -> String.valueOf(enforcer.getNamedGroupingPolicy(params[0])));
        handlers.put(GET_FILTERED_NAMED_GROUPING_POLICY, (params) -> String.valueOf(enforcer.getFilteredNamedGroupingPolicy(params[0], Integer.parseInt(params[1]), cutString(params[0]))));
        handlers.put(HAS_POLICY, (params) -> String.valueOf(enforcer.hasPolicy(cutString(params[0]))));
        handlers.put(HAS_NAMED_POLICY, (params) -> String.valueOf(enforcer.hasNamedPolicy(params[0], cutString(params[1]))));
        handlers.put(ADD_POLICY, (params) -> String.valueOf(enforcer.addPolicy(cutString(params[0]))));
        handlers.put(ADD_POLICIES, (params) -> String.valueOf(enforcer.addPolicies(parseNestedLists(params[0]))));
        handlers.put(ADD_NAMED_POLICY, (params) -> String.valueOf(enforcer.addNamedPolicy(params[0], cutString(params[1]))));
        handlers.put(ADD_NAMED_POLICIES, (params) -> String.valueOf(enforcer.addNamedPolicies(params[0], parseNestedLists(params[1]))));
        handlers.put(REMOVE_POLICY, (params) -> String.valueOf(enforcer.removePolicy(cutString(params[0]))));
        handlers.put(REMOVE_POLICIES, (params) -> String.valueOf(enforcer.removePolicies(parseNestedLists(params[0]))));
        handlers.put(REMOVE_FILTERED_POLICY, (params) -> String.valueOf(enforcer.removeFilteredPolicy(Integer.parseInt(params[0]), cutString(params[1]))));
        handlers.put(REMOVE_NAMED_POLICY, (params) -> String.valueOf(enforcer.removeNamedPolicy(params[0], cutString(params[1]))));
        handlers.put(REMOVE_NAMED_POLICIES, (params) -> String.valueOf(enforcer.removeNamedPolicies(params[0], parseNestedLists(params[1]))));
        handlers.put(REMOVE_FILTERED_NAMED_POLICY, (params) -> String.valueOf(enforcer.removeFilteredNamedPolicy(params[0], Integer.parseInt(params[1]), cutString(params[0]))));
        handlers.put(HAS_GROUPING_POLICY, (params) -> String.valueOf(enforcer.hasGroupingPolicy(cutString(params[0]))));
        handlers.put(HAS_NAMED_GROUPING_POLICY, (params) -> String.valueOf(enforcer.hasNamedGroupingPolicy(params[0], cutString(params[1]))));
        handlers.put(ADD_GROUPING_POLICY, (params) -> String.valueOf(enforcer.addGroupingPolicy(cutString(params[0]))));
        handlers.put(ADD_GROUPING_POLICIES, (params) -> String.valueOf(enforcer.addGroupingPolicies(parseNestedLists(params[0]))));
        handlers.put(ADD_NAMED_GROUPING_POLICY, (params) -> String.valueOf(enforcer.addNamedGroupingPolicy(params[0], cutString(params[1]))));
        handlers.put(ADD_NAMED_GROUPING_POLICIES, (params) -> String.valueOf(enforcer.addNamedGroupingPolicies(params[0], parseNestedLists(params[1]))));
        handlers.put(REMOVE_GROUPING_POLICY, (params) -> String.valueOf(enforcer.removeGroupingPolicy(params[0])));
        handlers.put(REMOVE_GROUPING_POLICIES, (params) -> String.valueOf(enforcer.removeGroupingPolicies(parseNestedLists(params[0]))));
        handlers.put(REMOVE_FILTERED_GROUPING_POLICY, (params) -> String.valueOf(enforcer.removeFilteredGroupingPolicy(Integer.parseInt(params[0]), cutString(params[1]))));
        handlers.put(REMOVE_NAMED_GROUPING_POLICY, (params) -> String.valueOf(enforcer.removeNamedGroupingPolicy(params[0], cutString(params[1]))));
        handlers.put(REMOVE_FILTERED_NAMED_GROUPING_POLICY, (params) -> String.valueOf(enforcer.removeFilteredNamedGroupingPolicy(params[0], Integer.parseInt(params[1]), cutString(params[0]))));
        handlers.put(UPDATE_POLICY, (params) -> String.valueOf(enforcer.updatePolicy(parseOrdinary(params[0]), parseOrdinary(params[1]))));
        //handlers.put(LOAD_FILTERED_POLICY, (params) -> String.valueOf(enforcer.loadFilteredPolicy()));
        handlers.put(UPDATE_GROUPING_POLICY, (params) -> String.valueOf(enforcer.updateGroupingPolicy(parseOrdinary(params[0]), parseOrdinary(params[1]))));
        handlers.put(UPDATE_NAMED_GROUPING_POLICY, (params) -> String.valueOf(enforcer.updateNamedGroupingPolicy(params[0], parseOrdinary(params[1]), parseOrdinary(params[2]))));

        return handlers;
    }


    private static Options getOptions() {
        Options options = new Options();

        Option option = new Option("e", ENFORCE, false, "enforce access control");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("ewm", ENFORCE_WITH_MATCHER, false, "enforce access control with matcher");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("ex", ENFORCE_EX, false, "enforce access control exception");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("exwm", ENFORCE_EX_WITH_MATCHER, false, "enforce access control exception with matcher");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("be", BATCH_ENFORCE, false, "batch enforce access control");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("gas", GET_ALL_SUBJECTS, false, "get all subjects");
        options.addOption(option);

        option = new Option("gans", GET_ALL_NAMED_SUBJECTS, false, "get all named subjects");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("go", GET_ALL_OBJECTS, false, "get all objects");
        options.addOption(option);

        option = new Option("ganos", GET_ALL_NAMED_OBJECTS, false, "get all named objects");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("ga", GET_ALL_ACTIONS, false, "get all actions");
        options.addOption(option);

        option = new Option("ganas", GET_ALL_NAMED_ACTIONS, false, "get all named actions");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("grl", GET_ALL_ROLES, false, "get all roles");
        options.addOption(option);

        option = new Option("garnr", GET_ALL_NAMED_ROLES, false, "get all named roles");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("gp", GET_POLICY, false, "get policy");
        options.addOption(option);

        option = new Option("gfp", GET_FILTERED_POLICY, false, "get filtered policy");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("gnp", GET_NAMED_POLICY, false, "get named policy");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("gfnp", GET_FILTERED_NAMED_POLICY, false, "get filtered named policy");
        option.setArgs(3);
        options.addOption(option);

        option = new Option("ggp", GET_GROUPING_POLICY, false, "get grouping policy");
        options.addOption(option);

        option = new Option("gfgp", GET_FILTERED_GROUPING_POLICY, false, "get filtered grouping policy");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("gnrp", GET_NAMED_GROUPING_POLICY, false, "get named grouping policy");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("gfngp", GET_FILTERED_NAMED_GROUPING_POLICY, false, "get filtered named grouping policy");
        option.setArgs(3);
        options.addOption(option);

        option = new Option("hp", HAS_POLICY, false, "check if a policy exists");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("hnp", HAS_NAMED_POLICY, false, "check if a named policy exists");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("ap", ADD_POLICY, false, "add a policy");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("aps", ADD_POLICIES, false, "add multiple policies");
        option.setArgs(0);
        options.addOption(option);

        option = new Option("anp", ADD_NAMED_POLICY, false, "add a named policy");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("ans", ADD_NAMED_POLICIES, false, "add multiple named policies");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("rp", REMOVE_POLICY, false, "remove a policy");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("rps", REMOVE_POLICIES, false, "remove multiple policies");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("rfp", REMOVE_FILTERED_POLICY, false, "remove a policy by filter");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("rnp", REMOVE_NAMED_POLICY, false, "remove a named policy");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("rnps", REMOVE_NAMED_POLICIES, false, "remove multiple named policies");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("rfnp", REMOVE_FILTERED_NAMED_POLICY, false, "remove a named policy by filter");
        option.setArgs(3);
        options.addOption(option);

        option = new Option("hgp", HAS_GROUPING_POLICY, false, "check if a grouping policy exists");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("hnpgp", HAS_NAMED_GROUPING_POLICY, false, "check if a named grouping policy exists");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("agp", ADD_GROUPING_POLICY, false, "add a grouping policy");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("ags", ADD_GROUPING_POLICIES, false, "add multiple grouping policies");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("angp", ADD_NAMED_GROUPING_POLICY, false, "add a named grouping policy");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("angs", ADD_NAMED_GROUPING_POLICIES, false, "add multiple named grouping policies");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("agnex", REMOVE_GROUPING_POLICIES, false, "remove multiple grouping policies");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("rgp", REMOVE_GROUPING_POLICY, false, "remove a grouping policy");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("rgs", REMOVE_GROUPING_POLICIES, false, "remove multiple grouping policies");
        option.setArgs(1);
        options.addOption(option);

        option = new Option("rgfp", REMOVE_FILTERED_GROUPING_POLICY, false, "remove a grouping policy by filter");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("rngp", REMOVE_NAMED_GROUPING_POLICY, false, "remove a named grouping policy");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("rnfngp", REMOVE_FILTERED_NAMED_GROUPING_POLICY, false, "remove a named grouping policy by filter");
        option.setArgs(3);
        options.addOption(option);

        option = new Option("up", UPDATE_POLICY, false, "update a policy");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("lfp", LOAD_FILTERED_POLICY, false, "load a filtered policy");
        options.addOption(option);

        option = new Option("ugp", UPDATE_GROUPING_POLICY, false, "update a grouping policy");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("ungp", UPDATE_NAMED_GROUPING_POLICY, false, "update a named grouping policy");
        option.setArgs(3);
        options.addOption(option);

        return options;
    }


}
