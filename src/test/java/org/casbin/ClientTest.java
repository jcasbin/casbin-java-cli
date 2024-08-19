package org.casbin;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ClientTest {

    @Test
    public void testRBAC() throws ParseException {
    }

    @Test
    public void testABAC() throws ParseException {
    }

    @Test
    public void testAddPolicy() throws ParseException {
    }

    @Test
    public void testRemovePolicy() throws ParseException {
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
        NewEnforcer enforce = new NewEnforcer(model, policy);
        assertTrue(enforce.enforce("alice", "data1", "read"));
    }

}
