package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.app.domain.User;
import org.example.app.dto.TransferRequestDto;
import org.example.app.dto.TransferResponseDto;
import org.example.app.exception.CardNotFoundException;
import org.example.app.exception.IllegalCardAccessException;
import org.example.app.exception.UnsupportedTransactionException;
import org.example.app.repository.CardRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;

    public List<Card> getAllByOwnerId(long ownerId) {
        return cardRepository.getAllByOwnerId(ownerId);
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

    public Optional<Card> order(long id) {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(10000);
        String number = String.format("**** %03d", num);

        return cardRepository.createNewCard(id, number);
    }

    public Card getById(long cardId) {
        return cardRepository.getById(cardId).orElseThrow(CardNotFoundException::new);

    }

    public long getOwnerId(long cardId) {
        return cardRepository.getOwnerId(cardId).orElseThrow(CardNotFoundException::new);
    }

    public Boolean blockById(long cardId) {
        return cardRepository.blockById(cardId).orElseThrow(CardNotFoundException::new);
    }
}
