DO
$$
BEGIN

    IF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE TABLE_SCHEMA = current_schema AND TABLE_NAME = 'firmware_file' AND COLUMN_NAME = 'image_identifier')
    THEN
        ALTER TABLE firmware_file DROP COLUMN image_identifier;
    END IF;

    IF NOT EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE TABLE_SCHEMA = current_schema AND TABLE_NAME = 'firmware_file' AND COLUMN_NAME = 'hash_type')
    THEN
        ALTER TABLE firmware_file ADD COLUMN hash_type character varying(12);
    END IF;


END;
$$