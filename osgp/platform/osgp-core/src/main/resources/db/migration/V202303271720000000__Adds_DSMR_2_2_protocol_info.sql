-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   protocol_info
    WHERE  protocol = 'DSMR'
    AND    protocol_version  = '2.2') THEN

INSERT INTO protocol_info(
    creation_time,
    modification_time,
    version,
    protocol,
    protocol_version,
    protocol_variant,
    outgoing_requests_property_prefix,
    incoming_responses_property_prefix,
    incoming_requests_property_prefix,
    outgoing_responses_property_prefix,
    parallel_requests_allowed)
VALUES (
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0,
    'DSMR',
    '2.2',
    null,
    'jms.protocol.dlms.outgoing.requests',
    'jms.protocol.dlms.incoming.responses',
    'jms.protocol.dlms.incoming.requests',
    'jms.protocol.dlms.outgoing.responses',
    true);

END IF;

END;
$$
