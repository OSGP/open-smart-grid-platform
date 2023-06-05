-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
    WHERE table_schema = current_schema 
    AND     table_name = 'protocol_info' 
    AND    column_name = 'incoming_requests_property_prefix') THEN

  ALTER TABLE ONLY protocol_info RENAME incoming_protocol_requests_queue TO incoming_requests_property_prefix;
  ALTER TABLE ONLY protocol_info RENAME incoming_protocol_responses_queue TO incoming_responses_property_prefix;
  ALTER TABLE ONLY protocol_info RENAME outgoing_protocol_requests_queue TO outgoing_requests_property_prefix;
  ALTER TABLE ONLY protocol_info RENAME outgoing_protocol_responses_queue TO outgoing_responses_property_prefix;
  
  UPDATE protocol_info 
  SET
    incoming_requests_property_prefix = 'jms.protocol.oslp.incoming.requests',
    incoming_responses_property_prefix = 'jms.protocol.oslp.incoming.responses',
    outgoing_requests_property_prefix = 'jms.protocol.oslp.outgoing.requests',
    outgoing_responses_property_prefix = 'jms.protocol.oslp.outgoing.responses'
  WHERE protocol = 'OSLP ELSTER';
  
  UPDATE protocol_info 
  SET
    incoming_requests_property_prefix = 'jms.protocol.iec60870.incoming.requests',
    incoming_responses_property_prefix = 'jms.protocol.iec60870.incoming.responses',
    outgoing_requests_property_prefix = 'jms.protocol.iec60870.outgoing.requests',
    outgoing_responses_property_prefix = 'jms.protocol.iec60870.outgoing.responses'
  WHERE protocol = '60870-5-104';
  
  UPDATE protocol_info 
  SET
    incoming_requests_property_prefix = 'jms.protocol.iec61850.incoming.requests',
    incoming_responses_property_prefix = 'jms.protocol.iec61850.incoming.responses',
    outgoing_requests_property_prefix = 'jms.protocol.iec61850.outgoing.requests',
    outgoing_responses_property_prefix = 'jms.protocol.iec61850.outgoing.responses'
  WHERE protocol = 'IEC61850';
  
  UPDATE protocol_info 
  SET
    incoming_requests_property_prefix = 'jms.protocol.dlms.incoming.requests',
    incoming_responses_property_prefix = 'jms.protocol.dlms.incoming.responses',
    outgoing_requests_property_prefix = 'jms.protocol.dlms.outgoing.requests',
    outgoing_responses_property_prefix = 'jms.protocol.dlms.outgoing.responses'
  WHERE protocol IN ('DLMS', 'DSMR', 'SMR');
  
  

END IF;

END;
$$
