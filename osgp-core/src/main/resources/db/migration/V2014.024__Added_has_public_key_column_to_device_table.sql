ALTER TABLE device ADD COLUMN has_public_key boolean;

UPDATE device SET has_public_key=FALSE;