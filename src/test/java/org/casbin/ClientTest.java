package org.casbin;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientTest {

    @Test
    public void testRBAC() throws ParseException {
        assertEquals(Client.run(new String[]{"management","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "-e", "alice,data1,read"}), "true");
        assertEquals(Client.run(new String[]{"management","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "-e", "alice,data1,write"}), "false");
        assertEquals(Client.run(new String[]{"management","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "-e", "alice,data2,read"}), "true");
        assertEquals(Client.run(new String[]{"management","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "-e", "alice,data2,write"}), "true");
        assertEquals(Client.run(new String[]{"management","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "-e", "bob,data1,read"}), "false");
        assertEquals(Client.run(new String[]{"management","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "-e", "bob,data1,write"}), "false");
        assertEquals(Client.run(new String[]{"management","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "-e", "bob,data2,read"}), "false");
        assertEquals(Client.run(new String[]{"management","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "-e", "bob,data2,write"}), "true");
    }

    @Test
    public void testABAC() throws ParseException {
        assertEquals(Client.run(new String[]{"management","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","alice,domain1,data1,read"}), "true");
        assertEquals(Client.run(new String[]{"management","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","alice,domain1,data1,write"}), "true");
        assertEquals(Client.run(new String[]{"management","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","alice,domain2,data1,read"}), "false");
        assertEquals(Client.run(new String[]{"management","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","alice,domain2,data1,write"}), "false");
        assertEquals(Client.run(new String[]{"management","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","bob,domain1,data2,read"}), "false");
        assertEquals(Client.run(new String[]{"management","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","bob,domain1,data2,write"}), "false");
        assertEquals(Client.run(new String[]{"management","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","bob,domain2,data2,read"}), "true");
        assertEquals(Client.run(new String[]{"management","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-e","bob,domain2,data2,read"}), "true");

    }

    @Test
    public void testAddAndRemovePolicy() throws ParseException {
        assertEquals(Client.run(new String[]{"management","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-ap","alice,domain1,data1,read"}), "true");
        assertEquals(Client.run(new String[]{"management","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv","-rp","alice,domain1,data1,read"}), "true");
    }

    @Test
    public void testParseString() {
        String model = "[request_definition]\n" +
                "r = sub, obj, act\n" +
                "\n" +
                "[policy_definition]\n" +
                "p = sub, obj, act\n" +
                "\n" +
                "[role_definition]\n" +
                "g = _, _\n" +
                "\n" +
                "[policy_effect]\n" +
                "e = some(where (p.eft == allow))\n" +
                "\n" +
                "[matchers]\n" +
                "m = g(r.sub, p.sub) && r.obj == p.obj && r.act == p.act";
        String policy = "p, alice, data1, read\n" +
                "p, bob, data2, write\n" +
                "p, data2_admin, data2, read\n" +
                "p, data2_admin, data2, write\n" +
                "g, alice, data2_admin";
        assertEquals(Client.run(new String[]{"management","-m",model,"-p",policy,"-e","alice,data1,read"}), "true");
    }

    @Test
    public void testCustomFunction() throws ParseException {
        String methodName = "keyMatchTest";
        String model = "[request_definition]\n" +
                "r = sub, obj, act\n" +
                "\n" +
                "[policy_definition]\n" +
                "p = sub, obj, act\n" +
                "\n" +
                "[policy_effect]\n" +
                "e = some(where (p.eft == allow))\n" +
                "\n" +
                "[matchers]\n" +
                "m = r.sub == p.sub && "+methodName+"(r.obj, p.obj) && regexMatch(r.act, p.act)\n";
        String func = "public static boolean "+methodName+"(String key1, String key2) {\n" +
                "        int i = key2.indexOf('*');\n" +
                "        if (i == -1) {\n" +
                "            return key1.equals(key2);\n" +
                "        }\n" +
                "\n" +
                "        if (key1.length() > i) {\n" +
                "            return key1.substring(0, i).equals(key2.substring(0, i));\n" +
                "        }\n" +
                "        return key1.equals(key2.substring(0, i));\n" +
                "    }";
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "alice, /alice_data/resource1, GET"}), "true");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "alice, /alice_data/resource1, POST"}), "true");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "alice, /alice_data/resource2, GET"}), "true");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "alice, /alice_data/resource2, POST"}), "false");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "alice, /bob_data/resource1, GET"}), "false");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "alice, /bob_data/resource1, POST"}), "false");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "alice, /bob_data/resource2, GET"}), "false");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "alice, /bob_data/resource2, POST"}), "false");

        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "bob, /alice_data/resource1, GET"}), "false");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "bob, /alice_data/resource1, POST"}), "false");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "bob, /alice_data/resource2, GET"}), "true");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "bob, /alice_data/resource2, POST"}), "false");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "bob, /bob_data/resource1, GET"}), "false");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "bob, /bob_data/resource1, POST"}), "true");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "bob, /bob_data/resource2, GET"}), "false");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "bob, /bob_data/resource2, POST"}), "true");

        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "cathy, /cathy_data, GET"}), "true");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "cathy, /cathy_data, POST"}), "true");
        assertEquals(Client.run(new String[]{"management", "-m", model, "-p", "examples/keymatch_policy.csv", "-af", func, "-e", "cathy, /cathy_data, DELETE"}), "false");

        }

        @Test
        public void testEnforce() {
            assertEquals(Client.run(new String[]{"enforce","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "alice", "data1", "read"}), "true");
        }

}
