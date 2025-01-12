package org.casbin;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ClientTest {

    @Test
    public void testRBAC() throws ParseException {
        assertEquals(Client.run(new String[]{"enforce","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "alice", "data1", "read"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "alice", "data1", "write"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "alice", "data2", "read"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "alice", "data2", "write"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "bob", "data1", "read"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "bob", "data1", "write"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "bob", "data2", "read"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "bob", "data2", "write"}), "{\"allow\":true,\"explain\":null}");
    }

    @Test
    public void testABAC() throws ParseException {
        assertEquals(Client.run(new String[]{"enforce","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv", "alice", "domain1", "data1", "read"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv", "alice","domain1", "data1", "write"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv", "alice", "domain2", "data1", "read"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv", "alice", "domain2", "data1", "write"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv", "bob", "domain1", "data2", "read"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv", "bob", "domain1", "data2", "write"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv", "bob", "domain2", "data2", "read"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv", "bob", "domain2", "data2", "read"}), "{\"allow\":true,\"explain\":null}");

    }

    @Test
    public void testAddAndRemovePolicy() throws ParseException {
        assertEquals(Client.run(new String[]{"addPolicy","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv", "alice", "domain1", "data1", "read"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"removePolicy","-m","examples/abac_rule_with_domains_model.conf","-p","examples/abac_rule_with_domains_policy.csv", "alice", "domain1", "data1", "read"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"addPolicy","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "alice", "data2", "write"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"removePolicy","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "alice", "data2", "write"}), "{\"allow\":true,\"explain\":null}");
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
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", policy, "alice", "data1", "read"}), "{\"allow\":true,\"explain\":null}");
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
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func, "alice", "/alice_data/resource1", "GET"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "alice", "/alice_data/resource1", "POST"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "alice", "/alice_data/resource2", "GET"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "alice", "/alice_data/resource2", "POST"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "alice", "/bob_data/resource1", "GET"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "alice", "/bob_data/resource1", "POST"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "alice", "/bob_data/resource2", "GET"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "alice", "/bob_data/resource2", "POST"}), "{\"allow\":false,\"explain\":null}");

        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "bob", "/alice_data/resource1", "GET"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "bob", "/alice_data/resource1", "POST"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "bob", "/alice_data/resource2", "GET"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "bob", "/alice_data/resource2", "POST"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "bob", "/bob_data/resource1", "GET"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "bob", "/bob_data/resource1", "POST"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "bob", "/bob_data/resource2", "GET"}), "{\"allow\":false,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "bob", "/bob_data/resource2", "POST"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "cathy", "/cathy_data", "GET"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "cathy", "/cathy_data", "POST"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforce", "-m", model, "-p", "examples/keymatch_policy.csv", "-AF", func,   "cathy", "/cathy_data", "DELETE"}), "{\"allow\":false,\"explain\":null}");

    }

    @Test
    public void testEnforce() {
        assertEquals(Client.run(new String[]{"enforce", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "alice", "data1", "read"}), "{\"allow\":true,\"explain\":null}");
    }

    @Test
    public void testManagementApi() {
        assertEquals(Client.run(new String[]{"enforce","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "alice", "data1", "read"}), "{\"allow\":true,\"explain\":null}");

        String matcher = "m = r.sub == 'root' || r.sub == p.sub && r.obj == p.obj && r.act == p.act";
        assertEquals(Client.run(new String[]{"enforceWithMatcher","-m","examples/basic_model.conf", "-p", "examples/basic_policy.csv", matcher, "alice", "data1", "read"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"enforceEx", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "alice", "data1", "read"}), "{\"allow\":true,\"explain\":[\"alice\",\"data1\",\"read\"]}");

        assertEquals(Client.run(new String[]{"enforceExWithMatcher", "-m", "examples/basic_model.conf", "-p", "examples/basic_policy.csv", matcher, "alice", "data1", "read"}), "{\"allow\":true,\"explain\":[\"alice\",\"data1\",\"read\"]}");

        assertEquals(Client.run(new String[]{"batchEnforce", "-m", "examples/basic_model.conf", "-p", "examples/basic_policy.csv", "alice,data1,read", "bob,data2,write", "jack,data3,read"}), "{\"allow\":null,\"explain\":[true,true,false]}");

        assertEquals(Client.run(new String[]{"getAllSubjects", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "alice", "data1", "read"}), "{\"allow\":null,\"explain\":[\"alice\",\"bob\",\"data2_admin\"]}");

        assertEquals(Client.run(new String[]{"getAllObjects", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "alice", "data1", "read"}), "{\"allow\":null,\"explain\":[\"data1\",\"data2\"]}");

        assertEquals(Client.run(new String[]{"getAllNamedSubjects", "-m", "examples/abac_rule_with_domains_model.conf", "-p", "examples/abac_rule_with_domains_policy.csv", "p"}), "{\"allow\":null,\"explain\":[\"r.domain == 'domain1'\",\"r.domain == 'domain2'\"]}");

        assertEquals(Client.run(new String[]{"getAllNamedObjects", "-m", "examples/abac_rule_with_domains_model.conf", "-p", "examples/abac_rule_with_domains_policy.csv", "p"}), "{\"allow\":null,\"explain\":[\"admin\"]}");

        assertEquals(Client.run(new String[]{"getAllActions", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv"}), "{\"allow\":null,\"explain\":[\"read\",\"write\"]}");

        assertEquals(Client.run(new String[]{"getAllNamedActions", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "p"}), "{\"allow\":null,\"explain\":[\"read\",\"write\"]}");

        assertEquals(Client.run(new String[]{"getAllRoles", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv"}), "{\"allow\":null,\"explain\":[\"data2_admin\"]}");

        assertEquals(Client.run(new String[]{"getAllNamedRoles", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "g"}), "{\"allow\":null,\"explain\":[\"data2_admin\"]}");

        assertEquals(Client.run(new String[]{"getPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv"}), "{\"allow\":null,\"explain\":[[\"alice\",\"data1\",\"read\"],[\"bob\",\"data2\",\"write\"],[\"data2_admin\",\"data2\",\"read\"],[\"data2_admin\",\"data2\",\"write\"]]}");

        assertEquals(Client.run(new String[]{"getFilteredPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "0", "alice"}), "{\"allow\":null,\"explain\":[[\"alice\",\"data1\",\"read\"]]}");

        assertEquals(Client.run(new String[]{"getNamedPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "p"}), "{\"allow\":null,\"explain\":[[\"alice\",\"data1\",\"read\"],[\"bob\",\"data2\",\"write\"],[\"data2_admin\",\"data2\",\"read\"],[\"data2_admin\",\"data2\",\"write\"]]}");

        assertEquals(Client.run(new String[]{"getFilteredNamedPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "p", "0", "bob"}), "{\"allow\":null,\"explain\":[[\"bob\",\"data2\",\"write\"]]}");

        assertEquals(Client.run(new String[]{"getGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv"}), "{\"allow\":null,\"explain\":[[\"alice\",\"data2_admin\"]]}");

        assertEquals(Client.run(new String[]{"getFilteredGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "0", "alice"}), "{\"allow\":null,\"explain\":[[\"alice\",\"data2_admin\"]]}");

        assertEquals(Client.run(new String[]{"getNamedGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "g"}), "{\"allow\":null,\"explain\":[[\"alice\",\"data2_admin\"]]}");

        assertEquals(Client.run(new String[]{"getFilteredNamedGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "g", "0", "alice"}), "{\"allow\":null,\"explain\":[[\"alice\",\"data2_admin\"]]}");

        assertEquals(Client.run(new String[]{"hasPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "alice", "data1", "read"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"hasNamedPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "p", "alice", "data1", "read"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"addPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "eve", "data3", "read"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"removePolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "eve", "data3", "read"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"addPolicies", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "jack,data4,read","katy,data4,write"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"removePolicies", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "jack,data4,read","katy,data4,write"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"addNamedPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "p", "eve", "data3", "read"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"removeNamedPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "p", "eve", "data3", "read"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"addNamedPolicies", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "p", "jack,data4,read","katy,data4,write"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"hasGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "alice", "data2_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"hasNamedGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "g", "alice", "data2_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"addGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "group1", "data2_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"updateGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "group1,data2_admin","group2,data3_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"removeGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "group2", "data3_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"addGroupingPolicies", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "ham,data4_admin","jack,data5_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"removeGroupingPolicies", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "ham,data4_admin","jack,data5_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"addNamedGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "g", "group2", "data2_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"removeNamedGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "g", "group2", "data2_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"addNamedGroupingPolicies", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "g", "ham,data4_admin","jack,data5_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"removeNamedGroupingPolicies", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "g", "ham,data4_admin","jack,data5_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"removeFilteredGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "0", "alice"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"addNamedGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "g", "alice", "data2_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"removeFilteredNamedGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "g", "0", "alice"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"addNamedGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "g", "alice", "data2_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"updatePolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "alice,data1,read","alice,data1,write"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"updatePolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "alice,data1,write","alice,data1,read"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"updateNamedGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "g", "alice,data2_admin","admin,data4_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"updateNamedGroupingPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "g", "admin,data4_admin","alice,data2_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"removeNamedPolicies", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "p", "jack,data4,read","katy,data4,write"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"removeFilteredPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "0", "alice", "data1", "read"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"removeFilteredNamedPolicy", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "p", "0", "bob"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"removePolicy","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "data2_admin", "data2", "read"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"removePolicy","-m","examples/rbac_model.conf","-p","examples/rbac_policy.csv", "data2_admin", "data2", "write"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"addPolicies", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv","alice,data1,read","bob,data2,write","data2_admin,data2,read","data2_admin,data2,write"}), "{\"allow\":true,\"explain\":null}");

    }

    @Test
    public void testRBACApi () {
        assertEquals(Client.run(new String[]{"getRolesForUser", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "alice"}), "{\"allow\":null,\"explain\":[\"data2_admin\"]}");

        assertEquals(Client.run(new String[]{"getUsersForRole", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "data2_admin"}), "{\"allow\":null,\"explain\":[\"alice\"]}");

        assertEquals(Client.run(new String[]{"hasRoleForUser", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "alice", "data2_admin"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"deleteRoleForUser", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "alice", "data2_admin"}), "{\"allow\":true,\"explain\":null}");
        resetRBACPolicyFile();

        assertEquals(Client.run(new String[]{"deleteRolesForUser", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "alice"}), "{\"allow\":true,\"explain\":null}");
        resetRBACPolicyFile();

        assertEquals(Client.run(new String[]{"deleteUser", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "alice"}), "{\"allow\":true,\"explain\":null}");
        resetRBACPolicyFile();

        assertEquals(Client.run(new String[]{"deleteRole", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "data2_admin"}), "{\"allow\":null,\"explain\":null}");
        resetRBACPolicyFile();

        assertEquals(Client.run(new String[]{"deletePermission", "-m", "examples/basic_without_resources_model.conf", "-p", "examples/basic_without_resources_policy.csv", "read"}), "{\"allow\":true,\"explain\":null}");
        resetBasicWithResourcesPolicyFile();

        assertEquals(Client.run(new String[]{"addPermissionForUser", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "bob", "read"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"deletePermissionForUser", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "bob", "read"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"deletePermissionsForUser", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_policy.csv", "alice"}), "{\"allow\":true,\"explain\":null}");
        resetRBACPolicyFile();

        assertEquals(Client.run(new String[]{"hasPermissionForUser", "-m", "examples/basic_without_resources_model.conf", "-p", "examples/basic_without_resources_policy.csv", "alice", "read"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"getImplicitUsersForRole", "-m", "examples/rbac_with_pattern_model.conf", "-p", "examples/rbac_with_pattern_policy.csv", "book_admin"}), "{\"allow\":null,\"explain\":[\"alice\"]}");

        assertEquals(Client.run(new String[]{"getImplicitPermissionsForUser", "-m", "examples/rbac_model.conf", "-p", "examples/rbac_with_hierarchy_policy.csv", "alice"}), "{\"allow\":null,\"explain\":[[\"alice\",\"data1\",\"read\"],[\"data1_admin\",\"data1\",\"read\"],[\"data1_admin\",\"data1\",\"write\"],[\"data2_admin\",\"data2\",\"read\"],[\"data2_admin\",\"data2\",\"write\"]]}");

        assertEquals(Client.run(new String[]{"getNamedImplicitPermissionsForUser", "-m", "examples/rbac_with_multiple_policy_model.conf", "-p", "examples/rbac_with_multiple_policy_policy.csv", "p2", "alice"}), "{\"allow\":null,\"explain\":[[\"admin\",\"create\"],[\"user\",\"view\"]]}");

    }

    @Test
    public void testRBACWithDomainsApi () {
        assertEquals(Client.run(new String[]{"getUsersForRoleInDomain", "-m", "examples/rbac_with_domains_model.conf", "-p", "examples/rbac_with_domains_policy.csv", "admin", "domain1"}), "{\"allow\":null,\"explain\":[\"alice\"]}");

        assertEquals(Client.run(new String[]{"getRolesForUserInDomain", "-m", "examples/rbac_with_domains_model.conf", "-p", "examples/rbac_with_domains_policy.csv", "alice", "domain1"}), "{\"allow\":null,\"explain\":[\"admin\"]}");

        assertEquals(Client.run(new String[]{"getPermissionsForUserInDomain", "-m", "examples/rbac_with_domains_model.conf", "-p", "examples/rbac_with_domains_policy.csv", "admin", "domain1"}), "{\"allow\":null,\"explain\":[[\"admin\",\"domain1\",\"data1\",\"read\"],[\"admin\",\"domain1\",\"data1\",\"write\"]]}");

        assertEquals(Client.run(new String[]{"addRoleForUserInDomain", "-m", "examples/rbac_with_domains_model.conf", "-p", "examples/rbac_with_domains_policy.csv", "alice", "admin", "domain3"}), "{\"allow\":true,\"explain\":null}");

        assertEquals(Client.run(new String[]{"deleteRoleForUserInDomain", "-m", "examples/rbac_with_domains_model.conf", "-p", "examples/rbac_with_domains_policy.csv", "alice", "admin", "domain3"}), "{\"allow\":true,\"explain\":null}");
    }


    public void resetRBACPolicyFile() {
        File file = new File("examples/rbac_policy.csv");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write("p, alice, data1, read\n");
            writer.write("p, bob, data2, write\n");
            writer.write("p, data2_admin, data2, read\n");
            writer.write("p, data2_admin, data2, write\n");
            writer.write("g, alice, data2_admin");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetBasicWithResourcesPolicyFile() {
        File file = new File("examples/basic_without_resources_policy.csv");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write("p, alice, read\n");
            writer.write("p, bob, write");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testABACRule() {
        assertEquals(Client.run(new String[]{"enforce", "-m", "examples/abac_rule_model.conf", "-p", "examples/abac_rule_policy.csv", "{Age: 30}", "/data1", "read"}), "{\"allow\":true,\"explain\":null}");
        assertEquals(Client.run(new String[]{"enforceEx", "-m", "examples/abac_rule_model.conf", "-p", "examples/abac_rule_policy.csv", "{Age: 30}", "/data1", "read"}), "{\"allow\":true,\"explain\":[\"r.sub.Age > 18 && r.sub.Age < 60\",\"/data1\",\"read\"]}");
    }
}
