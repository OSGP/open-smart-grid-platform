--
-- Rename REQUEST_SPECIAL_DAYS to SET_SPECIAL_DAYS in device_function_mapping table
-- SLIM-689
--

update device_function_mapping set function = 'SET_SPECIAL_DAYS' where function='REQUEST_SPECIAL_DAYS';
