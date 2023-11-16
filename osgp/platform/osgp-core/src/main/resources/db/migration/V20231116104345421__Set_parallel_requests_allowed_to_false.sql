DO
$$
BEGIN

    UPDATE protocol_info SET parallel_requests_allowed = false WHERE protocol = 'DSMR' OR protocol = 'SMR';

END;
$$
