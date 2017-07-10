DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
    WHERE table_schema = current_schema 
    AND     table_name = 'firmware' 
    AND    column_name = 'identification') THEN

  ALTER TABLE ONLY firmware ADD COLUMN identification character varying(100);
  UPDATE firmware SET identification = md5(random()::text);
  ALTER TABLE firmware ALTER COLUMN identification SET NOT NULL;
  ALTER TABLE firmware ADD CONSTRAINT firmware_identification_key UNIQUE (identification);

END IF;

END;
$$
