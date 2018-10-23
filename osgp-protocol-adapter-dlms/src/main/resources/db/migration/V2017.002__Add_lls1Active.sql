DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
 	WHERE table_schema=current_schema AND table_name = 'dlms_device' AND column_name='lls1Active') THEN
	 	alter table dlms_device add lls1Active boolean not null default false;
END IF;

END;
$$


