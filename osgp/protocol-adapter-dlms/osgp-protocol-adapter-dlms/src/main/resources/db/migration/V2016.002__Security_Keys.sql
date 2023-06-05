-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

CREATE TABLE security_key (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    dlms_device_id bigint references dlms_device(id),
    security_key_type character varying(255) NOT NULL,
    valid_from timestamp without time zone NOT NULL,
    valid_to timestamp without time zone,
    security_key character varying(255) NOT NULL
);

ALTER TABLE public.security_key OWNER TO osp_admin;

CREATE SEQUENCE security_key_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.security_key_id_seq OWNER TO osp_admin;

ALTER SEQUENCE security_key_id_seq OWNED BY security_key.id;

ALTER TABLE ONLY security_key ALTER COLUMN id SET DEFAULT nextval('security_key_id_seq'::regclass);
    
ALTER TABLE ONLY security_key
    ADD CONSTRAINT security_key_pkey PRIMARY KEY (id);
    
CREATE UNIQUE INDEX security_key_valid_idx ON security_key (dlms_device_id, security_key_type)
WHERE valid_to IS NULL;

INSERT INTO security_key (creation_time, modification_time, version, dlms_device_id, valid_from, valid_to, security_key_type, security_key)  (
	SELECT creation_time, modification_time, 1, id, modification_time, null, 'E_METER_MASTER', master_key FROM dlms_device
);

INSERT INTO security_key (creation_time, modification_time, version, dlms_device_id, valid_from, valid_to, security_key_type, security_key)  (
	SELECT creation_time, modification_time, 1, id, modification_time, null, 'E_METER_ENCRYPTION', global_encryption_unicast_key FROM dlms_device
);

INSERT INTO security_key (creation_time, modification_time, version, dlms_device_id, valid_from, valid_to, security_key_type, security_key)  (
	SELECT creation_time, modification_time, 1, id, modification_time, null, 'E_METER_AUTHENTICATION', authentication_key FROM dlms_device
);

ALTER TABLE dlms_device DROP COLUMN master_key;
ALTER TABLE dlms_device DROP COLUMN global_encryption_unicast_key;
ALTER TABLE dlms_device DROP COLUMN authentication_key;
