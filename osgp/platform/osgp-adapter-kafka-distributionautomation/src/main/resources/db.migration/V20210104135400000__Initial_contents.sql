DO
$$
BEGIN

    insert into location (id, creation_time,  modification_time, version, substation_identification, name) values(1, now(), now(), 1, '9005762', 'Balkon Zuid');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 1, 1, '00OY40');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 1, 2, '00OY41');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 1, 3, '00OY42');

    insert into location (id, creation_time,  modification_time, version, substation_identification, name) values(2, now(), now(), 1, '5001902', 'Benedeneind');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 2, 1, '29315');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 2, 2, '29318');

    insert into location (id, creation_time,  modification_time, version, substation_identification, name) values(3, now(), now(), 1, '9022082', 'Meentdijk 23');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 3, 2, '29317');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 3, 3, '29316');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 3, 4, '29313');

    insert into location (id, creation_time,  modification_time, version, substation_identification, name) values(4, now(), now(), 1, '9021595', 'Veenderij Eiland');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 4, 1, '01SU58');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 4, 2, '01SU57');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 4, 3, '01SU56');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 4, 4, '01SU55');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 4, 5, '37276');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 4, 6, '37216');

    insert into location (id, creation_time,  modification_time, version, substation_identification, name) values(5, now(), now(), 1, '9018549', 'Veenderij Vlek D');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 5, 1, '01IX42');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 5, 2, '01IX45');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 5, 3, '01IX43');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 5, 4, '01IX44');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 5, 5, '01SU63');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 5, 6, '01SU64');

    insert into location (id, creation_time,  modification_time, version, substation_identification, name) values(6, now(), now(), 1, '9016078', 'Veenderij');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 6, 1, '01JN07');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 6, 2, '01EF35');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 6, 3, '01EF38');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 6, 4, '01EF37');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 6, 5, '01JN08');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 6, 6, '01YK35');
    insert into feeder (creation_time,  modification_time, version, location_id, feeder_id, name) values(now(), now(), 1, 6, 7, '03FQ03');

END;
$$
