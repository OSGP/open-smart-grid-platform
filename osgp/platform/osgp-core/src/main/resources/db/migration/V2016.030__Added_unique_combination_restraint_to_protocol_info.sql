DO $$
BEGIN

IF NOT EXISTS (SELECT 1 FROM pg_constraint 
	WHERE conname = 'unique_protocol_version')

THEN

	ALTER TABLE protocol_info ADD CONSTRAINT unique_protocol_version UNIQUE (protocol, protocol_version);

END IF;

END;
$$
