CREATE DATABASE idm;


create table idm.token_status(
id INT primary key,
value VARCHAR(32) not null
);


create table idm.user_status(
id INT primary key,
value VARCHAR(32) not null
);


create table idm.role(
id INT primary key,
name VARCHAR(32) not null,
description VARCHAR(128) not null,
precedence INT not null
);

create table idm.user(
id INT PRIMARY KEY auto_increment,
email VARCHAR(32) NOT NULL UNIQUE,
user_status_id INT NOT NULL,
salt CHAR(8) NOT NULL,
hashed_password CHAR(88) NOT NULL,
FOREIGN KEY (user_status_id) REFERENCES idm.user_status (id) ON UPDATE CASCADE ON DELETE RESTRICT
);

create table idm.refresh_token(
id	INT	NOT NULL PRIMARY KEY AUTO_INCREMENT,
token	CHAR(36)	NOT NULL UNIQUE,
user_id	INT	NOT NULL,
token_status_id	INT	NOT NULL,
expire_time	TIMESTAMP	NOT NULL,
max_life_time	TIMESTAMP	NOT NULL,
FOREIGN KEY (user_id) REFERENCES idm.user (id) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY (token_status_id) REFERENCES idm.token_status (id) ON UPDATE CASCADE ON DELETE RESTRICT
);


create table idm.user_role(
user_id	INT	NOT NULL,
role_id	INT	NOT NULL,
PRIMARY KEY (user_id, role_id),
FOREIGN KEY (user_id) REFERENCES idm.user (id) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY (role_id) REFERENCES idm.role (id) ON UPDATE CASCADE ON DELETE RESTRICT
);
