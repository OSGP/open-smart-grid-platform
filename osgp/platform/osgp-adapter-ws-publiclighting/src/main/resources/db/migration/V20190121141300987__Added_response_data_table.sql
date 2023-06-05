-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_tables
    WHERE  schemaname = current_schema
    AND    tablename  = 'response_data') THEN

    -- Table: public.response_data

    CREATE TABLE public.response_data
    (
      id bigint NOT NULL,
      creation_time timestamp without time zone NOT NULL,
      modification_time timestamp without time zone NOT NULL,
      version bigint,
      organisation_identification character varying(255),
      device_identification character varying(255),
      message_type character varying(255),
      correlation_uid character varying(255),
      message_data bytea,
      result_type character varying(255),
      number_of_notifications_sent smallint NOT NULL DEFAULT 0
    );

    ALTER TABLE public.response_data OWNER TO osp_admin;

    COMMENT ON COLUMN public.response_data.organisation_identification IS 'Identification of the organisation behind the request this response is for.';
    COMMENT ON COLUMN public.response_data.device_identification IS 'Identification of the device the response data belongs with.';
    COMMENT ON COLUMN public.response_data.message_type IS 'Indicates which device function was executed that lead to this response.';
    COMMENT ON COLUMN public.response_data.correlation_uid IS 'Unique identifier correlating all actions related to the request this response is for.';
    COMMENT ON COLUMN public.response_data.message_data IS 'Serialized response object.';
    COMMENT ON COLUMN public.response_data.result_type IS 'Message result type [OK, NOT_FOUND, NOT_OK].';
    COMMENT ON COLUMN public.response_data.number_of_notifications_sent IS 'Number of notifications that has been sent after the initial one to notify a response is available for the request with this records correlation_uid.';

    CREATE SEQUENCE response_data_id_seq
        START WITH 1
        INCREMENT BY 1
        NO MINVALUE
        NO MAXVALUE
        CACHE 1;

    ALTER TABLE public.response_data_id_seq OWNER TO osp_admin;

    ALTER SEQUENCE response_data_id_seq OWNED BY response_data.id;

    ALTER TABLE ONLY response_data ALTER COLUMN id SET DEFAULT nextval('response_data_id_seq'::regclass);

    ALTER TABLE ONLY response_data ADD CONSTRAINT response_data_pkey PRIMARY KEY (id);

    -- Index: public.meter_response_data_correlation_uid_idx

    CREATE UNIQUE INDEX response_data_correlation_uid_idx
      ON public.response_data
      USING btree
      (correlation_uid COLLATE pg_catalog."default");

END IF;

END;
$$
