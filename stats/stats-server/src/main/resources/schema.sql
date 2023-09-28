DROP TABLE IF EXISTS hits;

CREATE TABLE IF NOT EXISTS hits
(
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   app VARCHAR(255),
   uri VARCHAR(512),
   ip VARCHAR(45),
   timestamp TIMESTAMP WITHOUT TIME ZONE,
   CONSTRAINT pk_hits PRIMARY KEY (id)
);