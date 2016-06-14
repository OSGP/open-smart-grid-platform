ALTER TABLE device ADD COLUMN technical_installation_date timestamp without time zone;


UPDATE device SET technical_installation_date = LOCALTIMESTAMP WHERE is_activated = TRUE;