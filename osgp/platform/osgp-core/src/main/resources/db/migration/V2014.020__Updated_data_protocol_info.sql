-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

UPDATE protocol_info 
	SET  protocol = 'OSLP', 
		requests_queue = 'osgp.protocol.oslp.1_0.requests', 
		responses_queue = 'osgp.protocol.oslp.1_0.responses'
WHERE protocol = 'OSLP 1.0';