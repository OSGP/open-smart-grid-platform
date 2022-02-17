DO
$$
BEGIN
    ALTER TABLE dlms_device ALTER COLUMN mbus_identification_number TYPE character varying(10);
END;
$$
