-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

--New queues for Elster protocol

INSERT INTO protocol_info(id
			 ,creation_time
			 ,modification_time
			 ,version
			 ,protocol
			 ,protocol_version
			 ,outgoing_protocol_requests_queue
			 ,incoming_protocol_responses_queue
			 ,incoming_protocol_requests_queue
			 ,outgoing_protocol_responses_queue)
VALUES (nextval('protocol_info_id_seq')
			 ,'2016-02-10 00:00:00'
			 ,'2016-02-10 00:00:00'
			 ,0
			 ,'OSLP ELSTER'
			 ,'1.0'
			 ,'protocol-oslp-elster.1_0.osgp-core.1_0.requests'
			 ,'osgp-core.1_0.protocol-oslp.1_0.responses'
			 ,'osgp-core.1_0.protocol-oslp.1_0.requests'
			 ,'protocol-oslp-elster.1_0.osgp-core.1_0.responses');