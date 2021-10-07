DO
$$
begin

if ((select count(*)
     from   protocol_info
     where  protocol like '%_CDMA') = 3) then

delete from protocol_info WHERE protocol = 'SMR_CDMA' AND protocol_version  = '5.1';
delete from protocol_info WHERE protocol = 'SMR_CDMA' AND protocol_version  = '5.0.0';
delete from protocol_info WHERE protocol = 'DSMR_CDMA' AND protocol_version  = '4.2.2';

end if;

end;
$$
