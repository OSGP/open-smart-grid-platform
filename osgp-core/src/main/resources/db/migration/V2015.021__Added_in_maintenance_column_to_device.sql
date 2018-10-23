--
-- Add in_maintenance column to device table
--

ALTER TABLE device ADD COLUMN in_maintenance boolean;

UPDATE device SET in_maintenance=FALSE;

ALTER TABLE device ALTER COLUMN in_maintenance SET NOT NULL;