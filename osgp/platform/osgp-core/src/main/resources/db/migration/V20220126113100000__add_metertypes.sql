DO $$
DECLARE

  device_models varchar[];
  CREATOR_NAME varchar := 'Integration Layer';
  
BEGIN

    -- Array definition
	-- [1] devicemodel code
	-- [2] devicemodel name (and description)
	-- [3] devicemodel family name
	-- [4] Manufacturer name
	-- [5] Manufacturer code
    FOREACH device_models SLICE 1 IN ARRAY
		array [ ['ACD-G10 e WL', 'G10, DSMR4, Wireless, G-meter, Itron', 'mbusmeter', 'Itron', 'ITR'],
				['ACD-G16 e WL', 'G16, DSMR4, Wireless, G-meter, Itron', 'mbusmeter', 'Itron', 'ITR'],
				['AM550E_CDMA', 'KWH_ISKRA_CDMA_SMR5 1-phase', 'KWH_ISKRA_CDMA_SMR5', 'Iskraemeco', 'ISK'],
				['AM550E_GPRS', 'KWH_ISKRA_SMR5 1-phase', 'KWH_ISKRA_SMR5', 'Iskraemeco', 'ISK'],
				['AM550T_CDMA', 'KWH_ISKRA_CDMA_SMR5 3-phase', 'KWH_ISKRA_CDMA_SMR5', 'Iskraemeco', 'ISK'],
				['AM550T_GPRS', 'KWH_ISKRA_SMR5 3-phase', 'KWH_ISKRA_SMR5', 'Iskraemeco', 'ISK'],
				['BK-G10 ETB', 'G10, DSMR4, Wireless, G-meter, Elster', 'mbusmeter', 'Elster', 'ELS'],
				['BK-G16 ETB', 'G16, DSMR4, Wireless, G-meter, Elster', 'mbusmeter', 'Elster', 'ELS'],
				['BK-G25 ETB', 'G25, DSMR4, Wireless, G-meter, Elster', 'mbusmeter', 'Elster', 'ELS'],
				['BK-G4 A', 'G4, DSMR2.2+, Wired, G-meter, Elster', 'mbusmeter', 'Elster', 'ELS'],
				['BK-G4 A RV', 'G4, DSMR2.2+, Wireless, G-meter, Elster', 'mbusmeter', 'Elster', 'ELS'],
				['BK-G4 ETB WL', 'G4, DSMR4, Wireless, G-meter, Elster', 'mbusmeter', 'Elster', 'ELS'],
				['BK-G4 ETB WR', 'G4, DSMR4, Wired, G-meter, Elster', 'mbusmeter', 'Elster', 'ELS'],
				['BK-G4 ETeB WL 5.1_G0081', 'Honeywell G4 SMR5.1', 'mbusmeter', 'Honeywell', 'HON'],
				['BK-G6 ETB WL', 'G6, DSMR 4, wireless, G-meter, Elster', 'mbusmeter', 'Elster', 'ELS'],
				['E360 CD2D CDMA SMR5', 'Single phase CDMA SMR5 energy meter, manufacturer Landis and Gyr, type E360', 'Landis and Gyr E360 CDMA SMR5', 'Landis & Gyr', 'LGB'],
				['E360 CD2D GPRS SMR5', 'Single phase SMR5 energy meter, manufacturer Landis and Gyr, type E360', 'Landis and Gyr E360 SMR5', 'Landis & Gyr', 'LGB'],
				['E360 CM3D CDMA SMR5', 'Poly phase CDMA SMR5 energy meter, manufacturer Landis and Gyr, type E360', 'Landis and Gyr E360 CDMA SMR5', 'Landis & Gyr', 'LGB'],
				['E360 CM3D GPRS SMR5', 'Poly phase SMR5 energy meter, manufacturer Landis and Gyr, type E360', 'Landis and Gyr E360 SMR5', 'Landis & Gyr', 'LGB'],
				['elster-instromet', 'G4/G6/G16/G25, Pre-NTA, Wired, G-meter, Elster', 'mbusmeter', 'Elster', 'ELS'],
				['elster-instromet Enexis', 'G4/G6/G16/G25, Pre-NTA, Wired, Enexis G-meter, Elster', 'mbusmeter', 'Elster', 'ELS'],
				['flonidan-gasmeter', 'G4/G6/G10/G16, Pre-NTA, Wired, G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['flonidan-gasmeter-rf', 'G4/G6/G10/G16, Pre-NTA, Wireless, G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G10SMV', 'G10, DSMR2.2+, Wired, G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G10SR', 'G10, DSMR2.2+, Wireless, No Valve G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G10SRT', 'Flonidan G10 SMR5', 'mbusmeter', 'Flonidan', 'FLO'],
				['G10SR-temp', 'G10, DSMR2.2+,WL-T-corr.no Valve G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G10SRV', 'G10, DSMR2.2+, Wireless, G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G16SMV', 'G16, DSMR2.2+, Wired, G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G16SR', 'G16, DSMR2.2+, Wireless, No Valve G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G16SRT', 'Flonidan G16 SMR5', 'mbusmeter', 'Flonidan', 'FLO'],
				['G16SRV', 'G16, DSMR2.2+, Wireless, G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G25 e WL', 'G25, DSMR4, Wireless, G-meter, Itron', 'mbusmeter', 'Itron', 'ITR'],
				['G25SR', 'G25, DSMR2.2+, Wireless, No Valve G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G25SRT', 'Flonidan G25 SMR5', 'mbusmeter', 'Flonidan', 'FLO'],
				['G35034000614054', 'G4, DSMR2.2+, Wireless, G-meter, Landis+Gyr', 'mbusmeter', 'Landis & Gyr', 'LGB'],
				['G35034000664056', 'G4, DSMR2.2+, Wireless, DNWB G-meter, Landis+Gyr', 'mbusmeter', 'Landis & Gyr', 'LGB'],
				['G35045000614054', 'G4, DSMR2.2+, Wired, G-meter, Landis+Gyr', 'mbusmeter', 'Landis & Gyr', 'LGB'],
				['G35045000664056', 'G4, DSMR2.2+, Wired, DNWB G-meter, Landis+Gyr', 'mbusmeter', 'Landis & Gyr', 'LGB'],
				['G350-56 G4 SMR5 WL', 'L&G G4 SMR5', 'mbusmeter', 'Landis & Gyr', 'LGB'],
				['G350-56 G4V DSMR4 WL', 'G4, DSMR4, Wireless, G-meter, Landis+Gyr', 'mbusmeter', 'Landis & Gyr', 'LGB'],
				['G350-56 G4V DSMR4 WR', 'G4, DSMR4, Wired, G-meter, Landis+Gyr', 'mbusmeter', 'Landis & Gyr', 'LGB'],
				['G350-59 G6 SMR5 WL', 'L&G G6 SMR5', 'mbusmeter', 'Landis & Gyr', 'LGB'],
				['G350-59 G6V DSMR4 WL', 'G6, DSMR4, Wireless, G-meter, Landis+Gyr', 'mbusmeter', 'Landis & Gyr', 'LGB'],
				['G350-59 G6V DSMR4 WR', 'G6, DSMR4, Wired, G-meter, Landis+Gyr', 'mbusmeter', 'Landis & Gyr', 'LGB'],
				['G4 RF1 e WL', 'G4, DSMR 4, Wireless, G-meter, Itron, Valveless', 'mbusmeter', 'Itron', 'ITR'],
				['G4 RF1 eV SW', 'G4, DSMR4, Wired, G-meter, Itron', 'mbusmeter', 'Itron', 'ITR'],
				['G4 RF1 eV WL', 'G4, DSMR4, Wireless, G-meter, Itron', 'mbusmeter', 'Itron', 'ITR'],
				['G4SMV', 'G4, DSMR2.2+, Wired, G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G4SMV 1.2L', 'G4 1.2L, DSMR2.2+, Wired, G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G4SMV-temp', 'G4, DSMR2.2+, Wired, Temp-Corrected G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G4SRT 1.2L', 'Flonidan G4 SMR5', 'mbusmeter', 'Flonidan', 'FLO'],
				['G4SRV', 'G4, DSMR2.2+, Wireless, G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G4SRV 1.2L', 'G4 1.2L, DSMR2.2+, Wireless, G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G6 RF1 e WL', 'G6, DSMR 4, Wireless, G-meter, Itron, Valveless', 'mbusmeter', 'Itron', 'ITR'],
				['G6SMV', 'G6, DSMR2.2+, Wired, G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G6SMV-temp', 'G6, DSMR2.2+, Wired, Temp-Corrected G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['G6SRT', 'Flonidan G6 SMR5', 'mbusmeter', 'Flonidan', 'FLO'],
				['G6SRV', 'G6, DSMR2.2+, Wireless, G-meter, Flonidan', 'mbusmeter', 'Flonidan', 'FLO'],
				['GWI UG-G10_ICS-V1 SMR5 WL', 'GWI SMR5 G10', 'mbusmeter', 'GWI', 'GWI'],
				['GWI UG-G16_ICS-V1 SMR5 WL', 'GWI SMR5 G16', 'mbusmeter', 'GWI', 'GWI'],
				['GWI UG-G25_ICS-V1 SMR5 WL', 'GWI_SMR5_G25', 'mbusmeter', 'GWI', 'GWI'],
				['Iskraemeco-S CDMA SMR5.1 energy meter', 'Single phase CDMA SMR5.1 energy meter, manufacturer Iskraemeco', 'Iskraemeco CDMA SMR5.1', 'Iskraemeco', 'ISK'],
				['Iskraemeco-S LTE SMR5.2 energy meter', 'Single phase LTE SMR5.1 energy meter, manufacturer Iskraemeco', 'Iskraemeco LTE SMR5.1', 'Iskraemeco', 'ISK'],
				['Iskraemeco-S SMR5.1 GPRS energy meter', 'Single phase SMR5.1 energy meter, manufacturer Iskraemeco', 'Iskraemeco SMR5.1', 'Iskraemeco', 'ISK'],
				['Iskraemeco-T CDMA SMR5.1 energy meter', 'Poly phase CDMA SMR5.1 energy meter, manufacturer Iskraemeco', 'Iskraemeco CDMA SMR5.1', 'Iskraemeco', 'ISK'],
				['Iskraemeco-T LTE SMR5.2 energy meter', 'Poly phase LTE SMR5.1 energy meter, manufacturer Iskraemeco', 'Iskraemeco LTE SMR5.1', 'Iskraemeco', 'ISK'],
				['Iskraemeco-T SMR5.1 GPRS energy meter', 'Poly phase SMR5.1 energy meter, manufacturer Iskraemeco', 'Iskraemeco SMR5.1', 'Iskraemeco', 'ISK'],
				['Kaifa-S CDMA DSMR4.1 energy meter', 'Single phase CDMA DSMR4.1 energy meter, manufacturer Kaifa', 'Kaifa CDMA DSMR4.1', 'Kaifa', 'KAI'],
				['Kaifa-S DSMR4.1 energy meter', 'Single phase DSMR4.1 energy meter, manufacturer Kaifa', 'Kaifa DSMR4.1', 'Kaifa', 'KAI'],
				['Kaifa-T CDMA DSMR4.1 energy meter', 'Poly phase CDMA DSMR4.1 energy meter, manufacturer Kaifa', 'Kaifa CDMA DSMR4.1', 'Kaifa', 'KAI'],
				['Kaifa-T DSMR4.1 energy meter', 'Poly phase DSMR4.1 energy meter, manufacturer Kaifa', 'Kaifa DSMR4.1', 'Kaifa', 'KAI'],
				['KWH_ITRON_DSMR4 1-phase', 'Single phase KWH_ITRON_DSMR4', 'itron kwh dsmr4 meter', 'Itron', 'ITR'],
				['KWH_ITRON_DSMR4 3-phase', 'Poly phase KWH_ITRON_DSMR4', 'itron kwh dsmr4 meter', 'Itron', 'ITR'],
				['Landis and Gyr-S CDMA SMR5.1 energy meter', 'Single phase CDMA SMR5.1 energy meter, manufacturer Landis and Gyr', 'Landis and Gyr CDMA SMR5.1', 'Landis & Gyr', 'LGB'],
				['Landis and Gyr-S E360 CDMA SMR5.1 energy meter', 'Single phase CDMA SMR5.1 energy meter, manufacturer Landis and Gyr, type E360', 'Landis and Gyr E360 CDMA SMR5.1', 'Landis & Gyr', 'LGB'],
				['Landis and Gyr-S E360 SMR5.1 energy meter', 'Single phase SMR5.1 energy meter, manufacturer Landis and Gyr, type E360', 'Landis and Gyr E360 SMR5.1', 'Landis & Gyr', 'LGB'],
				['Landis and Gyr-S SMR5.1 energy meter', 'Single phase SMR5.1 energy meter, manufacturer Landis and Gyr', 'Landis and Gyr SMR5.1', 'Landis & Gyr', 'LGB'],
				['Landis and Gyr-T CDMA SMR5.1 energy meter', 'Poly phase CDMA SMR5.1 energy meter, manufacturer Landis and Gyr', 'Landis and Gyr CDMA SMR5.1', 'Landis & Gyr', 'LGB'],
				['Landis and Gyr-T E360 CDMA SMR5.1 energy meter', 'Poly phase CDMA SMR5.1 energy meter, manufacturer Landis and Gyr, type E360', 'Landis and Gyr E360 CDMA SMR5.1', 'Landis & Gyr', 'LGB'],
				['Landis and Gyr-T E360 SMR5.1 energy meter', 'Poly phase SMR5.1 energy meter, manufacturer Landis and Gyr, type E360', 'Landis and Gyr E360 SMR5.1', 'Landis & Gyr', 'LGB'],
				['Landis and Gyr-T SMR5.1 energy meter', 'Poly phase SMR5.1 energy meter, manufacturer Landis and Gyr', 'Landis and Gyr SMR5.1', 'Landis & Gyr', 'LGB'],
				['LG350-S DSMR4.1 energy meter', 'Single phase DSMR4.1 energy meter, manufacturer Landis + Gyr, type 350', 'Xemex DSMR4.1', 'Landis & Gyr', 'LGB'],
				['LG350-T DSMR4.1 energy meter', 'Poly phase DSMR4.1 energy meter, manufacturer Landis + Gyr, type 350', 'Xemex DSMR4.1', 'Landis & Gyr', 'LGB'],
				['MA105', '1-phase, DSMR4, GPRS, E-meter, Kaifa', 'kaifa kwh dsmr4 meter', 'Kaifa', 'KAI'],
				['MA105A', 'CDMA 1 fase', 'kaifa kwh dsmr4 cdma meter', 'Kaifa', 'KAI'],
				['MA304', '3-phase, DSMR4, GPRS, E-meter, Kaifa', 'kaifa kwh dsmr4 meter', 'Kaifa', 'KAI'],
				['MA304A', 'CDMA 3 fase', 'kaifa kwh dsmr4 cdma meter', 'Kaifa', 'KAI'],
				['ME372', '1-phase, Pre-NTA, GPRS, E-meter, Iskraemeco', 'iskraemeco gprs', 'Iskraemeco', 'ISK'],
				['ME382-D1 GPRS', '1-phase, DSMR2.2+, GPRS, E-meter, Iskraemeco', 'iskraemeco gprs nta', 'Iskraemeco', 'ISK'],
				['MT372', '3-phase, Pre-NTA, GPRS, E-meter, Iskraemeco', 'iskraemeco gprs', 'Iskraemeco', 'ISK'],
				['MT382-D2 GPRS', '3-phase, DSMR2.2+, GPRS, E-meter, Iskraemeco', 'iskraemeco gprs nta', 'Iskraemeco', 'ISK'],
				['UniFloG4SRTDSMR422', 'Flonidan G4 DSMR 4.2.2', 'mbusmeter', 'Flonidan', 'FLO'],
				['ZCF2AD2 1phCB DSMR4', '1-phase, DSMR4, GPRS, E-meter, Landis+Gyr', 'lg350 kwh dsmr4 meter', 'Landis & Gyr', 'LGB'],
				['ZCF2AD2 CDMA SMR5', 'KWH_LG_CDMA_SMR5 1-phase', 'KWH_LG_CDMA_SMR5', 'Landis & Gyr', 'LGB'],
				['ZCF2AD2 GPRS SMR5', 'KWH_LG_SMR5 1-phase', 'KWH_LG_SMR5', 'Landis & Gyr', 'LGB'],
				['ZMF2AD2 3phCB DSMR4', '3-phase, DSMR4, GPRS, E-meter, Landis+Gyr', 'lg350 kwh dsmr4 meter', 'Landis & Gyr', 'LGB'],
				['ZMF2AD2 CDMA SMR5', 'KWH_LG_CDMA_SMR5 3-phase', 'KWH_LG_CDMA_SMR5', 'Landis & Gyr', 'LGB'],
				['ZMF2AD2 GPRS SMR5', 'KWH_LG_SMR5 3-phase', 'KWH_LG_SMR5', 'Landis & Gyr', 'LGB'] ] LOOP

		IF NOT EXISTS (
			SELECT 1
			  FROM manufacturer
			 WHERE code = device_models[5]
			) THEN
			
		INSERT INTO manufacturer(
			id, code, name, use_prefix)
		VALUES (
			nextval('manufacturer_id_seq'), 
			device_models[5], 
			device_models[4], 
			false);
	
		end if;
		
		IF NOT EXISTS (
			SELECT 1
			  FROM device_model
			 WHERE model_code = device_models[1]
			) THEN

		INSERT INTO device_model(
			id, creation_time, modification_time, version, manufacturer_id, model_code, description, file_storage)
		VALUES (
			nextval('device_model_id_seq'),
			CURRENT_TIMESTAMP,
			CURRENT_TIMESTAMP,
			0,
			(SELECT manufacturerid FROM manufacturer WHERE code = device_models[5]),
			device_models[1],
			device_models[2],
			true);

		END IF;

    END LOOP;
	
END$$;