DO
$$
BEGIN

ALTER TABLE dlms_device ALTER COLUMN invocation_counter TYPE BIGINT;

END;
$$
