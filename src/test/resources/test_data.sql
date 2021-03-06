INSERT
INTO user (username, name, email)
VALUES ('user1', 'user one', 'user1@hhu.de'),
       ('user2', 'user two', 'user2@hhu.de'),
       ('user3', 'user three', 'user3@hhu.de'),
       ('studentin', 'stud0@gmail.com', 'Bernhard Zimmermann');

INSERT
INTO forum (id, title, description)
VALUES (1, 'forum1', 'forum one'),
       (2, 'forum2', 'forum two'),
       (3, 'forum3', 'forum three');

INSERT
INTO topic (id, forum_id, title, description, moderated, anonymous)
VALUES (1, 2, 'topic1', 'topic one', 0, 1),
       (2, 2, 'topic2', 'topic two', 1, 0),
       (3, 3, 'topic3', 'topic three', 0, 1);

INSERT
INTO thread (id, forum_id, author_username, topic_id, title, description, moderated, anonymous, visible)
VALUES (1, 2, 'user2', 2, 'thread1', 'content one', 1, 0, 1),
       (2, 2, 'user3', 2, 'thread2', 'content two', 1, 1, 0),
       (3, 3, 'user3', 3, 'thread3', 'content four', 0, 1, 0);

INSERT
INTO post (id, forum_id, thread_id, author_username, text, visible, anonymous, date_time)
VALUES (1, 2, 1, 'user2', 'content one', 1, 0, '2020-01-01 00:00:00'),
       (2, 2, 2, 'user2', 'content two', 0, 1, '2020-01-01 00:00:00'),
       (3, 2, 2, 'user3', 'content three', 1, 0, '2020-01-01 00:00:00'),
       (4, 3, 3, 'user3', 'content four', 0, 1, '2020-01-01 00:00:00');

INSERT
INTO roles (username, role, forum_id)
VALUES ('user2', 'ADMIN', 2),
       ('user3', 'STUDENT', 2),
       ('user3', 'ADMIN', 3);

INSERT
INTO user_forum (username, id)
VALUES ('user2', 2),
       ('user3', 2),
       ('user3', 3);