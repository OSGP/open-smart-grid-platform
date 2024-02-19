DO
$$
BEGIN
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE TABLE_SCHEMA = current_schema AND TABLE_NAME = 'scheduled_task' AND COLUMN_NAME = 'device_model_code')
    THEN
        ALTER TABLE scheduled_task ADD COLUMN device_model_code VARCHAR(1279);
    END IF;

    COMMENT ON COLUMN scheduled_task.device_model_code IS 'comma separated list of devicemodel codes. First modelcode is the gateway device and subsequently the mbus devicess on channel 1 to 4 (size:(5*255)+4)';
END;
$$
