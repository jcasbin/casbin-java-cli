package org.casbin.command;

import org.apache.commons.cli.*;
import org.casbin.NewEnforcer;

import java.util.HashMap;
import java.util.Map;


public class RBACWithDomainsCommand extends AbstractCommand{

    private static final String GET_USERS_FOR_ROLE_IN_DOMAIN = "getUsersForRoleInDomain";
    private static final String GET_ROLES_FOR_USER_IN_DOMAIN = "getRolesForUserInDomain";
    private static final String GET_PERMISSIONS_FOR_USER_IN_DOMAIN = "getPermissionsForUserInDomain";
    private static final String ADD_ROLE_FOR_USER_IN_DOMAIN = "addRoleForUserInDomain";
    private static final String DELETE_ROLE_FOR_USER_IN_DOMAIN = "deleteRoleForUserInDomain";
    private static final String DELETE_ROLES_FOR_USER_IN_DOMAIN = "deleteRolesForUserInDomain";

    @Override
    public void run(NewEnforcer enforcer, String... args) throws Exception {
        Options options = getOptions();

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        Map<String, OperationHandle> handlers = getStringOperationHandleMap(enforcer);

        try {
            CommandLine cmd = parser.parse(options, args);

            String option = cmd.hasOption(GET_USERS_FOR_ROLE_IN_DOMAIN) ? GET_USERS_FOR_ROLE_IN_DOMAIN :
                            cmd.hasOption(GET_ROLES_FOR_USER_IN_DOMAIN) ? GET_ROLES_FOR_USER_IN_DOMAIN :
                            cmd.hasOption(GET_PERMISSIONS_FOR_USER_IN_DOMAIN) ? GET_PERMISSIONS_FOR_USER_IN_DOMAIN :
                            cmd.hasOption(ADD_ROLE_FOR_USER_IN_DOMAIN) ? ADD_ROLE_FOR_USER_IN_DOMAIN :
                            cmd.hasOption(DELETE_ROLE_FOR_USER_IN_DOMAIN) ? DELETE_ROLE_FOR_USER_IN_DOMAIN : DELETE_ROLES_FOR_USER_IN_DOMAIN;

            OperationHandle handle = handlers.get(option);
            String[] params = cmd.getOptionValues(option);
            String res = handle.handle(params);
            System.out.println(res);
            enforcer.savePolicy();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            formatter.printHelp("rbac_with_domains", options);
        }
    }

    private static Map<String, OperationHandle> getStringOperationHandleMap(NewEnforcer enforcer) {
        Map<String, OperationHandle> handlers = new HashMap<>();
        handlers.put(GET_USERS_FOR_ROLE_IN_DOMAIN, (params) -> String.valueOf(enforcer.getUsersForRoleInDomain(params[0], params[1])));
        handlers.put(GET_ROLES_FOR_USER_IN_DOMAIN, (params) -> String.valueOf(enforcer.getRolesForUserInDomain(params[0], params[1])));
        handlers.put(GET_PERMISSIONS_FOR_USER_IN_DOMAIN, (params) -> String.valueOf(enforcer.getPermissionsForUserInDomain(params[0], params[1])));
        handlers.put(ADD_ROLE_FOR_USER_IN_DOMAIN, (params) -> String.valueOf(enforcer.addRoleForUserInDomain(params[0], params[1], params[2])));
        handlers.put(DELETE_ROLE_FOR_USER_IN_DOMAIN, (params) -> String.valueOf(enforcer.deleteRoleForUserInDomain(params[0], params[1], params[2])));
        handlers.put(DELETE_ROLES_FOR_USER_IN_DOMAIN, (params) -> String.valueOf(enforcer.deleteRolesForUser(params[0])));
        return handlers;
    }

    private static Options getOptions() {
        Options options = new Options();

        Option option = new Option("gu", GET_USERS_FOR_ROLE_IN_DOMAIN, true, "retrieve the users that have a role within a domain");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("gr", GET_ROLES_FOR_USER_IN_DOMAIN, true, "retrieves the roles that a user has within a domain");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("gp", GET_PERMISSIONS_FOR_USER_IN_DOMAIN, true, "retrieves the permissions for a user or role within a domain");
        option.setArgs(2);
        options.addOption(option);

        option = new Option("ar", ADD_ROLE_FOR_USER_IN_DOMAIN, true, "adds a role for a user within a domain. It returns false if the user already has the role (no changes made)");
        option.setArgs(3);
        options.addOption(option);

        option = new Option("dr", DELETE_ROLE_FOR_USER_IN_DOMAIN, true, "removes a role for a user within a domain. It returns false if the user does not have the role (no changes made)");
        option.setArgs(3);
        options.addOption(option);

        option = new Option("drs", DELETE_ROLES_FOR_USER_IN_DOMAIN, true, "removes all roles for a user within a domain. It returns false if the user does not have any roles (no changes made)");
        option.setArgs(1);
        options.addOption(option);

        return options;
    }
}
