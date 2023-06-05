-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'response_url') THEN
	
	CREATE TABLE response_url (
	    id bigint NOT NULL,
	    creation_time timestamp without time zone NOT NULL,
    	modification_time timestamp without time zone NOT NULL,
  	    version bigint,
	    correlation_uid character varying(255),
	    response_url character varying(255)
	);
	
	ALTER TABLE public.response_url OWNER TO osp_admin;
	
	CREATE SEQUENCE response_url_seq
	    START WITH 1
	    INCREMENT BY 1
	    NO MINVALUE
	    NO MAXVALUE
	    CACHE 1;
	    
	ALTER TABLE public.response_url_seq OWNER TO osp_admin;
	
	ALTER SEQUENCE response_url_seq OWNED BY response_url.id;
	
	ALTER TABLE ONLY response_url ALTER COLUMN id SET DEFAULT nextval('response_url_seq'::regclass);
	
	ALTER TABLE ONLY response_url ADD CONSTRAINT response_url_pkey PRIMARY KEY (id);
	
	CREATE INDEX resonse_url_correlation_uid_idx ON response_url (correlation_uid);

END IF;


END;
$$