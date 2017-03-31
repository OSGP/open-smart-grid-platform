DO $$
BEGIN

IF NOT EXISTS (SELECT constraint_name FROM information_schema.constraint_column_usage 
	WHERE table_name = 'protocol_info' AND constraint_name = 'unique_protocol_version')

THEN

	ALTER TABLE protocol_info ADD CONSTRAINT unique_protocol_version UNIQUE (protocol, protocol_version);

END IF;

END;
$$



