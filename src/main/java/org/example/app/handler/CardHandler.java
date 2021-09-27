package org.example.app.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.app.dto.TransferRequestDto;
import org.example.app.exception.IllegalCardAccessException;
import org.example.app.exception.UserNotFoundException;
import org.example.app.service.CardService;
import org.example.app.util.UserHelper;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.Authentication;
import org.example.framework.security.Roles;

import java.io.IOException;
import java.util.regex.Matcher;

@Log
@RequiredArgsConstructor
public class CardHandler { // Servlet -> Controller -> Service (domain) -> domain
    private final CardService service;
    private final Gson gson;

    public void getAll(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var user = UserHelper.getUser(req);
            final var data = service.getAllByOwnerId(user.getId());
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getAllById(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var userId = Long.parseLong(((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR))
                    .group("userId"));
            final var auth = ((Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR))
                    .getAuthorities();
            final var user = UserHelper.getUser(req);

            if (auth.contains(Roles.ROLE_ADMIN) || user.getId() == userId) {
                final var data = service.getAllByOwnerId(userId);
                resp.setHeader("Content-Type", "application/json");
                resp.getWriter().write(gson.toJson(data));
            } else {
                throw new IllegalCardAccessException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getById(HttpServletRequest req, HttpServletResponse resp) {
        final var cardId = Long.parseLong(((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR))
                .group("cardId"));
        final var auth = ((Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR))
                .getAuthorities();
        final var user = UserHelper.getUser(req);

        if (auth.isEmpty() || auth.contains(Roles.ROLE_ANONYMOUS)) {
            throw new UserNotFoundException("Unsupported option for anonymous users");
        }

        if (auth.contains(Roles.ROLE_ADMIN) || user.getId() == service.getOwnerId(cardId)) {
            final var card = service.getById(cardId);
            resp.setHeader("Content-Type", "application/json");
            try {
                resp.getWriter().write(gson.toJson(card));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void order(HttpServletRequest req, HttpServletResponse resp) {
        final var auth = ((Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR))
                .getAuthorities();
        if (auth.isEmpty() || auth.contains(Roles.ROLE_ANONYMOUS)) {
            throw new UserNotFoundException("Unsupported option for anonymous users");
        }
        try {
            final var user = UserHelper.getUser(req);
            final var newCard = service.order(user.getId());
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(newCard));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void blockById(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var cardId = Long.parseLong(((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR))
                    .group("cardId"));
            final var auth = ((Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR))
                    .getAuthorities();
            final var user = UserHelper.getUser(req);

            if (auth.contains(Roles.ROLE_ADMIN) || user.getId() == service.getOwnerId(cardId)) {
                final var active = service.blockById(cardId);
                if (!active) {
                    resp.setHeader("Content-Type", "text/plain");
                    resp.getWriter().write("Карта успешно заблокирована");
                }
            } else {
                throw new IllegalCardAccessException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void transaction(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var cardId = Long.parseLong(((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR))
                    .group("cardId"));
            final var user = UserHelper.getUser(req);
            final var requestDto = gson.fromJson(req.getReader(), TransferRequestDto.class);
            final var responseDto = service.transfer(cardId, user, requestDto);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
