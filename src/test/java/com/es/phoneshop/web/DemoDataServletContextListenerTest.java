package com.es.phoneshop.web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DemoDataServletContextListenerTest {
    @Mock
    private ServletContextEvent event;
    @Mock
    private ServletContext context;
    private ServletContextListener contextListener = new DemoDataServletContextListener();

    @Test
    public void isContextInitialized() {
        when(event.getServletContext()).thenReturn(context);
        when(event.getServletContext().getInitParameter(any())).thenReturn("true");
        contextListener.contextInitialized(event);
        verify(context).getInitParameter(eq("insertDemoData"));
    }
}
