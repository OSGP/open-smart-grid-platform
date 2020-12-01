DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.columns
  WHERE table_schema=current_schema AND table_name = 'encrypted_secret' AND column_name = 'secret_status') THEN
    -- add non-null column 'secret_status' with initial value 'EXPIRED'
 	ALTER TABLE "encrypted_secret" ADD COLUMN secret_status CHARACTER VARYING(32);
    UPDATE "encrypted_secret" SET secret_status = 'EXPIRED';
    ALTER TABLE "encrypted_secret" ALTER COLUMN secret_status SET NOT NULL;
    -- set most recent secret for each device and secret_type to 'ACTIVE'
    UPDATE encrypted_secret es
    SET secret_status = 'ACTIVE'
      WHERE es.creation_time = (
        SELECT max(es2.creation_time)
        FROM encrypted_secret es2
        WHERE es.device_identification=es2.device_identification AND es.secret_type=es2.secret_type
        GROUP BY es2.device_identification, es2.secret_type);
END IF;

END;
$$