INSERT INTO device_function_mapping(function_group, function)
  SELECT 'OWNER' ,'GET_M_BUS_DEVICE_ON_CHANNEL'
  WHERE NOT EXISTS (SELECT 1 
                    FROM device_function_mapping
                    WHERE function_group = 'OWNER'
                    AND function = 'GET_M_BUS_DEVICE_ON_CHANNEL');