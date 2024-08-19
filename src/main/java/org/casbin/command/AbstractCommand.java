package org.casbin.command;

import org.casbin.NewEnforcer;

public abstract class AbstractCommand {

    protected AbstractCommand() {

    }

    public abstract String run(NewEnforcer enforcer, String... args) throws Exception;
}
