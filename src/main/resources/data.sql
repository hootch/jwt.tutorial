insert into "user" (userid, password, nickname, activated) values ('admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'admin', 1);
insert into "user" (userid, password, nickname, activated) values ('user', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'user', 1);
insert into "user" (userid, password, nickname, activated) values ('lego', '$2a$10$HJ6R02l9JLluFKI9XBbageZ6wkV.aoCeUh9PCvJGNqjnZUwVj30Si', 'lego', 1);
insert into "USER_AUTH" (id,auth1,auth2) values ('lego', 'ROLE_USER','ROLE_ADMIN');

-- insert into authority (authority_name) values ('ROLE_USER');
-- insert into authority (authority_name) values ('ROLE_ADMIN');
--
-- insert into user_authority (id, authority_name) values (1, 'ROLE_USER');
-- insert into user_authority (id, authority_name) values (1, 'ROLE_ADMIN');
-- insert into user_authority (id, authority_name) values (3, 'ROLE_USER');
-- insert into user_authority (id, authority_name) values (3, 'ROLE_ADMIN');
-- insert into user_authority (id, authority_name) values (2, 'ROLE_USER');