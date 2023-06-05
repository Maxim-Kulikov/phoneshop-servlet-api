package com.es.phoneshop.web;

import com.es.phoneshop.security.DosProtectionService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DosFilterTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private DosProtectionService dosProtectionService;
    @Mock
    private FilterChain filterChain;
    @InjectMocks
    private Filter filter = new DosFilter();

    @Test
    public void testDoFilterInvokeFilterChainWhenAllowedUser() throws ServletException, IOException {
        when(dosProtectionService.isAllowed(request.getRemoteAddr())).thenReturn(true);

        filter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterSetsStatus409WhenNotAllowedUser() throws ServletException, IOException {
        when(dosProtectionService.isAllowed(request.getRemoteAddr())).thenReturn(false);

        filter.doFilter(request, response, filterChain);
        verify(response).setStatus(eq(429));
    }
}