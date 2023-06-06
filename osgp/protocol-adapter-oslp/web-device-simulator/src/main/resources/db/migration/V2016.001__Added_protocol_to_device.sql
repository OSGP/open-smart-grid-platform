ALTER TABLE device ADD COLUMN protocol character varying(255);

-- set protocol initial to OSLP
UPDATE device SET protocol = 'OSLP';