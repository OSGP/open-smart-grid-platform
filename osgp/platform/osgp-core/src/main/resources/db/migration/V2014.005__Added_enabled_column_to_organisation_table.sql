--
-- Add enabled column to organisation table
--

ALTER TABLE organisation ADD COLUMN enabled boolean;

UPDATE organisation SET enabled=TRUE;

ALTER TABLE organisation ALTER COLUMN enabled SET NOT NULL;