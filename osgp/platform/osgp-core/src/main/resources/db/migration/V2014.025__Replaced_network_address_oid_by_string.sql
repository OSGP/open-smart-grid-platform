-- drop old network_address column of type oid
ALTER TABLE device DROP COLUMN network_address;

-- add new network_address column of type character varying 50
ALTER TABLE device ADD COLUMN network_address character varying(50);
