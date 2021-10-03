INSERT INTO users(id, username, password)
VALUES (1, 'admin', '$argon2id$v=19$m=4096,t=3,p=1$CjM/3pdG9sYDzxGFDbesMA$HA1Fm8y23U9QucCFmq1hynPzyCsMfSNKpEqyZVif/og'),
       (2, 'student', '$argon2id$v=19$m=4096,t=3,p=1$gCAC+zI1HRM5IRKJc8HhGw$RadiUVwSqt6m7icr6+Cp67Jh8Sc+YRs+zJ9xw2nYh6M');

ALTER SEQUENCE users_id_seq RESTART WITH 3;

INSERT INTO tokens(token, "userId")
VALUES ('6NSb+2kcdKF44ut4iBu+dm6YLu6pakWapvxHtxqaPgMr5iRhox/HlhBerAZMILPjwnRtXms+zDfVTLCsao9nuw==', 1);


INSERT INTO cards(id, "ownerId", number, balance)
VALUES (1, 1, 1111222233334444, 50000),
       (2, 2, 5555666677778888, 90000);

ALTER SEQUENCE cards_id_seq RESTART WITH 3;

INSERT INTO tokens(token, "userId", "endLifeTime")
VALUES ('1NSb+2kcdKF44ut4iBu+dm6YLu6pakWapvxHtxqaPgMr5iRhox/HlhBerAZMILPjwnRtXms+zDfVTLCsao9nuw==',
        2,
        current_timestamp - interval '1 hour');

INSERT INTO roles("userId", role)
VALUES (1, 'admin');

INSERT INTO roles("userId")
VALUES (1),
       (2);