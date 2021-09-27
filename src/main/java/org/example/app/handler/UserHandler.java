package org.example.app.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.app.dto.LoginRequestDto;
import org.example.app.dto.RecoveryRequestDto;
import org.example.app.dto.RegistrationRequestDto;
import org.example.app.service.UserService;
import org.example.framework.attribute.RequestAttributes;

import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Matcher;

@Log
@RequiredArgsConstructor
public class UserHandler {
    private final UserService service;
    private final Gson gson;

    public void register(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "register");
            final var requestDto = gson.fromJson(req.getReader(), RegistrationRequestDto.class);
            final var responseDto = service.register(requestDto);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void login(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "register");
            final var requestDto = gson.fromJson(req.getReader(), LoginRequestDto.class);
            final var responseDto = service.login(requestDto);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void recoveryRequest(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var username = ((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR))
                    .group("username");

            final var response = service.recoveryRequest(username);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void recovery(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var requestDto = gson.fromJson(req.getReader(), RecoveryRequestDto.class);
            final var response = service.recovery(requestDto);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
