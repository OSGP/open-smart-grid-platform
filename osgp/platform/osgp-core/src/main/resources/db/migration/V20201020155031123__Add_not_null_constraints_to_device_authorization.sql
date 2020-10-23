DELETE FROM device_authorization WHERE device IS NULL OR organisation IS NULL OR function_group IS NULL;

ALTER TABLE device_authorization ALTER COLUMN device SET NOT NULL;
ALTER TABLE device_authorization ALTER COLUMN organisation SET NOT NULL;
ALTER TABLE device_authorization ALTER COLUMN function_group SET NOT NULL;
