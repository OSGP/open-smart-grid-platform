DO
$$
    BEGIN
        alter table dlms_device alter column communication_method drop not null;
    END;
$$
