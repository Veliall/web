package org.example.app.repository;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.jdbc.JdbcTemplate;
import org.example.jdbc.RowMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CardRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Card> cardRowMapper = resultSet -> new Card(
            resultSet.getLong("id"),
            resultSet.getLong("number"),
            resultSet.getLong("balance")
    );
    private final RowMapper<Long> cardIdRowMapper = resultSet -> resultSet.getLong("ownerId");
    private final RowMapper<Long> cardNumberRowMapper = resultSet -> resultSet.getLong("number");

    public List<Card> getAllByOwnerId(long ownerId) {
        // language=PostgreSQL
        return jdbcTemplate.queryAll(
                "SELECT id, number, balance FROM cards WHERE \"ownerId\" = ? AND active = TRUE",
                cardRowMapper,
                ownerId
        );
    }

    public Optional<Card> getById(long cardId) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                "SELECT id, number, balance FROM cards WHERE id = ? AND active = TRUE",
                cardRowMapper,
                cardId
        );
    }

    public Optional<Card> getByNumber(long number) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                "SELECT id, number, balance FROM cards WHERE number = ? AND active = TRUE",
                cardRowMapper,
                number
        );
    }

    public void transaction(long cardId, long addresseeNumber, int sum) {
        // language=PostgreSQL
        jdbcTemplate.update(
                "UPDATE cards SET balance = balance + ? WHERE number = ? AND active = TRUE",
                sum,
                addresseeNumber
        );
        // language=PostgreSQL
        jdbcTemplate.update(
                "UPDATE cards SET balance = balance - ? WHERE id = ? AND active = TRUE",
                sum,
                cardId
        );
    }

    public Optional<Long> getOwnerId(long cardId) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                "SELECT \"ownerId\" FROM cards WHERE id = ? AND active = TRUE",
                cardIdRowMapper,
                cardId
        );
    }

    public Optional<Card> createNewCard(long userId, long number) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        INSERT INTO cards("ownerId",number) VALUES (?, ?) RETURNING id, number, balance
                        """,
                cardRowMapper,
                userId, number
        );
    }

    public Optional<Card> blockById(long cardId) {
        //language=PostgreSQL
        return jdbcTemplate.queryOne(
                "UPDATE cards SET active = FALSE WHERE id = ? RETURNING id, number, balance",
                cardRowMapper,
                cardId
        );
    }
}
