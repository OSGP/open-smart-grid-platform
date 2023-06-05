-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM domain_info WHERE "domain" = 'DISTRIBUTION_AUTOMATION' AND "domain_version" = '1.0') THEN
  -- Adds the incoming and outgoing queues for the Distribution Automation queues
  INSERT INTO domain_info(creation_time, modification_time, version, domain,
   domain_version, incoming_domain_requests_queue, outgoing_domain_responses_queue,
   outgoing_domain_requests_queue, incoming_domain_responses_queue)
  VALUES (current_date, current_date, 0, 'DISTRIBUTION_AUTOMATION', '1.0',
   'osgp-core.1_0.domain-distributionautomation.1_0.requests',
   'domain-distributionautomation.1_0.osgp-core.1_0.responses',
   'domain-distributionautomation.1_0.osgp-core.1_0.requests',
   'osgp-core.1_0.domain-distributionautomation.1_0.responses'
  );
END IF;

END;
$$
