package org.casbin.command;

public class HelpCommand {

    public void run() {
        System.out.println("Usage: java -jar casbin-java-cli.jar rbac|rbac_with_condition|rbac_with_domains|role_manager|management [options]");
    }
}
