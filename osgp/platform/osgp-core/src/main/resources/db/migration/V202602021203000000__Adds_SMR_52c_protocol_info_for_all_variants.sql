/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

DO
$$
BEGIN

DELETE
FROM protocol_info
WHERE protocol = 'SMR'
  AND protocol_version  = '5.2c';

INSERT INTO protocol_info(
    creation_time,
    modification_time,
    protocol,
    protocol_version,
    outgoing_requests_property_prefix,
    incoming_responses_property_prefix,
    incoming_requests_property_prefix,
    outgoing_responses_property_prefix,
    parallel_requests_allowed,
    protocol_variant)
VALUES
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SMR', '5.2c', 'jms.protocol.dlms.outgoing.cdma.requests', 'jms.protocol.dlms.incoming.cdma.responses', 'jms.protocol.dlms.incoming.cdma.requests', 'jms.protocol.dlms.outgoing.cdma.responses', true, 'CDMA'),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SMR', '5.2c', 'jms.protocol.dlms.outgoing.lte.requests', 'jms.protocol.dlms.incoming.lte.responses', 'jms.protocol.dlms.incoming.lte.requests', 'jms.protocol.dlms.outgoing.lte.responses', true, 'LTE'),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SMR', '5.2c', 'jms.protocol.dlms.outgoing.gprs.requests', 'jms.protocol.dlms.incoming.gprs.responses', 'jms.protocol.dlms.incoming.gprs.requests', 'jms.protocol.dlms.outgoing.gprs.responses', true, 'GPRS'),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SMR', '5.2c', 'jms.protocol.dlms.outgoing.default.requests', 'jms.protocol.dlms.incoming.default.responses', 'jms.protocol.dlms.incoming.default.requests', 'jms.protocol.dlms.outgoing.default.responses', true, null);

END;
$$