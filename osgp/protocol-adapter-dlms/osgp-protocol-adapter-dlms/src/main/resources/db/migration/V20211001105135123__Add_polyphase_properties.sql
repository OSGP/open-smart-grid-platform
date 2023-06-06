DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
 	WHERE table_schema=current_schema AND table_name = 'dlms_device' AND column_name='polyphase') THEN
	 	alter table dlms_device add polyphase boolean not null default false;
END IF;

END;
$$


