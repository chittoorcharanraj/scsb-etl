package org.recap.config;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.Writer;

import static org.junit.Assert.assertTrue;

/**
 * Created by hemalathas on 19/4/17.
 */
public class SwaggerInterceptorUT extends BaseTestCaseUT {

    @InjectMocks
    SwaggerInterceptor swaggerInterceptor;

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Test
    public void testPreHandleExportFailure() throws Exception {
        httpServletRequest.setAttribute("api_key", null);
        PrintWriter printWriter = new PrintWriter(Writer.nullWriter());
        Mockito.when(httpServletResponse.getWriter()).thenReturn(printWriter);
        boolean continueExport = swaggerInterceptor.preHandle(httpServletRequest, httpServletResponse, new Object());
        assertTrue(!continueExport);
    }

    @Test
    public void testPostHandle() throws Exception {
        swaggerInterceptor.postHandle(httpServletRequest, httpServletResponse, new Object(), new ModelAndView());
    }

    @Test
    public void testAfterCompletion() throws Exception {
        swaggerInterceptor.afterCompletion(httpServletRequest, httpServletResponse, new Object(), new Exception());
    }

}