-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- First, rename the queue names.
UPDATE domain_info SET requests_queue='osgp.domain.core.1_0.out.requests' WHERE id=1;
UPDATE domain_info SET responses_queue='osgp.domain.core.1_0.out.responses' WHERE id=1;

UPDATE domain_info SET requests_queue='osgp.domain.publiclighting.1_0.out.requests' WHERE id=2;
UPDATE domain_info SET responses_queue='osgp.domain.publiclighting.1_0.out.responses' WHERE id=2;

UPDATE domain_info SET requests_queue='osgp.domain.tariffswitching.1_0.out.requests' WHERE id=3;
UPDATE domain_info SET responses_queue='osgp.domain.tariffswitching.1_0.out.responses' WHERE id=3;

-- Then, proceed with adding the new 'in' queue columns.
ALTER TABLE domain_info ADD COLUMN requests_queue_in varchar(255);
ALTER TABLE domain_info ADD COLUMN responses_queue_in varchar(255);

-- Rename the existing queue columns to 'out'.
ALTER TABLE domain_info RENAME COLUMN requests_queue TO requests_queue_out;
ALTER TABLE domain_info RENAME COLUMN responses_queue TO responses_queue_out;

-- Add the 'in' queue names.
UPDATE domain_info SET requests_queue_in='osgp.domain.core.1_0.in.requests' WHERE id=1;
UPDATE domain_info SET responses_queue_in='osgp.domain.core.1_0.in.responses' WHERE id=1;

UPDATE domain_info SET requests_queue_in='osgp.domain.publiclighting.1_0.in.requests' WHERE id=2;
UPDATE domain_info SET responses_queue_in='osgp.domain.publiclighting.1_0.in.responses' WHERE id=2;

UPDATE domain_info SET requests_queue_in='osgp.domain.tariffswitching.1_0.in.requests' WHERE id=3;
UPDATE domain_info SET responses_queue_in='osgp.domain.tariffswitching.1_0.in.responses' WHERE id=3;