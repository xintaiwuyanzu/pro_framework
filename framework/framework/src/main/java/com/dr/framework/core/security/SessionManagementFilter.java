package com.dr.framework.core.security;

import com.dr.framework.core.security.bo.ClientInfo;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionManagementFilter extends GenericFilterBean {
    static final String FILTER_APPLIED = "__session_management_filter_filter_applied";
    private SecurityManager securityManager;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if (request.getAttribute(FILTER_APPLIED) != null) {
            chain.doFilter(req, res);
            return;
        }
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SecurityManager.SECURITY_MANAGER_CONTEXT_KEY) != null) {
            ClientInfo clientInfo = securityManager.currentClientInfo();
            if (clientInfo == null) {
                clientInfo = createClientInfo(request);
            }
        }
        chain.doFilter(req, res);
    }

    private ClientInfo createClientInfo(HttpServletRequest request) {
        return null;
    }
}
