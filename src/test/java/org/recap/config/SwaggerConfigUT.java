package org.recap.config;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.ServletContext;

import static org.junit.Assert.assertNotNull;

public class SwaggerConfigUT extends BaseTestCaseUT {

    @InjectMocks
    SwaggerConfig swaggerConfig;

    @Mock
    ApplicationContext applicationContext;

    @Mock
    ServletContext servletContext;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(swaggerConfig, "buildVersionNumber", "10");
    }

    @Test
    public void documentation() {
        Docket docket = swaggerConfig.documentation();
        assertNotNull(docket);
    }

    @Test
    public void addResourceHandlers() {
        ResourceHandlerRegistry registry = new ResourceHandlerRegistry(applicationContext, servletContext);
        swaggerConfig.addResourceHandlers(registry);
    }

    @Test
    public void addInterceptors() {
        InterceptorRegistry interceptorRegistry = new InterceptorRegistry();
        swaggerConfig.addInterceptors(interceptorRegistry);
    }
}
