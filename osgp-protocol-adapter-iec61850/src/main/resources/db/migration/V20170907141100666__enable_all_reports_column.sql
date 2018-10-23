DO $$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
    WHERE table_schema=current_schema AND table_name = 'iec61850_device' AND column_name='enable_all_reports_on_connect') THEN
    ALTER TABLE iec61850_device ADD COLUMN enable_all_reports_on_connect BOOLEAN NOT NULL DEFAULT FALSE;
END IF;

END;
$$