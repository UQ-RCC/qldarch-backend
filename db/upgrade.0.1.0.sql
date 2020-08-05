ALTER TABLE structure ADD COLUMN completionpd smallint;
UPDATE structure set completionpd=365 WHERE extract(day from completion) = 1 and extract(month from completion) = 1;

INSERT INTO relationshipsource (type) VALUES ('article') ON CONFLICT (type) DO NOTHING;

CREATE TABLE articlerelationship (
    id integer NOT NULL,
    article integer NOT NULL,
    page text,
    CONSTRAINT articlerelationship_pkey PRIMARY KEY (id),
    CONSTRAINT articlerelationship_id_fkey FOREIGN KEY (id) REFERENCES relationship(id),
    CONSTRAINT articlerelationship_article_fkey FOREIGN KEY (article) REFERENCES text(id)
);

ALTER TABLE archobj ADD COLUMN pubts timestamp without time zone;
UPDATE archobj set pubts=created;
