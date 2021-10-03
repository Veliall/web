package org.example.framework.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.app.exception.WrongLoginAndPassException;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.AuthenticationException;
import org.example.framework.security.AuthenticationProvider;
import org.example.framework.util.FilterHelper;

import java.io.IOException;
import java.util.Base64;

public class BasicAuthenticationFilter extends HttpFilter {
    private AuthenticationProvider provider;

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        provider = ((AuthenticationProvider) getServletContext().getAttribute(ContextAttributes.AUTH_PROVIDER_ATTR));
    }


    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (!FilterHelper.authenticationIsRequired(req)) {
            super.doFilter(req, res, chain);
            return;
        }

        final var auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Basic")) {
            super.doFilter(req, res, chain);
            return;
        }

        final var schemaAndEncodeData = auth.split(" ");
        if (schemaAndEncodeData.length != 2) {
            throw new WrongLoginAndPassException();
        }

        final var loginAndPass = new String(Base64.getDecoder().decode(schemaAndEncodeData[1]));
        final var split = loginAndPass.split(":");
        if (split.length != 2) {
            throw new WrongLoginAndPassException();
        }

        final var principal = split[0];
        final var credentials = split[1];

        try {
            final var authentication = provider.authenticate
                    (principal, credentials);
            req.setAttribute(RequestAttributes.AUTH_ATTR, authentication);
        } catch (AuthenticationException e) {
            res.sendError(401);
            return;
        }

        super.doFilter(req, res, chain);
    }

}
