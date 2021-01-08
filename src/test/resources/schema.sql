DROP DATABASE urlshortener;
CREATE DATABASE urlshortener;
USE urlshortener;

CREATE TABLE IF NOT EXISTS role
(
    ID INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ROLE_NAME VARCHAR(20)
    );

CREATE TABLE IF NOT EXISTS user
(
    ID		 INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,                               -- KEY
    USERNAME VARCHAR(15) UNIQUE,                                                    -- Username
    PASSWORD VARCHAR(50),                                                           -- Password
    ROLE     INT UNSIGNED,                                                          -- ROLE ID
    FOREIGN KEY (ROLE)	REFERENCES role (ID) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS shorturl
(
    HASH    VARCHAR(30) PRIMARY KEY, -- Key
    TARGET  VARCHAR(1024),           -- Original URL
    SPONSOR VARCHAR(1024),           -- Sponsor URL
    CREATED TIMESTAMP DEFAULT CURRENT_TIMESTAMP,               -- Creation date
    EXPIRATION DATE,                 -- Expiration date
    OWNER   INT UNSIGNED,           -- User id
    MODE    INTEGER,                 -- Redirect mode
    SAFE    BOOLEAN DEFAULT false,                 -- Safe target
    IP      VARCHAR(20),             -- IP
    COUNTRY VARCHAR(50),              -- Country
    FOREIGN KEY (OWNER)	REFERENCES user (ID) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS click
(
    ID		 INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    CREATED  TIMESTAMP,
    REFERRER VARCHAR(1024),
    BROWSER  VARCHAR(50),
    PLATFORM VARCHAR(50),
    IP       VARCHAR(20),
    COUNTRY  VARCHAR(50),
    HASH     VARCHAR(30) NOT NULL,
    FOREIGN KEY (HASH)
    REFERENCES shorturl (HASH) ON DELETE CASCADE

    );

INSERT INTO role(ROLE_NAME) VALUES ('ROLE_ADMIN');
INSERT INTO role(ROLE_NAME) VALUES ('ROLE_USER');
INSERT INTO user(USERNAME, PASSWORD, ROLE) VALUES ('admin','1234',1);
INSERT INTO user(USERNAME, PASSWORD, ROLE) VALUES ('user','1234',2);
INSERT INTO shorturl (`HASH`, `TARGET`, `SPONSOR`, `CREATED`, `EXPIRATION`, `OWNER`, `MODE`, `SAFE`, `IP`, `COUNTRY`) VALUES
('09bb6428', 'https://www.live.com', NULL, '2021-01-06 08:00:00', '1970-01-01', 2, 307, 1, '', NULL),
('0b4f261e', 'https://www.reddit.com', NULL, '2021-01-06 08:00:00', '1970-01-01', 2, 307, 1, '', NULL);



