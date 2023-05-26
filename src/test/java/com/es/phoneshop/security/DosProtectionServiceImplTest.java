package com.es.phoneshop.security;

import com.es.phoneshop.security.impl.DosProtectionServiceImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DosProtectionServiceImplTest {
    private DosProtectionService service;

    @Before
    public void init() {
        service = DosProtectionServiceImpl.INSTANCE;
    }

    @Test
    public void ifRequestsQuantityFromOneUserMoreThenThresholdThenReturnFalse() {
        while (service.isAllowed("x")) ;
        assertFalse(service.isAllowed("x"));
        while (!service.isAllowed("x")) ;
        assertTrue(service.isAllowed("x"));
    }


}
