do
	$$
	declare l_count integer;
	begin
		select count(*) into l_count
		from pg_indexes
		where schemaname = 'public'
		and tablename = 'meter_response_data'
		and indexname = 'meter_response_data_correlation_uid_idx';
	
		if l_count = 0 then 
			execute 'CREATE UNIQUE INDEX ON meter_response_data (correlation_uid)';
		end if;

end;
$$