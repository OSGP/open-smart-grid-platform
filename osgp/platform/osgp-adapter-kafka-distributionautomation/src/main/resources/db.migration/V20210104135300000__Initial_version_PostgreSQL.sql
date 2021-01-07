DO
$$
BEGIN

CREATE DATABASE osgp_adapter_kafka_distributionautomation
    WITH OWNER = osp_admin
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

    CREATE TABLE location
    (
        id                        bigserial                   NOT NULL,
        creation_time             timestamp without time zone NOT NULL,
        modification_time         timestamp without time zone NOT NULL,
        version                   bigint,
        substation_identification character varying(12)       NOT NULL,
        name                      character varying(32)       NOT NULL,
        CONSTRAINT location_pkey PRIMARY KEY (id),
        CONSTRAINT location_substation_identification_key UNIQUE (substation_identification)
    );

    ALTER TABLE public.location OWNER TO osp_admin;

    CREATE TABLE feeder (
            id bigserial NOT NULL,
            creation_time timestamp without time zone NOT NULL,
            modification_time timestamp without time zone NOT NULL,
            version bigint,
            location_id bigint NOT NULL,
            feeder_id bigint NOT NULL,
            name character varying(32) NOT NULL,
            CONSTRAINT feeder_pkey PRIMARY KEY (id),
            CONSTRAINT location_id_fk FOREIGN KEY (location_id) REFERENCES location (id) ON UPDATE CASCADE ON DELETE RESTRICT
        );

    ALTER TABLE public.feeder OWNER TO osp_admin;


END;
$$
