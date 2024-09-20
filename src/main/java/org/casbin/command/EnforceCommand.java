package org.casbin.command;

import org.casbin.NewEnforcer;

public class EnforceCommand extends AbstractCommand {
    @Override
    public String run(NewEnforcer enforcer, String... args) throws Exception {
        String subject = args[0];
        String object = args[1];
        String action = args[2];
        boolean res = enforcer.enforce(subject, object, action);
        System.out.println(res ? "Allowed" : "Denied");
        return String.valueOf(res);
    }
}
