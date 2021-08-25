DO
$$
BEGIN
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE TABLE_SCHEMA = current_schema AND TABLE_NAME = 'firmware_file' AND COLUMN_NAME = 'image_identifier')
    THEN
        ALTER TABLE firmware_file ADD COLUMN image_identifier bytea;
    END IF;

    COMMENT ON COLUMN firmware_file.image_identifier IS 'The ImageIdentifier bytearray is use to initiate the connection to the meter for the purpose of updating with this firmware file';

END;
$$
