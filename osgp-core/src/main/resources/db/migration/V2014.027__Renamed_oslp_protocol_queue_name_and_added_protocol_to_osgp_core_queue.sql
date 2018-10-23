-- First, rename the queue names.
UPDATE protocol_info SET requests_queue='osgp.adapter.protocol.oslp.1_0.out.requests' WHERE id=1;
UPDATE protocol_info SET responses_queue='osgp.adapter.protocol.oslp.1_0.out.responses' WHERE id=1;

UPDATE protocol_info SET requests_queue='osgp.adapter.protocol.oslp.1_1.out.requests' WHERE id=2;
UPDATE protocol_info SET responses_queue='osgp.adapter.protocol.oslp.1_1.out.responses' WHERE id=2;

-- Then, proceed with adding the new 'in' queue columns.
ALTER TABLE protocol_info ADD COLUMN requests_queue_in varchar(255);
ALTER TABLE protocol_info ADD COLUMN responses_queue_in varchar(255);

-- Rename the existing queue columns to 'out'.
ALTER TABLE protocol_info RENAME COLUMN requests_queue TO requests_queue_out;
ALTER TABLE protocol_info RENAME COLUMN responses_queue TO responses_queue_out;

-- Add the 'in' queue names.
UPDATE protocol_info SET requests_queue_in='osgp.adapter.protocol.oslp.1_0.in.requests' WHERE id=1;
UPDATE protocol_info SET responses_queue_in='osgp.adapter.protocol.oslp.1_0.in.responses' WHERE id=1;

UPDATE protocol_info SET requests_queue_in='osgp.adapter.protocol.oslp.1_1.in.requests' WHERE id=2;
UPDATE protocol_info SET responses_queue_in='osgp.adapter.protocol.oslp.1_1.in.responses' WHERE id=2;