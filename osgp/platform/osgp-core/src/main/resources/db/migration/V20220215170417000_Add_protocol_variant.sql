DO
$$
BEGIN
	IF NOT EXISTS (
		SELECT 1 FROM information_schema.columns
	    WHERE TABLE_SCHEMA = current_schema AND TABLE_NAME = 'protocol_info' AND COLUMN_NAME = 'protocol_variant')
	THEN
		ALTER TABLE protocol_info ADD COLUMN protocol_variant VARCHAR(255);
		CREATE UNIQUE INDEX IF NOT EXISTS unique_protocol_kind_version_variant_id ON protocol_info (protocol, protocol_version, protocol_variant);
		ALTER TABLE protocol_info ADD CONSTRAINT unique_protocol_kind_version_variant_id UNIQUE USING INDEX unique_protocol_kind_version_variant_id;
	END IF;
END;
$$