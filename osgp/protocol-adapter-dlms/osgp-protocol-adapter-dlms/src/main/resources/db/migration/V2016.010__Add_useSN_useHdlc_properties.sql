-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
 	WHERE table_schema=current_schema AND table_name = 'dlms_device' AND column_name='use_sn') THEN
	 	alter table dlms_device add use_sn boolean not null default false;
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
 	WHERE table_schema=current_schema AND table_name = 'dlms_device' AND column_name='use_hdlc') THEN
		alter table dlms_device add use_hdlc boolean not null default false;
END IF;


END;
$$


