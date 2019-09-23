DO
$$
begin

if not exists (select 1 from device_function_mapping where "function" = 'SET_RANDOMIZATION_SETTINGS') then
	insert into device_function_mapping (function_group, "function") values ('OWNER', 'SET_RANDOMIZATION_SETTINGS');
end if;

end;
$$