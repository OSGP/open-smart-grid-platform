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
    AND    protocol_version  = '5.1') THEN

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
    '2018-12-10 00:00:00',
    '2018-12-10 00:00:00',
    0,
    'SMR',
    '5.1',
    'protocol-dlms.1_0.osgp-core.1_0.requests',
    'osgp-core.1_0.protocol-dlms.1_0.responses',
    'osgp-core.1_0.protocol-dlms.1_0.requests',
    'protocol-dlms.1_0.osgp-core.1_0.responses',
    true);

END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   firmware_module
    WHERE  description = 'm_bus_driver_active_firmware') THEN

INSERT INTO firmware_module (description) VALUES ('m_bus_driver_active_firmware');

END IF;

END;
$$
