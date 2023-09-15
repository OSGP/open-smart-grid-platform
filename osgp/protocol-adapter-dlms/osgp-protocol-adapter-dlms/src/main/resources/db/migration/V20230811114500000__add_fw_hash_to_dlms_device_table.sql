DO
$$
BEGIN
    ALTER TABLE dlms_device ADD COLUMN firmware_hash VARCHAR(64) null;
END;
$$