DO
$$
BEGIN

CREATE INDEX IF NOT EXISTS scheduled_task_status_idx on scheduled_task(status);
CREATE INDEX IF NOT EXISTS scheduled_task_scheduled_time_idx on scheduled_task(scheduled_time);

DELETE FROM scheduled_task WHERE status = 3 AND error_log = 'No response received for scheduled task';

END;
$$
