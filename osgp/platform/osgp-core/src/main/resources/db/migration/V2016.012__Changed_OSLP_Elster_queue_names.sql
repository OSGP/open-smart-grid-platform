-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

UPDATE protocol_info 
SET outgoing_protocol_requests_queue='protocol-oslp-elster.1_0.osgp-core.1_0.requests',
    incoming_protocol_responses_queue='osgp-core.1_0.protocol-oslp-elster.1_0.responses',
    incoming_protocol_requests_queue='osgp-core.1_0.protocol-oslp-elster.1_0.requests',
    outgoing_protocol_responses_queue='protocol-oslp-elster.1_0.osgp-core.1_0.responses' 
WHERE protocol='OSLP ELSTER' and protocol_version='1.0';