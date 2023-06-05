-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- Adds the incoming and outgoing queues for the Smart Metering queues
INSERT INTO domain_info(creation_time, modification_time, version, domain,
 domain_version, incoming_domain_requests_queue, outgoing_domain_responses_queue,
 outgoing_domain_requests_queue, incoming_domain_responses_queue)
VALUES (current_date, current_date, 0, 'SMART_METERING', '1.0',
 'osgp-core.1_0.domain-smartmetering.1_0.requests',
 'domain-smartmetering.1_0.osgp-core.1_0.responses',
 'domain-smartmetering.1_0.osgp-core.1_0.requests',
 'osgp-core.1_0.domain-smartmetering.1_0.responses'
);
