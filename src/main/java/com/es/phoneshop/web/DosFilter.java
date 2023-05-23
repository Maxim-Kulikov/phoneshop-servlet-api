package com.es.phoneshop.web;

import com.es.phoneshop.security.DosProtectionService;
import com.es.phoneshop.security.impl.DosProtectionServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DosFilter implements Filter {
    private DosProtectionService dosProtectionService;

    @Override
    public void init(FilterConfig filterConfig){
        dosProtectionService = DosProtectionServiceImpl.INSTANCE;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
        if(dosProtectionService.isAllowed(req.getRemoteAddr())){
            filterChain.doFilter(req, resp);
        }
        else {
            ((HttpServletResponse) resp).setStatus(429);
        }
    }

    @Override
    public void destroy() {

    }
}
