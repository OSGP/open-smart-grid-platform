--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

-- COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: device; Type: TABLE; Schema: public; Owner: osp_admin; Tablespace: 
--

CREATE TABLE device (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    device_identification character varying(40) NOT NULL,
    device_type character varying(255),
    device_uid character varying(255),
    has_schedule boolean NOT NULL,
    is_activated boolean NOT NULL,
    network_address oid
);


ALTER TABLE public.device OWNER TO osp_admin;

--
-- Name: device_authorization; Type: TABLE; Schema: public; Owner: osp_admin; Tablespace: 
--

CREATE TABLE device_authorization (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    function_group integer,
    device bigint,
    organisation bigint
);


ALTER TABLE public.device_authorization OWNER TO osp_admin;

--
-- Name: device_authorization_id_seq; Type: SEQUENCE; Schema: public; Owner: osp_admin
--

CREATE SEQUENCE device_authorization_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.device_authorization_id_seq OWNER TO osp_admin;

--
-- Name: device_authorization_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: osp_admin
--

ALTER SEQUENCE device_authorization_id_seq OWNED BY device_authorization.id;


--
-- Name: device_id_seq; Type: SEQUENCE; Schema: public; Owner: osp_admin
--

CREATE SEQUENCE device_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.device_id_seq OWNER TO osp_admin;

--
-- Name: device_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: osp_admin
--

ALTER SEQUENCE device_id_seq OWNED BY device.id;


--
-- Name: event; Type: TABLE; Schema: public; Owner: osp_admin; Tablespace: 
--

CREATE TABLE event (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    description character varying(255) NOT NULL,
    event integer NOT NULL,
    index integer NOT NULL,
    device bigint
);


ALTER TABLE public.event OWNER TO osp_admin;

--
-- Name: event_id_seq; Type: SEQUENCE; Schema: public; Owner: osp_admin
--

CREATE SEQUENCE event_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.event_id_seq OWNER TO osp_admin;

--
-- Name: event_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: osp_admin
--

ALTER SEQUENCE event_id_seq OWNED BY event.id;


--
-- Name: organisation; Type: TABLE; Schema: public; Owner: osp_admin; Tablespace: 
--

CREATE TABLE organisation (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    function_group integer NOT NULL,
    name character varying(255) NOT NULL,
    organisation_identification character varying(40) NOT NULL
);


ALTER TABLE public.organisation OWNER TO osp_admin;

--
-- Name: organisation_id_seq; Type: SEQUENCE; Schema: public; Owner: osp_admin
--

CREATE SEQUENCE organisation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.organisation_id_seq OWNER TO osp_admin;

--
-- Name: organisation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: osp_admin
--

ALTER SEQUENCE organisation_id_seq OWNED BY organisation.id;


--
-- Name: oslp_log_item; Type: TABLE; Schema: public; Owner: osp_admin; Tablespace: 
--

CREATE TABLE oslp_log_item (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    decoded_message character varying(8000),
    device_identification character varying(255),
    device_uid character varying(255),
    encoded_message character varying(8000),
    incoming boolean NOT NULL,
    organisation_identification character varying(255),
    valid boolean NOT NULL
);


ALTER TABLE public.oslp_log_item OWNER TO osp_admin;

--
-- Name: oslp_log_item_id_seq; Type: SEQUENCE; Schema: public; Owner: osp_admin
--

CREATE SEQUENCE oslp_log_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.oslp_log_item_id_seq OWNER TO osp_admin;

--
-- Name: oslp_log_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: osp_admin
--

ALTER SEQUENCE oslp_log_item_id_seq OWNED BY oslp_log_item.id;


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: osp_admin
--

ALTER TABLE ONLY device ALTER COLUMN id SET DEFAULT nextval('device_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: osp_admin
--

ALTER TABLE ONLY device_authorization ALTER COLUMN id SET DEFAULT nextval('device_authorization_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: osp_admin
--

ALTER TABLE ONLY event ALTER COLUMN id SET DEFAULT nextval('event_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: osp_admin
--

ALTER TABLE ONLY organisation ALTER COLUMN id SET DEFAULT nextval('organisation_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: osp_admin
--

ALTER TABLE ONLY oslp_log_item ALTER COLUMN id SET DEFAULT nextval('oslp_log_item_id_seq'::regclass);


--
-- Name: device_authorization_pkey; Type: CONSTRAINT; Schema: public; Owner: osp_admin; Tablespace: 
--

ALTER TABLE ONLY device_authorization
    ADD CONSTRAINT device_authorization_pkey PRIMARY KEY (id);


--
-- Name: device_device_identification_key; Type: CONSTRAINT; Schema: public; Owner: osp_admin; Tablespace: 
--

ALTER TABLE ONLY device
    ADD CONSTRAINT device_device_identification_key UNIQUE (device_identification);


--
-- Name: device_device_uid_key; Type: CONSTRAINT; Schema: public; Owner: osp_admin; Tablespace: 
--

ALTER TABLE ONLY device
    ADD CONSTRAINT device_device_uid_key UNIQUE (device_uid);


--
-- Name: device_pkey; Type: CONSTRAINT; Schema: public; Owner: osp_admin; Tablespace: 
--

ALTER TABLE ONLY device
    ADD CONSTRAINT device_pkey PRIMARY KEY (id);


--
-- Name: event_pkey; Type: CONSTRAINT; Schema: public; Owner: osp_admin; Tablespace: 
--

ALTER TABLE ONLY event
    ADD CONSTRAINT event_pkey PRIMARY KEY (id);


--
-- Name: organisation_organisation_identification_key; Type: CONSTRAINT; Schema: public; Owner: osp_admin; Tablespace: 
--

ALTER TABLE ONLY organisation
    ADD CONSTRAINT organisation_organisation_identification_key UNIQUE (organisation_identification);


--
-- Name: organisation_pkey; Type: CONSTRAINT; Schema: public; Owner: osp_admin; Tablespace: 
--

ALTER TABLE ONLY organisation
    ADD CONSTRAINT organisation_pkey PRIMARY KEY (id);


--
-- Name: oslp_log_item_pkey; Type: CONSTRAINT; Schema: public; Owner: osp_admin; Tablespace: 
--

ALTER TABLE ONLY oslp_log_item
    ADD CONSTRAINT oslp_log_item_pkey PRIMARY KEY (id);


--
-- Name: fk5c6729a99350fa9; Type: FK CONSTRAINT; Schema: public; Owner: osp_admin
--

ALTER TABLE ONLY event
    ADD CONSTRAINT fk5c6729a99350fa9 FOREIGN KEY (device) REFERENCES device(id);


--
-- Name: fk7e0c025092a7e2f1; Type: FK CONSTRAINT; Schema: public; Owner: osp_admin
--

ALTER TABLE ONLY device_authorization
    ADD CONSTRAINT fk7e0c025092a7e2f1 FOREIGN KEY (organisation) REFERENCES organisation(id);


--
-- Name: fk7e0c025099350fa9; Type: FK CONSTRAINT; Schema: public; Owner: osp_admin
--

ALTER TABLE ONLY device_authorization
    ADD CONSTRAINT fk7e0c025099350fa9 FOREIGN KEY (device) REFERENCES device(id);


--
-- PostgreSQL database dump complete
--

