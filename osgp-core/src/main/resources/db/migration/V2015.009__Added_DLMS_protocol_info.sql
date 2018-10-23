INSERT INTO protocol_info(id, creation_time, modification_time, version, 
                          protocol, protocol_version, 
                          outgoing_protocol_requests_queue, incoming_protocol_responses_queue, 
                          incoming_protocol_requests_queue, outgoing_protocol_responses_queue)
                  VALUES (nextval('protocol_info_id_seq'),'2015-07-26 00:00:00','2015-07-26 00:00:00',0,
                          'DLMS','1.0',
                          'protocol-dlms.1_0.osgp-core.1_0.requests','osgp-core.1_0.protocol-dlms.1_0.responses',
                          'osgp-core.1_0.protocol-dlms.1_0.requests','protocol-dlms.1_0.osgp-core.1_0.responses');