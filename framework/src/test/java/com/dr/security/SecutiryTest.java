package com.dr.security;

import com.dr.Application;
import com.dr.framework.core.security.entity.Permission;
import com.dr.framework.core.security.entity.SysMenu;
import com.dr.framework.core.security.service.SecurityManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class SecutiryTest {
    @Autowired
    SecurityManager securityManager;

    @Test
    public void testAddPermission() {
        String permissionCode = "aaaa";
        Permission permissio = new Permission();
        permissio.setCode(permissionCode);
    }

    @Test
    public void testAddMenu() {
        String permissionCode = "aaaa";
        SysMenu sysMenu = new SysMenu();
        sysMenu.setUrl(permissionCode);
    }
}
