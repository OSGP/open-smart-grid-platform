delete from scheduled_task where status = 3;

-- status is mapped on ordinal value, make sure the values after 3 are mapped to the correct value
update scheduled_task set status = status -1 where status > 3;