DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   pg_tables
    WHERE  schemaname = current_schema
    AND    tablename  = 'location') THEN

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

    ALTER TABLE location OWNER TO osp_admin;

END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_tables
    WHERE  schemaname = current_schema
    AND    tablename  = 'feeder') THEN

    CREATE TABLE feeder (
            id bigserial NOT NULL,
            creation_time timestamp without time zone NOT NULL,
            modification_time timestamp without time zone NOT NULL,
            version bigint,
            location_id bigint NOT NULL,
            feeder_number integer NOT NULL,
            name character varying(32) NOT NULL,
            CONSTRAINT feeder_pkey PRIMARY KEY (id),
            CONSTRAINT location_feeder_key UNIQUE (location_id, feeder_number),
            CONSTRAINT location_id_fk FOREIGN KEY (location_id) REFERENCES location (id) ON UPDATE CASCADE ON DELETE RESTRICT
        );

    ALTER TABLE feeder OWNER TO osp_admin;

END IF;

END;
$$
