-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

--
-- Add table device_log_item
--

CREATE TABLE device_log_item
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
  CONSTRAINT device_log_item_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

ALTER TABLE device_log_item OWNER TO osp_admin;

GRANT ALL ON TABLE device_log_item TO osp_admin;

GRANT SELECT ON TABLE device_log_item TO osgp_read_only_ws_user;
