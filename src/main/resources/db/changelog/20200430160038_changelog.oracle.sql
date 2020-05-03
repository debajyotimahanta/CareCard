--liquibase formatted sql

--changeset AH0158671:1588280467569-1
CREATE SEQUENCE hibernate_sequence START WITH 1 INCREMENT BY 1;

--changeset AH0158671:1588280467569-2
CREATE TABLE business (id BLOB NOT NULL, create_date TIMESTAMP, modified_date TIMESTAMP, website VARCHAR2(255), address VARCHAR2(255), description VARCHAR2(255), external_ref_id VARCHAR2(255), formatted_phone_number VARCHAR2(255), international_phone_number VARCHAR2(255), latitude FLOAT(24), longitude FLOAT(24), name VARCHAR2(255), photo_attributions VARCHAR2(255), photo_reference VARCHAR2(255), photo_url VARCHAR2(255), state VARCHAR2(32) DEFAULT 'DRAFT', owner_id NUMBER(38, 0), CONSTRAINT businessPK PRIMARY KEY (id));

--changeset AH0158671:1588280467569-3
CREATE TABLE business_account_detail (id NUMBER(38, 0) NOT NULL, create_date TIMESTAMP, modified_date TIMESTAMP, access_token BLOB, external_ref_id VARCHAR2(255), refresh_token BLOB, CONSTRAINT business_account_detailPK PRIMARY KEY (id));

--changeset AH0158671:1588280467569-4
CREATE TABLE gift_card (id BLOB NOT NULL, create_date TIMESTAMP, modified_date TIMESTAMP, amount FLOAT(24), state VARCHAR2(32) DEFAULT 'Draft', item_id NUMBER(38, 0), CONSTRAINT gift_cardPK PRIMARY KEY (id));

--changeset AH0158671:1588280467569-5
CREATE TABLE item (id NUMBER(38, 0) NOT NULL, create_date TIMESTAMP, modified_date TIMESTAMP, quantity INTEGER, unit_price FLOAT(24), order_item_id NUMBER(38, 0), CONSTRAINT itemPK PRIMARY KEY (id));

--changeset AH0158671:1588280467569-6
CREATE TABLE order_detail (id BLOB NOT NULL, create_date TIMESTAMP, modified_date TIMESTAMP, contribution FLOAT(24), currency INTEGER, customer_email VARCHAR2(255), customer_mobile VARCHAR2(255), processing_fee FLOAT(24), session_id VARCHAR2(255), status VARCHAR2(32) DEFAULT 'PENDING', total FLOAT(24), CONSTRAINT order_detailPK PRIMARY KEY (id));

--changeset AH0158671:1588280467569-7
CREATE TABLE order_item (id NUMBER(38, 0) NOT NULL, create_date TIMESTAMP, modified_date TIMESTAMP, payment_state INTEGER, processing_id VARCHAR2(255), tip FLOAT(24), business_id BLOB, order_detail_id BLOB, CONSTRAINT order_itemPK PRIMARY KEY (id));

--changeset AH0158671:1588280467569-8
CREATE TABLE users (id NUMBER(38, 0) NOT NULL, create_date TIMESTAMP, modified_date TIMESTAMP, confirmation_token VARCHAR2(255), email VARCHAR2(255) NOT NULL, enabled NUMBER(1), linkname VARCHAR2(255) NOT NULL, phone_number VARCHAR2(255) NOT NULL, account NUMBER(38, 0), CONSTRAINT usersPK PRIMARY KEY (id));

--changeset AH0158671:1588280467569-9
ALTER TABLE users ADD CONSTRAINT idx_email UNIQUE (email);

--changeset AH0158671:1588280467569-10
ALTER TABLE business ADD CONSTRAINT idx_ext_ref_id UNIQUE (external_ref_id);

--changeset AH0158671:1588280467569-11
ALTER TABLE business_account_detail ADD CONSTRAINT idx_ext_ref_id UNIQUE (external_ref_id);

--changeset AH0158671:1588280467569-12
CREATE INDEX idx_business_name ON business(name);

--changeset AH0158671:1588280467569-13
CREATE INDEX idx_confirmation_token ON users(confirmation_token);

--changeset AH0158671:1588280467569-14
ALTER TABLE item ADD CONSTRAINT FK7iq7dnw3709wtxk9nxqpqy1bn FOREIGN KEY (order_item_id) REFERENCES order_item (id);

--changeset AH0158671:1588280467569-15
ALTER TABLE gift_card ADD CONSTRAINT FK8i4fpw53eossnyfbmlby29u9t FOREIGN KEY (item_id) REFERENCES item (id);

--changeset AH0158671:1588280467569-16
ALTER TABLE users ADD CONSTRAINT FK9gaay5pwggmdvmr4hx8s0bx4e FOREIGN KEY (account) REFERENCES business_account_detail (id);

--changeset AH0158671:1588280467569-17
ALTER TABLE order_item ADD CONSTRAINT FKetp2gbd2bsv1qqq4bp1b4p4ds FOREIGN KEY (order_detail_id) REFERENCES order_detail (id);

--changeset AH0158671:1588280467569-18
ALTER TABLE business ADD CONSTRAINT FKgydlolm5wjr0qpxb1jvv3ki4a FOREIGN KEY (owner_id) REFERENCES users (id);

--changeset AH0158671:1588280467569-19
ALTER TABLE order_item ADD CONSTRAINT FKtkhlf7l6tfbm6k0qkhu51ki97 FOREIGN KEY (business_id) REFERENCES business (id);

