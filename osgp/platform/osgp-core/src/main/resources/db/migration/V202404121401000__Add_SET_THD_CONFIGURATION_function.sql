DO
$$
BEGIN

  INSERT INTO public.device_function_mapping(function_group, function)
    VALUES ('OWNER', 'SET_THD_CONFIGURATION');

END;
$$
