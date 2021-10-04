package org.example.app.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.app.dto.TransferRequestDto;
import org.example.app.service.CardService;
import org.example.app.util.CardHelper;
import org.example.app.util.UserHelper;

import java.io.IOException;

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
        final var userId = UserHelper.getUserId(req);
        final var auth = UserHelper.getAuthorities(req);
        final var user = UserHelper.getUser(req);
        try {
            final var data = service.getAllByOwnerId(userId, auth, user);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void getById(HttpServletRequest req, HttpServletResponse resp) {
        final var cardId = CardHelper.getCardId(req);
        final var auth = UserHelper.getAuthorities(req);
        final var user = UserHelper.getUser(req);
        try {
            final var card = service.getById(cardId, auth, user);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(card));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void order(HttpServletRequest req, HttpServletResponse resp) {
        final var auth = UserHelper.getAuthorities(req);

        try {
            final var user = UserHelper.getUser(req);
            final var newCard = service.order(user.getId(), auth);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(newCard));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void blockById(HttpServletRequest req, HttpServletResponse resp) {
        final long cardId = CardHelper.getCardId(req);
        final var auth = UserHelper.getAuthorities(req);
        final var user = UserHelper.getUser(req);
        try {
            final var result = service.blockById(cardId, auth, user);
            resp.setHeader("Content-Type", "text/plain");
            resp.getWriter().write(gson.toJson(result));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void transaction(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var cardId = CardHelper.getCardId(req);
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
