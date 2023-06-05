-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   protocol_info
    WHERE  protocol = 'SMR'
    AND    protocol_version  = '5.0.0') THEN

INSERT INTO protocol_info(
    creation_time,
    modification_time,
    version,
    protocol,
    protocol_version,
    outgoing_protocol_requests_queue,
    incoming_protocol_responses_queue,
    incoming_protocol_requests_queue,
    outgoing_protocol_responses_queue,
    parallel_requests_allowed)
VALUES (
    '2019-09-16 00:00:00',
    '2019-09-16 00:00:00',
    0,
    'SMR',
    '5.0.0',
    'protocol-dlms.1_0.osgp-core.1_0.requests',
    'osgp-core.1_0.protocol-dlms.1_0.responses',
    'osgp-core.1_0.protocol-dlms.1_0.requests',
    'protocol-dlms.1_0.osgp-core.1_0.responses',
    true);

END IF;

END;
$$
