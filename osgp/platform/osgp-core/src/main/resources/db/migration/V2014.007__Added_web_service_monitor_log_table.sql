-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- Create table
CREATE TABLE web_service_monitor_log(
	id bigint NOT NULL,
	creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    time_stamp timestamp without time zone NOT NULL,
    class_name character varying(255) NOT NULL,
    method_name character varying(255) NOT NULL,
    organisation_identification character varying(40) NOT NULL,    
    request_device_identification character varying(40),
    request_correlation_uid character varying(255),
    response_result character varying(15),
    response_data_size integer
);

ALTER TABLE public.web_service_monitor_log OWNER TO osp_admin;

CREATE SEQUENCE web_service_monitor_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.web_service_monitor_log_id_seq OWNER TO osp_admin;

ALTER SEQUENCE web_service_monitor_log_id_seq OWNED BY web_service_monitor_log.id;

ALTER TABLE ONLY web_service_monitor_log ALTER COLUMN id SET DEFAULT nextval('web_service_monitor_log_id_seq'::regclass);

ALTER TABLE ONLY web_service_monitor_log ADD CONSTRAINT web_service_monitor_log_pkey PRIMARY KEY (id);