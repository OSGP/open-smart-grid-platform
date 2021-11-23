DO
$$
begin

/*
 * There are 3 records created in previous flyway scripts SMR_CDMA (5.1), SMR_CDMA (5.0.0) and DSMR_CDMA (4.2.2)
 * These records are CDMA specific, and should not be added by the flyway scripts.
 * In production there could be other specific protocols already created by hand (for example: SMR_GPRS and SMR_LTE_M),
 * when exist in the database, then we do not want to delete the 3 protocol specific records.
 */
if ((select count(*)
     from   protocol_info
     where  protocol like '%SMR_%') = 3) then

delete from protocol_info WHERE protocol = 'SMR_CDMA' AND protocol_version  = '5.1';
delete from protocol_info WHERE protocol = 'SMR_CDMA' AND protocol_version  = '5.0.0';
delete from protocol_info WHERE protocol = 'DSMR_CDMA' AND protocol_version  = '4.2.2';

end if;

end;
$$
