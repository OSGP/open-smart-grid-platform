-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
begin

if not exists (select 1 from device_function_mapping where "function" = 'SET_RANDOMISATION_SETTINGS') then
	insert into device_function_mapping (function_group, "function") values ('OWNER', 'SET_RANDOMISATION_SETTINGS');
end if;

end;
$$