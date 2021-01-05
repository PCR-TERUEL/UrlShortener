-- Clean database

DROP TABLE CLICK IF EXISTS;
DROP TABLE SHORTURL IF EXISTS;
DROP TABLE USER IF EXISTS;
DROP TABLE ROLE IF EXISTS;

-- ShortURL

CREATE TABLE SHORTURL
(
    HASH    VARCHAR(30) PRIMARY KEY, -- Key
    TARGET  VARCHAR(1024),           -- Original URL
    SPONSOR VARCHAR(1024),           -- Sponsor URL
    CREATED TIMESTAMP,               -- Creation date
    EXPIRATION DATE,                 -- Expiration date
    OWNER   BIGINT,                  -- User id
    MODE    INTEGER,                 -- Redirect mode
    SAFE    BOOLEAN DEFAULT false,                 -- Safe target
    IP      VARCHAR(20),             -- IP
    COUNTRY VARCHAR(50)              -- Country
);

-- Click

CREATE TABLE CLICK
(
    ID       BIGINT IDENTITY,                                             -- KEY
    HASH     VARCHAR(10), -- Foreing key
    CREATED  TIMESTAMP,                                                   -- Creation date
    REFERRER VARCHAR(1024),                                               -- Traffic origin
    BROWSER  VARCHAR(50),                                                 -- Browser
    PLATFORM VARCHAR(50),                                                 -- Platform
    IP       VARCHAR(20),                                                 -- IP
    COUNTRY  VARCHAR(50)                                                  -- Country
);

-- USER

CREATE TABLE USER
(
    ID       BIGINT IDENTITY PRIMARY KEY,                                 -- KEY
    USERNAME VARCHAR(15) UNIQUE,                                          -- Username
    PASSWORD VARCHAR(50),                                                 -- Password
    ROLE     INT                                                          -- ROLE ID
);

CREATE TABLE ROLE
(
    ID INT IDENTITY PRIMARY KEY,
    ROLE_NAME VARCHAR(20)
);

ALTER TABLE SHORTURL ADD FOREIGN KEY (OWNER) REFERENCES USER (ID) ON DELETE CASCADE;
ALTER TABLE USER ADD FOREIGN KEY (ROLE) REFERENCES ROLE (ID) ON DELETE CASCADE;
ALTER TABLE CLICK ADD FOREIGN KEY (HASH) REFERENCES SHORTURL (HASH) ON DELETE CASCADE;
INSERT INTO ROLE(ROLE_NAME) VALUES ('ROLE_ADMIN');
INSERT INTO ROLE(ROLE_NAME) VALUES ('ROLE_USER');
INSERT INTO USER(USERNAME, PASSWORD, ROLE) VALUES ('admin','1234',0);
INSERT INTO USER(USERNAME, PASSWORD, ROLE) VALUES ('user','1234',1);
INSERT INTO USER(USERNAME, PASSWORD, ROLE) VALUES ('user2','1234',1);



