DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.columns
 	WHERE table_schema=current_schema AND table_name = 'device' AND column_name = 'last_seen') THEN
	 	ALTER TABLE device ADD COLUMN last_seen  timestamp without time zone ;
END IF;

END;
$$


