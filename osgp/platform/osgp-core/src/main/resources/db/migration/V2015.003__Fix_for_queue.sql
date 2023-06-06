-- UPDATE QUEUE NAMES FOR DOMAIN ADMIN 1.0
UPDATE domain_info 
SET
	outgoing_domain_requests_queue='domain-admin.1_0.osgp-core.1_0.requests',
	incoming_domain_responses_queue='osgp-core.1_0.domain-admin.1_0.responses'
WHERE
	domain='ADMIN'
	AND domain_version='1.0';

-- UPDATE QUEUE NAMES FOR DOMAIN CORE 1.0
UPDATE domain_info 
SET
	outgoing_domain_requests_queue='domain-core.1_0.osgp-core.1_0.requests',
	incoming_domain_responses_queue='osgp-core.1_0.domain-core.1_0.responses'
WHERE
	domain='CORE'
	AND domain_version='1.0';

-- UPDATE QUEUE NAMES FOR DOMAIN PUBLIC LIGHTING 1.0
UPDATE domain_info 
SET
	outgoing_domain_requests_queue='domain-publiclighting.1_0.osgp-core.1_0.requests',
	incoming_domain_responses_queue='osgp-core.1_0.domain-publiclighting.1_0.responses'
WHERE
	domain='PUBLIC_LIGHTING'
	AND domain_version='1.0';

-- UPDATE QUEUE NAMES FOR DOMAIN TARIFF SWITCHING 1.0
UPDATE domain_info 
SET
	outgoing_domain_requests_queue='domain-tariffswitching.1_0.osgp-core.1_0.requests',
	incoming_domain_responses_queue='osgp-core.1_0.domain-tariffswitching.1_0.responses'
WHERE
	domain='TARIFF_SWITCHING'
	AND domain_version='1.0';

