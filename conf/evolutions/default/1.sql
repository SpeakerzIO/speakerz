# --- !Ups

CREATE TABLE Speakerz (
    id varchar(255) NOT NULL,
    document json NOT NULL,
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE Speakerz;