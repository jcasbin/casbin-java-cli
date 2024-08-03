package org.casbin;

import org.apache.commons.cli.ParseException;
import org.casbin.jcasbin.main.Enforcer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientTest {

    @Test
    public void testRBAC() throws ParseException {
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv","-e","alice,data1,read"}), true);
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv","-e","alice,data1,write"}), false);
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv","-e","alice,data2,read"}), true);
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv","-e","alice,data2,write"}), true);
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv","-e","bob,data1,read"}), false);
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv","-e","bob,data1,write"}), false);
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv","-e","bob,data2,read"}), false);
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv","-e","bob,data2,write"}), true);
    }

    @Test
    public void testABAC() throws ParseException {
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","alice,domain1,data1,read"}), true);
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","alice,domain1,data1,write"}), true);
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","alice,domain2,data1,read"}), false);
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","alice,domain2,data1,write"}), false);
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","bob,domain1,data2,read"}), false);
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","bob,domain1,data2,write"}), false);
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","bob,domain2,data2,read"}), true);
        assertEquals(JCasbinCLI.run(new String[]{"-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","bob,domain2,data2,read"}), true);
    }

    @Test
    public void testAddPolicy() throws ParseException {
        String modelPath = "examples/rbac_model.conf";
        String policyPath = "examples/rbac_policy.csv";
        Enforcer enforcer = new Enforcer(modelPath, policyPath);
        String policy = "aliceTest,data,read";
        assertEquals(JCasbinCLI.run(new String[]{"-m",modelPath,"-p",policyPath,"-ap",policy}), true);
        enforcer.removePolicy(policy.split(","));
        enforcer.savePolicy();
    }

    @Test
    public void testRemovePolicy() throws ParseException {
        String modelPath = "examples/rbac_model.conf";
        String policyPath = "examples/rbac_policy.csv";
        Enforcer enforcer = new Enforcer(modelPath, policyPath);
        String policy = "alice,data1,read";
        assertEquals(JCasbinCLI.run(new String[]{"-m",modelPath,"-p",policyPath,"-rp",policy}), true);
        enforcer.addPolicy(policy.split(","));
        enforcer.savePolicy();

    }

}
