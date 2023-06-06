DO
$$
BEGIN

  IF NOT EXISTS(SELECT 1 FROM information_schema.columns
                WHERE table_schema = current_schema
                AND table_name = 'device'
                AND column_name = 'container_number_addition')
  THEN
    ALTER TABLE device RENAME COLUMN container_number TO original_container_number;
    
    ALTER TABLE device ADD COLUMN container_number INT;
    ALTER TABLE device ADD COLUMN container_number_addition VARCHAR(10);

    COMMENT ON COLUMN device.container_number IS 'The street number of this device.';
    COMMENT ON COLUMN device.container_number_addition IS 'The street number addition of this device.';
    
    UPDATE device 
    SET
        container_number = CAST(SUBSTRING(TRIM(original_container_number) FROM '^[\d]+') AS INT),
        container_number_addition = TRIM(SUBSTRING(TRIM(original_container_number) FROM '[^\d]+.*$'));
  END IF;

END;
$$
