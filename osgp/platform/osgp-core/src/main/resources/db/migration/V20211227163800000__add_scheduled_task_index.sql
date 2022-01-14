DO
$$
BEGIN

CREATE UNIQUE INDEX IF NOT EXISTS scheduled_task_correlation_uid ON scheduled_task(correlation_uid);

END;
$$
