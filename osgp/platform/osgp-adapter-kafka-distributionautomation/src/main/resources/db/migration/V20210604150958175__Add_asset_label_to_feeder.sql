DO
$$
BEGIN

  IF NOT EXISTS(SELECT 1 FROM information_schema.columns
                WHERE table_schema = current_schema AND table_name = 'feeder' AND column_name = 'asset_label')
  THEN
    ALTER TABLE feeder ADD COLUMN asset_label CHARACTER VARYING(255);

    COMMENT ON COLUMN feeder.asset_label IS 'A label providing asset details with the feeder.';
  END IF;

END;
$$
