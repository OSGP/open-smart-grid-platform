DO
$$
BEGIN

  IF NOT EXISTS(SELECT 1 FROM information_schema.columns
                WHERE table_schema = current_schema AND table_name = 'event' AND column_name = 'device_identification')
  THEN
    ALTER TABLE event ADD COLUMN device_identification CHARACTER VARYING(40);
    COMMENT ON COLUMN event.device_identification IS 'Identification of the device that generated the event.';
    
    UPDATE event
    SET device_identification = (SELECT device.device_identification FROM device WHERE device.id = event.device)
    ;
    
    ALTER TABLE event DROP COLUMN device;
  END IF;

END;
$$
