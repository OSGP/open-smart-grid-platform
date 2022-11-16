DO
$$
BEGIN
    ALTER TABLE dlms_device ADD COLUMN timezone VARCHAR(40) null;
END;
$$
