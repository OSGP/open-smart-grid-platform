DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
    WHERE table_schema = current_schema 
    AND     table_name = 'device' 
    AND    column_name = 'integration_type') THEN

    ALTER TABLE device ADD COLUMN integration_type character varying(20) NOT NULL DEFAULT 'WEB_SERVICE';
    COMMENT ON COLUMN device.integration_type IS 'indicates the routing of messages to be used for the device; possible values are [WEB_SERVICE,KAFKA,BOTH]';
END IF;

END;
$$
