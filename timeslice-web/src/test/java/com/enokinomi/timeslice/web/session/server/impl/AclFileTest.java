package com.enokinomi.timeslice.web.session.server.impl;

import org.junit.Assert;
import org.junit.Test;


public class AclFileTest
{
    @Test
    public void hello()
    {
        Assert.assertEquals("testpass", new AclFile("src/test/input/auth/test.acl").lookupPassword("test1"));
    }
}
