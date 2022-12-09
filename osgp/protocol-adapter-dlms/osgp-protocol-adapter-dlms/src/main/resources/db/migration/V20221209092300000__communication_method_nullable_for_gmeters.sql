DO
$$
    BEGIN
        alter table device_model alter column communication_method drop not null;
    END;
$$
