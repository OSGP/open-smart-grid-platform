ALTER TABLE scheduled_task ADD COLUMN max_retries smallint;
ALTER TABLE scheduled_task ALTER COLUMN max_retries SET DEFAULT 0;
ALTER TABLE scheduled_task ADD COLUMN retry smallint;
ALTER TABLE scheduled_task ALTER COLUMN retry SET DEFAULT 0;
