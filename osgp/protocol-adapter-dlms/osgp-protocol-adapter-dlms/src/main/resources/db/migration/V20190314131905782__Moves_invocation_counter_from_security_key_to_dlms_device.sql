DO
$$
BEGIN

IF EXISTS (SELECT 1 FROM information_schema.columns
 	WHERE table_schema=current_schema AND table_name = 'security_key' AND column_name = 'invocation_counter') THEN
	 	ALTER TABLE security_key DROP COLUMN invocation_counter;
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.columns
 	WHERE table_schema=current_schema AND table_name = 'dlms_device' AND column_name = 'invocation_counter') THEN
	 	ALTER TABLE dlms_device ADD COLUMN invocation_counter integer;
END IF;

END;
$$


