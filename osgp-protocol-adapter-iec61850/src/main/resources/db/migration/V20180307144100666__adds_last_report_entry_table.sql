DO $$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'iec61850_last_report_entry') THEN
  CREATE TABLE IF NOT EXISTS iec61850_last_report_entry (
    id bigint NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    report_id character varying(255) NOT NULL,
    device_identification character varying(40) NOT NULL,
    entry_id bytea NOT NULL,
    time_of_entry timestamp without time zone NOT NULL,
    CONSTRAINT iec61850_last_report_entry_pkey PRIMARY KEY (id),
    CONSTRAINT iec61850_last_report_entry_ukey UNIQUE (report_id, device_identification)
  );

  ALTER TABLE iec61850_last_report_entry OWNER TO osp_admin;

  CREATE SEQUENCE iec61850_last_report_entry_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

  ALTER TABLE iec61850_last_report_entry_seq OWNER TO osp_admin;

  ALTER SEQUENCE iec61850_last_report_entry_seq OWNED BY iec61850_last_report_entry.id;

  ALTER TABLE ONLY iec61850_last_report_entry ALTER COLUMN id SET DEFAULT nextval('iec61850_last_report_entry_seq'::regclass);
END IF;

END;
$$