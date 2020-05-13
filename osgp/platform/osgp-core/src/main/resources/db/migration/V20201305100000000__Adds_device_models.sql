DO
$$
begin

-- ISKRAEMECO CDMA --

if not exists (
    select 1
    from   device_model
    where  model_code = 'AM550E_CDMA'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'Iskraemeco'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'AM550E_CDMA',
    'AM550E_CDMA 1fase SMR 5.0.0 CDMA',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'AM550T_CDMA'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'Iskraemeco'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'AM550T_CDMA',
    'AM550T_CDMA 3fase SMR 5.0.0 CDMA',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'Iskraemeco-S CDMA SMR5.1 energy meter'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'Iskraemeco'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'Iskraemeco-S CDMA SMR5.1 energy meter',
    'Iskraemeco-S CDMA SMR5.1 energy meter 1fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'Iskraemeco-T CDMA SMR5.1 energy meter'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'Iskraemeco'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'Iskraemeco-T CDMA SMR5.1 energy meter',
    'Iskraemeco-T CDMA SMR5.1 energy meter 3fase',
    true,
    true,
    0);

end if;

-- ISKRAEMECO GPRS --

if not exists (
    select 1
    from   device_model
    where  model_code = 'AM550E_GPRS'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'Iskraemeco'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'AM550E_GPRS',
    'AM550E_GPRS 1fase SMR 5.0.0 GPRS',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'AM550T_GPRS'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'Iskraemeco'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'AM550T_GPRS',
    'AM550T_GPRS 3fase SMR 5.0.0 GPRS',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'Iskraemeco-S SMR5.1 GPRS energy meter'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'Iskraemeco'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'Iskraemeco-S SMR5.1 GPRS energy meter',
    'Iskraemeco-S SMR5.1 GPRS energy meter 1fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'Iskraemeco-T SMR5.1 GPRS energy meter'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'Iskraemeco'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'Iskraemeco-T SMR5.1 GPRS energy meter',
    'Iskraemeco-T SMR5.1 GPRS energy meter 3fase',
    true,
    true,
    0);

end if;



-- LANDIS & GYR CDMA --

if not exists (
    select 1
    from   device_model
    where  model_code = 'ZCF2AD2 CDMA SMR5'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'ZCF2AD2 CDMA SMR5',
    'ZCF2AD2 CDMA SMR5 1fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'ZMF2AD2 CDMA SMR5'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'ZMF2AD2 CDMA SMR5',
    'ZMF2AD2 CDMA SMR5 3fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'E360 CD2D CDMA SMR5'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'E360 CD2D CDMA SMR5',
    'E360 CD2D CDMA SMR5 1fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'E360 CM3D CDMA SMR5'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'E360 CM3D CDMA SMR5',
    'E360 CM3D CDMA SMR5 3fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'Landis and Gyr-S CDMA SMR5.1 energy meter'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'Landis and Gyr-S CDMA SMR5.1 energy meter',
    'Landis and Gyr-S CDMA SMR5.1 energy meter 1fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'Landis and Gyr-T CDMA SMR5.1 energy meter'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'Landis and Gyr-T CDMA SMR5.1 energy meter',
    'Landis and Gyr-T CDMA SMR5.1 energy meter 3fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'Landis and Gyr-S E360 CDMA SMR5.1 energy meter'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'Landis and Gyr-S E360 CDMA SMR5.1 energy meter',
    'Landis and Gyr-S E360 CDMA SMR5.1 energy meter 1fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'Landis and Gyr-T E360 CDMA SMR5.1 energy meter'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'Landis and Gyr-T E360 CDMA SMR5.1 energy meter',
    'Landis and Gyr-T E360 CDMA SMR5.1 energy meter 3fase',
    true,
    true,
    0);

end if;

-- LANDIS & GYR GPRS --

if not exists (
    select 1
    from   device_model
    where  model_code = 'ZCF2AD2 GPRS SMR5'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'ZCF2AD2 GPRS SMR5',
    'ZCF2AD2 GPRS SMR5 1fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'ZMF2AD2 GPRS SMR5'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'ZMF2AD2 GPRS SMR5',
    'ZMF2AD2 GPRS SMR5 3fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'E360 CD2D GPRS SMR5'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'E360 CD2D GPRS SMR5',
    'E360 CD2D GPRS SMR5 1fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'E360 CM3D GPRS SMR5'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'E360 CM3D GPRS SMR5',
    'E360 CM3D GPRS SMR5 3fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'Landis and Gyr-S SMR5.1 energy meter'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'Landis and Gyr-S SMR5.1 energy meter',
    'Landis and Gyr-S SMR5.1 energy meter 1fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'Landis and Gyr-T SMR5.1 energy meter'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'Landis and Gyr-T SMR5.1 energy meter',
    'Landis and Gyr-T SMR5.1 energy meter 3fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'Landis and Gyr-S E360 SMR5.1 energy meter'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'Landis and Gyr-S E360 SMR5.1 energy meter',
    'Landis and Gyr-S E360 SMR5.1 energy meter 1fase',
    true,
    true,
    0);

end if;

if not exists (
    select 1
    from   device_model
    where  model_code = 'Landis and Gyr-T E360 SMR5.1 energy meter'
    ) then

insert into device_model(
    id,
    manufacturerid,
    creation_time,
    modification_time,
    model_code,
    description,
    file_storage,
    metered,
    version)
values (
    nextval('device_model_id_seq'),
    (select id from manufacturer where name = 'L+G'),
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    'Landis and Gyr-T E360 SMR5.1 energy meter',
    'Landis and Gyr-T E360 SMR5.1 energy meter 3fase',
    true,
    true,
    0);

end if;

end;
$$
