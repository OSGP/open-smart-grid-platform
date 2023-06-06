DO
$$
BEGIN
    ALTER TABLE smart_meter ALTER COLUMN mbus_identification_number TYPE character varying(10);
END;
$$
