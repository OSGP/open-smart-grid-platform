--
-- Rename GET_CONFIGURATION_OBJECTS to GET_ALL_ATTRIBUTE_VALUES in device_function_mapping table
-- SLIM-675
--

update device_function_mapping set function = 'GET_ALL_ATTRIBUTE_VALUES' where function='GET_CONFIGURATION_OBJECTS';
