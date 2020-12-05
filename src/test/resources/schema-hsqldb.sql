-- Clean database

DROP TABLE CLICK IF EXISTS;
DROP TABLE SHORTURL IF EXISTS;
DROP TABLE USER IF EXISTS;
DROP TABLE USER_ROLES IF EXISTS;

-- ShortURL

CREATE TABLE SHORTURL
(
    HASH    VARCHAR(30) PRIMARY KEY, -- Key
    TARGET  VARCHAR(1024),           -- Original URL
    SPONSOR VARCHAR(1024),           -- Sponsor URL
    CREATED TIMESTAMP,               -- Creation date
    EXPIRATION DATE,                 -- Expiration date
    OWNER   BIGINT,           -- User id
    MODE    INTEGER,                 -- Redirect mode
    SAFE    BOOLEAN,                 -- Safe target
    IP      VARCHAR(20),             -- IP
    COUNTRY VARCHAR(50)              -- Country
);

-- Click

CREATE TABLE CLICK
(
    ID       BIGINT IDENTITY,                                             -- KEY
    HASH     VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES SHORTURL (HASH), -- Foreing key
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
    PASSWORD VARCHAR(50)                                                  -- Password
);

CREATE TABLE USER_ROLE
(
    ID  BIGINT IDENTITY PRIMARY KEY,                                        -- KEY
    USERNAME VARCHAR(15),                                                   -- Username
    ROLE VARCHAR(45) NOT NULL                                               -- Role
);

ALTER TABLE SHORTURL ADD FOREIGN KEY (OWNER) REFERENCES USER (ID);
ALTER TABLE USER_ROLE ADD FOREIGN KEY (USERNAME) REFERENCES USER (USERNAME);
INSERT INTO USER (USERNAME, PASSWORD) VALUES ('Admin','1234');
INSERT INTO USER_ROLE (USERNAME, ROLE) VALUES ('Admin','ADMIN');
