CREATE INDEX IF NOT EXISTS device_log_item_device_identification_upper ON device_log_item(UPPER(device_identification));
ANALYZE device_log_item;