INSERT INTO device_function_mapping(function_group, function)
  SELECT 'OWNER' ,'GENERATE_AND_REPLACE_KEYS'
  WHERE NOT EXISTS (SELECT 1 
                    FROM device_function_mapping
                    WHERE function_group = 'OWNER'
                    AND function = 'GENERATE_AND_REPLACE_KEYS');