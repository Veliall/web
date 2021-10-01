package org.example.app.repository;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.User;
import org.example.app.domain.UserWithPassword;
import org.example.jdbc.JdbcTemplate;
import org.example.jdbc.RowMapper;

import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> rowMapper = resultSet -> new User(
            resultSet.getLong("id"),
            resultSet.getString("username")
    );
    private final RowMapper<UserWithPassword> rowMapperWithPassword = resultSet -> new UserWithPassword(
            resultSet.getLong("id"),
            resultSet.getString("username"),
            resultSet.getString("password")
    );
    private final RowMapper<Integer> rowMapperCode = resultSet -> resultSet.getInt("code");
    private final RowMapper<String> rowMapperToken = resultSet -> resultSet.getString("token");
    private final RowMapper<Date> rowMapperTime = resultSet -> resultSet.getTimestamp("endLifeTime");
    private final RowMapper<String[]> rowMapperRoles = resultSet -> new String[]{
            resultSet.getString("role")
    };

    public Optional<User> getByUsername(String username) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne("SELECT id, username FROM users WHERE username = ?", rowMapper, username);
    }

    public Optional<UserWithPassword> getByUsernameWithPassword(String username) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne("SELECT id, username, password FROM users WHERE username = ?", rowMapperWithPassword, username);
    }

    /**
     * saves user to db
     *
     * @param id       - user id, if 0 - insert, if not 0 - update
     * @param username
     * @param hash
     */
    public Optional<User> save(long id, String username, String hash) {
        // language=PostgreSQL
        return id == 0 ? jdbcTemplate.queryOne(
                """
                        INSERT INTO users(username, password) VALUES (?, ?) RETURNING id, username
                        """,
                rowMapper,
                username, hash
        ) : jdbcTemplate.queryOne(
                """
                        UPDATE users SET username = ?, password = ? WHERE id = ? RETURNING id, username
                        """,
                rowMapper,
                username, hash, id
        );
    }

    public Optional<User> findByToken(String token) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        SELECT u.id, u.username FROM tokens t
                        JOIN users u ON t."userId" = u.id
                        WHERE t.token = ?
                        """,
                rowMapper,
                token
        );
    }

    public Optional<String> findToken(long userId) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne("SELECT token FROM tokens WHERE \"userId\" = ?",
                rowMapperToken,
                userId);
    }

    public void saveToken(long userId, String token) {
        // language=PostgreSQL
        jdbcTemplate.update(
                """
                        INSERT INTO tokens(token, "userId") VALUES (?, ?)
                        """,
                token, userId
        );
    }

    public void updateToken(long userId, String token) {
        // language=PostgreSQL
        jdbcTemplate.update(
                """
                        UPDATE tokens SET token = ? WHERE "userId" = ?
                        """,
                token, userId
        );
    }

    public void recoveryRequest(String username, int code) {
        // language=PostgreSQL
        jdbcTemplate.update(
                """
                        INSERT INTO recovery_password (username, code) VALUES (?, ?)
                        """,
                username, code
        );
    }

    public void recoveryUpdate(String username, int code) {
        // language=PostgreSQL
        jdbcTemplate.update(
                """
                        UPDATE recovery_password SET code = ? WHERE username = ?
                        """,
                code, username
        );
    }

    public Optional<Integer> getCodeForRecovery(String username) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                "SELECT code FROM recovery_password WHERE username = ?",
                rowMapperCode,
                username
        );
    }

    public Optional<Date> getTokenEndLifeTime(String token) {
// language=PostgreSQL
        return jdbcTemplate.queryOne(
                "SELECT \"endLifeTime\" FROM tokens WHERE token = ?",
                rowMapperTime,
                token
        );
    }

    public void updateTokenLifeTime(String token) {
        // language=PostgreSQL
        jdbcTemplate.update(
                """
                        UPDATE tokens SET \"endLifeTime\" = current_timestamp + '600 second' WHERE token = ?
                        """,
                token
        );
    }

    public Optional<String[]> getRoles(long id) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                "SELECT role FROM roles WHERE \"userId\" = ?",
                rowMapperRoles,
                id
        );
    }
}
