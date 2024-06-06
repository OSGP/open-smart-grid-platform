ALTER TABLE dlms_device ADD COLUMN with_list_max integer DEFAULT 1;

UPDATE dlms_device
SET with_list_max = 32
WHERE with_list_supported = true;

UPDATE dlms_device
SET with_list_max = 1
WHERE with_list_supported = false;

ALTER TABLE dlms_device DROP COLUMN with_list_supported;
