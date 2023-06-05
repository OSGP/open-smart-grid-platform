-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

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

-- CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


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
    actual_link_type integer,
    device_identification character varying(255) NOT NULL,
    device_type character varying(255) NOT NULL,
    device_uid character varying(255) NOT NULL,
    dim_value integer,
    event_notifications integer,
    ip_address character varying(255) NOT NULL,
    light_on boolean NOT NULL,
    light_type integer,
    preferred_link_type integer,
    selftest_active boolean NOT NULL
);


ALTER TABLE public.device OWNER TO osp_admin;

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
    incoming boolean NOT NULL
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

ALTER TABLE ONLY oslp_log_item ALTER COLUMN id SET DEFAULT nextval('oslp_log_item_id_seq'::regclass);


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
-- Name: oslp_log_item_pkey; Type: CONSTRAINT; Schema: public; Owner: osp_admin; Tablespace: 
--

ALTER TABLE ONLY oslp_log_item
    ADD CONSTRAINT oslp_log_item_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

