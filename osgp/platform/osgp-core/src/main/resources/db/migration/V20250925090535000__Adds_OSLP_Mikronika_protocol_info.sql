-- SPDX-FileCopyrightText: 2025 Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0
DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   protocol_info
    WHERE  protocol = 'OSLP MIKRONIKA'
    AND    protocol_version  = '1.0') THEN

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
    'OSLP MIKRONIKA',
    '1.0',
    null,
    'jms.protocol.oslp.mikronika.outgoing.requests',
    'jms.protocol.oslp.mikronika.incoming.responses',
    'jms.protocol.oslp.mikronika.incoming.requests',
    'jms.protocol.oslp.mikronika.outgoing.responses',
    true);

END IF;

END;
$$

