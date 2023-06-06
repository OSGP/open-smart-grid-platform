ALTER TABLE scheduled_task ADD COLUMN retry smallint;
ALTER TABLE scheduled_task ALTER COLUMN retry SET DEFAULT 0;
