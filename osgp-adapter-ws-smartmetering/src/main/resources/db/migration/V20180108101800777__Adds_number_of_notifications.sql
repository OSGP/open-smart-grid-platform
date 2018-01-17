DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   information_schema.columns
    WHERE  table_schema = current_schema
    AND    table_name  = 'response_data'
    AND	   column_name = 'number_of_notifications_sent') THEN

    ALTER TABLE response_data ADD COLUMN number_of_notifications_sent smallint NOT NULL DEFAULT(0);

END IF;

END;
$$