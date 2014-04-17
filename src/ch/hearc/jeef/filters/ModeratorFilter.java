/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.jeef.filters;

import ch.hearc.jeef.beans.LoginBean;
import ch.hearc.jeef.entities.User;
import ch.hearc.jeef.util.JsfUtil;
import com.sun.xml.rpc.processor.generator.nodes.JaxRpcMappingTagNames;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Alexandre
 */
public class ModeratorFilter implements Filter {

    private FilterConfig filterConfig;

    @Inject
    private LoginBean loginBean;

    public ModeratorFilter() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        User user = loginBean.getUser();
        if (user != null && user.getRole().getId() < 3) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletRequest) request).getSession().setAttribute("msg", ResourceBundle.getBundle("/Localization").getString("NotAllow"));
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.sendRedirect("/");
        }
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

}
