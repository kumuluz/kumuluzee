package com.kumuluz.ee.common.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PoweredByFilter implements Filter {

    private String name;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.name = filterConfig.getInitParameter("name");
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException,
            ServletException {

        ((HttpServletResponse) response).addHeader("X-Powered-By", name);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}