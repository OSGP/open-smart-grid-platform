-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- RENAME DOMAIN INFO QUEUE COLUMNS
ALTER TABLE domain_info RENAME COLUMN requests_queue_out TO incoming_domain_requests_queue;
ALTER TABLE domain_info RENAME COLUMN responses_queue_out TO outgoing_domain_responses_queue;
ALTER TABLE domain_info RENAME COLUMN requests_queue_in TO outgoing_domain_requests_queue;
ALTER TABLE domain_info RENAME COLUMN responses_queue_in TO incoming_domain_responses_queue;

-- UPDATE QUEUE NAMES FOR DOMAIN ADMIN 1.0
UPDATE domain_info 
SET
	incoming_domain_requests_queue='osgp-core.1_0.domain-admin.1_0.requests',
	outgoing_domain_responses_queue='domain-admin.1_0.osgp-core.1_0.responses',
	outgoing_domain_requests_queue='domain-admin.1_0.osgp-core.1_0.responses',
	incoming_domain_responses_queue='osgp-core.1_0.domain-admin.1_0.requests'
WHERE
	domain='ADMIN'
	AND domain_version='1.0';

-- UPDATE QUEUE NAMES FOR DOMAIN CORE 1.0
UPDATE domain_info 
SET
	incoming_domain_requests_queue='osgp-core.1_0.domain-core.1_0.requests',
	outgoing_domain_responses_queue='domain-core.1_0.osgp-core.1_0.responses',
	outgoing_domain_requests_queue='domain-core.1_0.osgp-core.1_0.responses',
	incoming_domain_responses_queue='osgp-core.1_0.domain-core.1_0.requests'
WHERE
	domain='CORE'
	AND domain_version='1.0';

-- UPDATE QUEUE NAMES FOR DOMAIN PUBLIC LIGHTING 1.0
UPDATE domain_info 
SET
	incoming_domain_requests_queue='osgp-core.1_0.domain-publiclighting.1_0.requests',
	outgoing_domain_responses_queue='domain-publiclighting.1_0.osgp-core.1_0.responses',
	outgoing_domain_requests_queue='domain-publiclighting.1_0.osgp-core.1_0.responses',
	incoming_domain_responses_queue='osgp-core.1_0.domain-publiclighting.1_0.requests'
WHERE
	domain='PUBLIC_LIGHTING'
	AND domain_version='1.0';

-- UPDATE QUEUE NAMES FOR DOMAIN TARIFF SWITCHING 1.0
UPDATE domain_info 
SET
	incoming_domain_requests_queue='osgp-core.1_0.domain-tariffswitching.1_0.requests',
	outgoing_domain_responses_queue='domain-tariffswitching.1_0.osgp-core.1_0.responses',
	outgoing_domain_requests_queue='domain-tariffswitching.1_0.osgp-core.1_0.responses',
	incoming_domain_responses_queue='osgp-core.1_0.domain-tariffswitching.1_0.requests'
WHERE
	domain='TARIFF_SWITCHING'
	AND domain_version='1.0';

	
-- RENAME PROTOCOL INFO QUEUE COLUMS
ALTER TABLE protocol_info RENAME COLUMN requests_queue_out TO outgoing_protocol_requests_queue;
ALTER TABLE protocol_info RENAME COLUMN responses_queue_out TO incoming_protocol_responses_queue;
ALTER TABLE protocol_info RENAME COLUMN requests_queue_in TO incoming_protocol_requests_queue;
ALTER TABLE protocol_info RENAME COLUMN responses_queue_in TO outgoing_protocol_responses_queue;

-- UPDATE QUEUE NAMES FOR PROTOCOL OSLP 1.0
UPDATE protocol_info 
SET 
	outgoing_protocol_requests_queue='protocol-oslp.1_0.osgp-core.1_0.requests',
	incoming_protocol_responses_queue='osgp-core.1_0.protocol-oslp.1_0.responses',
	incoming_protocol_requests_queue='osgp-core.1_0.protocol-oslp.1_0.requests',
	outgoing_protocol_responses_queue='protocol-oslp.1_0.osgp-core.1_0.responses'
WHERE 
	protocol='OSLP'
	AND protocol_version='1.0';

-- UPDATE QUEUE NAMES FOR PROTOCOL OSLP 1.1
UPDATE protocol_info 
SET 
	outgoing_protocol_requests_queue='protocol-oslp.1_1.osgp-core.1_0.requests',
	incoming_protocol_responses_queue='osgp-core.1_0.protocol-oslp.1_1.responses',
	incoming_protocol_requests_queue='osgp-core.1_0.protocol-oslp.1_1.requests',
	outgoing_protocol_responses_queue='protocol-oslp.1_1.osgp-core.1_0.responses'
WHERE 
	protocol='OSLP'
	AND protocol_version='1.1';
