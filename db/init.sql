SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;
-- COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';

SET default_tablespace = '';
SET default_with_oids = false;

CREATE TABLE appuserrole (
    role text PRIMARY KEY
);


CREATE TABLE appuser (
    id integer PRIMARY KEY,
    name text UNIQUE,
    email text,
    displayname text,
    password text,
    created timestamp without time zone DEFAULT now() NOT NULL,
    activation text,
    activated timestamp without time zone,
    role text DEFAULT 'editor'::text NOT NULL,
    signin boolean DEFAULT true NOT NULL,
    contact boolean DEFAULT false NOT NULL,
    FOREIGN KEY (role) REFERENCES appuserrole(role)
);
CREATE SEQUENCE appuser_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE appuser_id_seq OWNED BY appuser.id;


CREATE TABLE archobjtype (
    type text PRIMARY KEY
);


CREATE TABLE archobj (
    id integer PRIMARY KEY,
    extid text UNIQUE,
    extid2 integer,
    label text,
    summary text,
    note text,
    type text NOT NULL,
    created date DEFAULT now() NOT NULL,
    version integer,
    locked timestamp without time zone,
    deleted timestamp without time zone,
    owner integer NOT NULL,
    pubts timestamp without time zone,
    FOREIGN KEY (owner) REFERENCES appuser(id),
    FOREIGN KEY (type) REFERENCES archobjtype(type)
);
CREATE SEQUENCE archobj_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE archobj_id_seq OWNED BY archobj.id;


CREATE TABLE archobjversion (
    id integer PRIMARY KEY,
    oid integer NOT NULL,
    owner integer NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    comment text,
    document text NOT NULL,
    parent integer,
    UNIQUE (id, oid),
    FOREIGN KEY (oid) REFERENCES archobj(id),
    FOREIGN KEY (owner) REFERENCES appuser(id),
    FOREIGN KEY (parent, oid) REFERENCES archobjversion(id, oid)
);
CREATE SEQUENCE archobjversion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE archobjversion_id_seq OWNED BY archobjversion.id;
ALTER TABLE ONLY archobj
    ADD CONSTRAINT archobj_version_fkey FOREIGN KEY (version, id) REFERENCES archobjversion(id, oid);


CREATE TABLE person (
    id integer PRIMARY KEY,
    personid integer,
    firstname text,
    lastname text,
    preflabel text,
    practicedinqueensland boolean NOT NULL,
    architect boolean NOT NULL,
    FOREIGN KEY (id) REFERENCES archobj(id)
);


CREATE TABLE storagetype (
    type text PRIMARY KEY
);


CREATE TABLE compobjtype (
    type text PRIMARY KEY
);


CREATE TABLE structure (
    id integer PRIMARY KEY,
    structureid integer,
    location text,
    completion date,
    completionpd smallint,
    lat real,
    lng real,
    australian boolean NOT NULL,
    demolished boolean NOT NULL,
    FOREIGN KEY (id) REFERENCES archobj(id)
);


CREATE TABLE text (
    id integer PRIMARY KEY,
    mediaid integer,
    published date,
    pages text,
    periodical text,
    volume text,
    issue text,
    authors text,
    FOREIGN KEY (id) REFERENCES archobj(id)
);


CREATE TABLE buildingtypologytype (
    type text PRIMARY KEY
);


CREATE TABLE buildingtypology (
    structure integer NOT NULL,
    typology text NOT NULL,
    PRIMARY KEY (structure, typology),
    FOREIGN KEY (structure) REFERENCES structure(id),
    FOREIGN KEY (typology) REFERENCES buildingtypologytype(type)
);


CREATE TABLE compobj (
    id integer PRIMARY KEY,
    extid text,
    label text,
    type text NOT NULL,
    created date DEFAULT now() NOT NULL,
    owner integer NOT NULL,
    FOREIGN KEY (owner) REFERENCES appuser(id),
    FOREIGN KEY (type) REFERENCES compobjtype(type)
);
CREATE SEQUENCE compobj_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE compobj_id_seq OWNED BY compobj.id;


CREATE TABLE compobjstructure (
    compobj integer NOT NULL,
    structure integer NOT NULL,
    PRIMARY KEY (compobj, structure),
    FOREIGN KEY (compobj) REFERENCES compobj(id),
    FOREIGN KEY (structure) REFERENCES structure(id)
);


CREATE TABLE firm (
    id integer PRIMARY KEY,
    firmid integer,
    australian boolean NOT NULL,
    startdate date,
    enddate date,
    precededby integer,
    succeededby integer,
    FOREIGN KEY (id) REFERENCES archobj(id),
    FOREIGN KEY (precededby) REFERENCES firm(id),
    FOREIGN KEY (succeededby) REFERENCES firm(id)
);


CREATE TABLE interview (
    id integer PRIMARY KEY,
    interviewid integer,
    location text,
    interviewdate date,
    FOREIGN KEY (id) REFERENCES archobj(id)
);


CREATE TABLE utterance (
    id integer PRIMARY KEY,
    interview integer NOT NULL,
    speaker integer NOT NULL,
    "time" integer NOT NULL,
    transcript text,
    FOREIGN KEY (speaker) REFERENCES person(id),
    FOREIGN KEY (interview) REFERENCES interview(id)
);
CREATE SEQUENCE utterance_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE utterance_id_seq OWNED BY utterance.id;


CREATE TABLE interviewee (
    interview integer NOT NULL,
    interviewee integer NOT NULL,
    PRIMARY KEY (interview, interviewee),
    FOREIGN KEY (interview) REFERENCES interview(id),
    FOREIGN KEY (interviewee) REFERENCES person(id)
);


CREATE TABLE interviewer (
    interview integer NOT NULL,
    interviewer integer NOT NULL,
    PRIMARY KEY (interview, interviewer),
    FOREIGN KEY (interview) REFERENCES interview(id),
    FOREIGN KEY (interviewer) REFERENCES person(id)
);


CREATE TABLE mediatype (
    type text PRIMARY KEY
);


CREATE TABLE media (
    id integer PRIMARY KEY,
    mediaid integer,
    fileid integer,
    extid text,
    title text,
    description text,
    creator text,
    created date,
    identifier text,
    location text,
    projectnumber text,
    rights text,
    filename text,
    path text NOT NULL,
    storagetype text NOT NULL,
    mimetype text,
    filesize bigint,
    hash text,
    uploaded timestamp without time zone DEFAULT now() NOT NULL,
    preferred timestamp without time zone,
    deleted timestamp without time zone,
    type text NOT NULL,
    depicts integer,
    owner integer NOT NULL,
    FOREIGN KEY (depicts) REFERENCES archobj(id),
    FOREIGN KEY (owner) REFERENCES appuser(id),
    FOREIGN KEY (storagetype) REFERENCES storagetype(type),
    FOREIGN KEY (type) REFERENCES mediatype(type)
);
CREATE SEQUENCE media_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE media_id_seq OWNED BY media.id;


CREATE TABLE associatedmedia (
    associated integer NOT NULL,
    media integer NOT NULL,
    PRIMARY KEY (associated, media),
    FOREIGN KEY (associated) REFERENCES archobj(id),
    FOREIGN KEY (media) REFERENCES media(id)
);


CREATE TABLE relationshipsource (
    type text PRIMARY KEY
);


CREATE TABLE relationshiptype (
    type text PRIMARY KEY,
    label text
);


CREATE TABLE relationship (
    id integer PRIMARY KEY,
    extid text UNIQUE,
    subject integer NOT NULL,
    object integer NOT NULL,
    type text NOT NULL,
    source text,
    note text,
    fromyear integer,
    untilyear integer,
    created timestamp without time zone DEFAULT now() NOT NULL,
    owner integer NOT NULL,
    FOREIGN KEY (object) REFERENCES archobj(id),
    FOREIGN KEY (owner) REFERENCES appuser(id),
    FOREIGN KEY (source) REFERENCES relationshipsource(type),
    FOREIGN KEY (subject) REFERENCES archobj(id),
    FOREIGN KEY (type) REFERENCES relationshiptype(type)
);
CREATE SEQUENCE relationship_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE relationship_id_seq OWNED BY relationship.id;


CREATE TABLE relationshiplog (
    id integer PRIMARY KEY,
    trxid text NOT NULL,
    owner integer NOT NULL,
    created timestamp without time zone NOT NULL,
    relationship integer,
    action text NOT NULL,
    field text NOT NULL,
    oidfrom integer,
    oidto integer,
    sourcedocument text NOT NULL,
    FOREIGN KEY (oidfrom) REFERENCES archobj(id),
    FOREIGN KEY (oidto) REFERENCES archobj(id),
    FOREIGN KEY (owner) REFERENCES appuser(id),
    FOREIGN KEY (relationship) REFERENCES relationship(id) ON DELETE SET NULL
);
CREATE SEQUENCE relationshiplog_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE relationshiplog_id_seq OWNED BY relationshiplog.id;


CREATE TABLE interviewrelationship (
    id integer PRIMARY KEY,
    interview integer NOT NULL,
    utterance integer NOT NULL,
    FOREIGN KEY (id) REFERENCES relationship(id),
    FOREIGN KEY (interview) REFERENCES interview(id),
    FOREIGN KEY (utterance) REFERENCES utterance(id)
);


CREATE TABLE articlerelationship (
    id integer NOT NULL,
    article integer NOT NULL,
    page text,
    CONSTRAINT articlerelationship_pkey PRIMARY KEY (id),
    CONSTRAINT articlerelationship_id_fkey FOREIGN KEY (id) REFERENCES relationship(id),
    CONSTRAINT articlerelationship_article_fkey FOREIGN KEY (article) REFERENCES text(id)
);


CREATE TABLE session (
    session text PRIMARY KEY,
    appuser integer NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    FOREIGN KEY (appuser) REFERENCES appuser(id)
);


CREATE TABLE thumbnail (
    id integer PRIMARY KEY,
    media integer NOT NULL,
    width integer NOT NULL,
    height integer NOT NULL,
    path text NOT NULL,
    hash text,
    created timestamp without time zone DEFAULT now() NOT NULL,
    mimetype text NOT NULL,
    filesize integer NOT NULL,
    thumbnail bytea,
    failed boolean DEFAULT false NOT NULL,
    failmsg text,
    UNIQUE (media, width, height),
    FOREIGN KEY (media) REFERENCES media(id)
);
CREATE SEQUENCE thumbnail_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE thumbnail_id_seq OWNED BY thumbnail.id;


CREATE TABLE timelineevent (
    id integer PRIMARY KEY,
    compobj integer NOT NULL,
    archobj integer,
    label text,
    note text,
    fromyear integer,
    untilyear integer,
    FOREIGN KEY (archobj) REFERENCES archobj(id),
    FOREIGN KEY (compobj) REFERENCES compobj(id)
);
CREATE SEQUENCE timelineevent_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE timelineevent_id_seq OWNED BY timelineevent.id;


CREATE TABLE wordcloud (
    id integer PRIMARY KEY,
    compobj integer NOT NULL,
    label text,
    text text,
    FOREIGN KEY (compobj) REFERENCES compobj(id)
);
CREATE SEQUENCE wordcloud_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE wordcloud_id_seq OWNED BY wordcloud.id;


ALTER TABLE ONLY appuser ALTER COLUMN id SET DEFAULT nextval('appuser_id_seq'::regclass);
ALTER TABLE ONLY archobj ALTER COLUMN id SET DEFAULT nextval('archobj_id_seq'::regclass);
ALTER TABLE ONLY archobjversion ALTER COLUMN id SET DEFAULT nextval('archobjversion_id_seq'::regclass);
ALTER TABLE ONLY compobj ALTER COLUMN id SET DEFAULT nextval('compobj_id_seq'::regclass);
ALTER TABLE ONLY media ALTER COLUMN id SET DEFAULT nextval('media_id_seq'::regclass);
ALTER TABLE ONLY relationship ALTER COLUMN id SET DEFAULT nextval('relationship_id_seq'::regclass);
ALTER TABLE ONLY relationshiplog ALTER COLUMN id SET DEFAULT nextval('relationshiplog_id_seq'::regclass);
ALTER TABLE ONLY thumbnail ALTER COLUMN id SET DEFAULT nextval('thumbnail_id_seq'::regclass);
ALTER TABLE ONLY timelineevent ALTER COLUMN id SET DEFAULT nextval('timelineevent_id_seq'::regclass);
ALTER TABLE ONLY utterance ALTER COLUMN id SET DEFAULT nextval('utterance_id_seq'::regclass);
ALTER TABLE ONLY wordcloud ALTER COLUMN id SET DEFAULT nextval('wordcloud_id_seq'::regclass);


COPY appuserrole (role) FROM stdin;
reader
editor
admin
\.

COPY appuser (id, name, email, displayname, password, created, activation, activated, role, signin, contact) FROM stdin;
1	admin	\N	qldarch@uq.edu.au	$shiro1$SHA-256$500000$707jugFV5pWv7JfhRZn4Eg==$cliyB4AwBEpJvzT2i1Yjc4UP3lfni/vGh6HIHtHV/c0=	2014-01-01 12:00:00	\N	2014-01-01 12:00:00	admin	t	f
\.
SELECT pg_catalog.setval('appuser_id_seq', 1, true);

COPY archobj (id, extid, extid2, label, summary, note, type, created, version, locked, deleted, owner) FROM stdin;
\.
SELECT pg_catalog.setval('archobj_id_seq', 1, false);

COPY archobjtype (type) FROM stdin;
person
structure
firm
interview
article
award
event
organisation
education
government
place
publication
topic
\.


COPY archobjversion (id, oid, owner, created, comment, document, parent) FROM stdin;
\.
SELECT pg_catalog.setval('archobjversion_id_seq', 1, false);

COPY associatedmedia (associated, media) FROM stdin;
\.

COPY buildingtypology (structure, typology) FROM stdin;
\.

COPY buildingtypologytype (type) FROM stdin;
Health care facilities
Government buildings
Religious buildings
Recreation and sports facilities
Industrial buildings
Transport infrastructure
Commercial buildings
Educational facilities
Dwellings
High-Rise
\.

COPY compobj (id, extid, label, type, created, owner) FROM stdin;
\.
SELECT pg_catalog.setval('compobj_id_seq', 1, false);

COPY compobjstructure (compobj, structure) FROM stdin;
\.

COPY compobjtype (type) FROM stdin;
timeline
map
wordcloud
\.

COPY firm (id, firmid, australian, startdate, enddate, precededby, succeededby) FROM stdin;
\.

COPY interview (id, interviewid, location, interviewdate) FROM stdin;
\.

COPY interviewee (interview, interviewee) FROM stdin;
\.

COPY interviewer (interview, interviewer) FROM stdin;
\.

COPY interviewrelationship (id, interview, utterance) FROM stdin;
\.

COPY media (id, mediaid, fileid, extid, title, description, creator, created, identifier, location, projectnumber, rights, filename, path, storagetype, mimetype, filesize, hash, uploaded, preferred, deleted, type, depicts, owner) FROM stdin;
\.
SELECT pg_catalog.setval('media_id_seq', 1, false);


COPY mediatype (type) FROM stdin;
Article
Image
LineDrawing
Photograph
Portrait
Spreadsheet
Transcript
Text
Youtube
Audio
Video
\.

COPY person (id, personid, firstname, lastname, preflabel, practicedinqueensland, architect) FROM stdin;
\.

COPY relationship (id, extid, subject, object, type, source, note, fromyear, untilyear, created, owner) FROM stdin;
\.
SELECT pg_catalog.setval('relationship_id_seq', 1, false);

COPY relationshiplog (id, trxid, owner, created, relationship, action, field, oidfrom, oidto, sourcedocument) FROM stdin;
\.
SELECT pg_catalog.setval('relationshiplog_id_seq', 1, false);

COPY relationshipsource (type) FROM stdin;
article
interview
structure
firm
\.

COPY relationshiptype (type, label) FROM stdin;
Attended	attended
Authored	authored
Awarded	awarded
Became	became
ClientOf	client of
CollaboratedWith	collaborated with
DesignedBy	designed by
Employment	employed by
Founded	founded
InfluencedBy	influenced by
KnewOf	knew of
KnewProfessionally	knew professionally
KnewSocially	knew socially
MentoredBy	mentored by
MergedWith	merged with
PartnerOf	partner of
Read	reads
Reference	references
RelatedTo	related to
StudiedAt	studied at
StudiedWith	studied with
TaughtAt	taught at
TaughtBy	taught by
TravelledTo	travelled to
WasInfluenceBy	was influenced by
WorkedOn	worked on
WorkedWith	worked with
WroteFor	wrote for
\.

COPY session (session, appuser, created) FROM stdin;
\.

COPY storagetype (type) FROM stdin;
Local
ObjectStore
External
\.

COPY structure (id, structureid, location, completion, lat, lng, australian, demolished) FROM stdin;
\.

COPY text (id, mediaid, published, pages, periodical, volume, issue, authors) FROM stdin;
\.

COPY thumbnail (id, media, width, height, path, hash, created, mimetype, filesize, thumbnail, failed, failmsg) FROM stdin;
\.
SELECT pg_catalog.setval('thumbnail_id_seq', 1, false);

COPY timelineevent (id, compobj, archobj, label, note, fromyear, untilyear) FROM stdin;
\.
SELECT pg_catalog.setval('timelineevent_id_seq', 1, false);

COPY utterance (id, interview, speaker, "time", transcript) FROM stdin;
\.
SELECT pg_catalog.setval('utterance_id_seq', 1, false);

COPY wordcloud (id, compobj, label, text) FROM stdin;
\.

SELECT pg_catalog.setval('wordcloud_id_seq', 1, false);

