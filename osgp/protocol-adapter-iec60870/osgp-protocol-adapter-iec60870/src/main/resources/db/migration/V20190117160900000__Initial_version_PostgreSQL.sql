DO $$
BEGIN

  CREATE TABLE IF NOT EXISTS iec60870_device (
    id bigserial NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    device_identification character varying(40) NOT NULL,
    common_address integer NOT NULL,
    port integer,
    CONSTRAINT iec60870_device_pkey PRIMARY KEY (id),
    CONSTRAINT iec60870_device_device_identification_key UNIQUE (device_identification)
  );

  ALTER TABLE iec60870_device OWNER TO osp_admin;
  
  COMMENT ON COLUMN iec60870_device.id IS 'Unique technical id of this IEC60870 device.';
  COMMENT ON COLUMN iec60870_device.device_identification IS 'Business key; identification of this IEC60870 device.';
  COMMENT ON COLUMN iec60870_device.common_address IS 'Common address of this IEC60870 device.';
  COMMENT ON COLUMN iec60870_device.port IS 'The port of this IEC60870 device.';

END$$;
