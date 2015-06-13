--
-- Add table device_log
--

CREATE TABLE device_log
(
  id bigserial NOT NULL,
  creation_time timestamp without time zone NOT NULL,
  modification_time timestamp without time zone NOT NULL,
  version bigint,
  decoded_message character varying(8000),
  device_identification character varying(255),
  device_uid character varying(255),
  encoded_message character varying(8000),
  incoming boolean NOT NULL,
  organisation_identification character varying(255),
  valid boolean NOT NULL,
  data_size integer NOT NULL,
  CONSTRAINT device_log_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

ALTER TABLE device_log OWNER TO osp_admin;

GRANT ALL ON TABLE device_log TO osp_admin;

GRANT SELECT ON TABLE device_log TO osgp_read_only_ws_user;
