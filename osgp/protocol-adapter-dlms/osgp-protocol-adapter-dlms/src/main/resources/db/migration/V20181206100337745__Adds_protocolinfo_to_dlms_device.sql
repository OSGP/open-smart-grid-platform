DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.columns
 	WHERE table_schema=current_schema AND table_name = 'dlms_device' AND column_name = 'protocol') THEN
	 	ALTER TABLE dlms_device ADD COLUMN protocol VARCHAR(255) NOT NULL DEFAULT 'DSMR';
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.columns
 	WHERE table_schema=current_schema AND table_name = 'dlms_device' AND column_name = 'protocol_version') THEN
	 	ALTER TABLE dlms_device ADD COLUMN protocol_version VARCHAR(255) NOT NULL DEFAULT '4.2.2';
END IF;

END;
$$


