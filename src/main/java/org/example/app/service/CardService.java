package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.app.domain.User;
import org.example.app.dto.TransferRequestDto;
import org.example.app.dto.TransferResponseDto;
import org.example.app.exception.CardNotFoundException;
import org.example.app.exception.IllegalCardAccessException;
import org.example.app.exception.UnsupportedTransactionException;
import org.example.app.exception.UserNotFoundException;
import org.example.app.repository.CardRepository;
import org.example.framework.security.Roles;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;

    public List<Card> getAllByOwnerId(long userId, Collection<String> auth, User user) {
        if (auth.contains(Roles.ROLE_ADMIN) || user.getId() == userId) {
            return cardRepository.getAllByOwnerId(userId);
        } else {
            throw new IllegalCardAccessException();
        }
    }

    public List<Card> getAllByOwnerId(long userId) {
        return cardRepository.getAllByOwnerId(userId);
    }

    public TransferResponseDto transfer(long cardId, User user, TransferRequestDto requestDto) {
        if (user.getId() != cardRepository.getOwnerId(cardId).orElseThrow(IllegalCardAccessException::new)) {
            throw new IllegalCardAccessException();
        }

        final var card = cardRepository.getById(cardId).get();
        final var addressee = requestDto.getAddresseeNumber();
        final var sum = requestDto.getSum();

        if (cardRepository.getByNumber(addressee).isEmpty()) {
            throw new CardNotFoundException();
        }

        if (card.getBalance() < sum) {
            throw new UnsupportedTransactionException("Not enough money");
        }
        if (sum <= 0) {
            throw new UnsupportedTransactionException("Incorrect amount");
        }

        cardRepository.transaction(cardId, addressee, sum);
        final var result = cardRepository.getById(cardId).orElseThrow(UnsupportedTransactionException::new);

        return new TransferResponseDto(result.getId(), result.getNumber(), result.getBalance());
    }

    public Optional<Card> order(long id, Collection<String> auth) {
        if (auth.isEmpty() || auth.contains(Roles.ROLE_ANONYMOUS)) {
            throw new UserNotFoundException("Unsupported option for anonymous users");
        }
        SecureRandom random = new SecureRandom();
        long num = random.nextLong();
        String number = String.format("%016d", num);
        long result = Math.abs(Long.parseLong(number));
        return cardRepository.createNewCard(id, result);
    }

    public Card getById(long cardId, Collection<String> auth, User user) {
        if (auth.isEmpty() || auth.contains(Roles.ROLE_ANONYMOUS)) {
            throw new UserNotFoundException("Unsupported option for anonymous users");
        }
        if (auth.contains(Roles.ROLE_ADMIN) || user.getId() == getOwnerId(cardId)) {
            return cardRepository.getById(cardId).orElseThrow(CardNotFoundException::new);
        } else {
            throw new IllegalCardAccessException();
        }
    }

    public long getOwnerId(long cardId) {
        return cardRepository.getOwnerId(cardId).orElseThrow(CardNotFoundException::new);
    }

    public Card blockById(long cardId, Collection<String> auth, User user) {
        if (auth.contains(Roles.ROLE_ADMIN) || user.getId() == getOwnerId(cardId)) {
            return cardRepository.blockById(cardId).orElseThrow(CardNotFoundException::new);
        }  else {
            throw new IllegalCardAccessException();
        }
    }
}
