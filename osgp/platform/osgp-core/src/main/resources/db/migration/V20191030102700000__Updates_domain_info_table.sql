-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
    WHERE table_schema = current_schema 
    AND     table_name = 'domain_info' 
    AND    column_name = 'incoming_requests_property_prefix') THEN

  ALTER TABLE ONLY domain_info RENAME incoming_domain_requests_queue TO incoming_requests_property_prefix;
  ALTER TABLE ONLY domain_info RENAME incoming_domain_responses_queue TO incoming_responses_property_prefix;
  ALTER TABLE ONLY domain_info RENAME outgoing_domain_requests_queue TO outgoing_requests_property_prefix;
  ALTER TABLE ONLY domain_info RENAME outgoing_domain_responses_queue TO outgoing_responses_property_prefix;
  
  UPDATE domain_info 
  SET
    incoming_requests_property_prefix = 'jms.domain.' || lower(regexp_replace("domain", '_', '')) || '.incoming.requests',
    incoming_responses_property_prefix = 'jms.domain.' || lower(regexp_replace("domain", '_', '')) || '.incoming.responses',
    outgoing_requests_property_prefix = 'jms.domain.' || lower(regexp_replace("domain", '_', '')) || '.outgoing.requests',
    outgoing_responses_property_prefix = 'jms.domain.' || lower(regexp_replace("domain", '_', '')) || '.outgoing.responses'
  ;
  

END IF;

END;
$$
