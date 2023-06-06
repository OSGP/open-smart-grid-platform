-- Creates the table for rtu devices along with the proper permissions
CREATE TABLE rtu_device (
    id bigserial NOT NULL
);

ALTER TABLE public.rtu_device OWNER TO osp_admin;

ALTER TABLE ONLY rtu_device ADD CONSTRAINT rtu_device_pkey PRIMARY KEY (id);

GRANT ALL ON public.rtu_device TO osp_admin;
GRANT SELECT ON public.rtu_device TO osgp_read_only_ws_user;


-- Inserts the device function mapping for microgrids functions
insert into device_function_mapping  (function_group, function) values ('OWNER' ,'GET_DATA');
insert into device_function_mapping  (function_group, function) values ('AD_HOC' ,'GET_DATA');
insert into device_function_mapping  (function_group, function) values ('OWNER' ,'SET_SETPOINT');
insert into device_function_mapping  (function_group, function) values ('AD_HOC' ,'SET_SETPOINT');


-- Adds the incoming and outgoing queues for the microgrids domain queues
INSERT INTO domain_info(creation_time, modification_time, version, domain,
 domain_version, incoming_domain_requests_queue, outgoing_domain_responses_queue,
 outgoing_domain_requests_queue, incoming_domain_responses_queue)
VALUES (current_date, current_date, 0, 'MICROGRIDS', '1.0',
 'osgp-core.1_0.domain-microgrids.1_0.requests',
 'domain-microgrids.1_0.osgp-core.1_0.responses',
 'domain-microgrids.1_0.osgp-core.1_0.requests',
 'osgp-core.1_0.domain-microgrids.1_0.responses'
);
