DO
$$
BEGIN

    UPDATE dlms_device SET communication_method = 'LTE' WHERE communication_method = 'LTE-M';

END;
$$ 
