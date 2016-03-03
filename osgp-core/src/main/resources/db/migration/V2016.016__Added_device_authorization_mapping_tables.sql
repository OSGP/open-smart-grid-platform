CREATE TABLE device_function_group
(
  id integer NOT NULL,
  name character varying(255) NOT NULL,
  CONSTRAINT device_function_group_pkey PRIMARY KEY (id),
  CONSTRAINT device_function_group_name_key UNIQUE (name)
);

ALTER TABLE public.device_function_group OWNER TO osp_admin;
GRANT ALL ON public.device_function_group TO osp_admin;
GRANT SELECT ON public.device_function_group TO osgp_read_only_ws_user;

CREATE TABLE device_function
(
  id integer NOT NULL,
  name character varying(255) NOT NULL,
  CONSTRAINT device_function_pkey PRIMARY KEY (id),
  CONSTRAINT device_function_name_key UNIQUE (name)
);

ALTER TABLE public.device_function OWNER TO osp_admin;
GRANT ALL ON public.device_function TO osp_admin;
GRANT SELECT ON public.device_function TO osgp_read_only_ws_user;

CREATE TABLE device_function_mapping
(
  device_function_group_id integer NOT NULL,
  device_function_id integer NOT NULL,
  CONSTRAINT device_function_mapping_pkey PRIMARY KEY (device_function_group_id, device_function_id),
  CONSTRAINT device_function_mapping_device_function_group_fkey FOREIGN KEY (device_function_group_id)
      REFERENCES device_function_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT device_function_mapping_device_function_fkey FOREIGN KEY (device_function_id)
      REFERENCES device_function (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

ALTER TABLE public.device_function_mapping OWNER TO osp_admin;
GRANT ALL ON public.device_function_mapping TO osp_admin;
GRANT SELECT ON public.device_function_mapping TO osgp_read_only_ws_user;

INSERT INTO device_function_group(id, name) VALUES
  (0, 'OWNER'),
  (1, 'INSTALLATION'),
  (2, 'AD_HOC'),
  (3, 'MANAGEMENT'),
  (4, 'FIRMWARE'),
  (5, 'SCHEDULING'),
  (6, 'TARIFF_SCHEDULING'),
  (7, 'CONFIGURATION'),
  (8, 'MONITORING');

INSERT INTO device_function(id, name) VALUES
  ( 0, 'START_SELF_TEST'),
  ( 1, 'STOP_SELF_TEST'),
  ( 2, 'SET_LIGHT'),
  ( 3, 'GET_DEVICE_AUTHORIZATION'),
  ( 4, 'SET_EVENT_NOTIFICATIONS'),
  ( 5, 'SET_DEVICE_AUTHORIZATION'),
  ( 6, 'GET_EVENT_NOTIFICATIONS'),
  ( 7, 'UPDATE_FIRMWARE'),
  ( 8, 'GET_FIRMWARE_VERSION'),
  ( 9, 'SET_TARIFF_SCHEDULE'),
  (10, 'SET_LIGHT_SCHEDULE'),
  (11, 'SET_CONFIGURATION'),
  (12, 'GET_CONFIGURATION'),
  (13, 'GET_STATUS'),
  (14, 'GET_LIGHT_STATUS'),
  (15, 'GET_TARIFF_STATUS'),
  (16, 'REMOVE_DEVICE'),
  (17, 'GET_ACTUAL_POWER_USAGE'),
  (18, 'GET_POWER_USAGE_HISTORY'),
  (19, 'RESUME_SCHEDULE'),
  (20, 'SET_REBOOT'),
  (21, 'SET_TRANSITION'),
  (22, 'UPDATE_KEY'),
  (23, 'REVOKE_KEY'),
  (24, 'FIND_SCHEDULED_TASKS'),
  (25, 'REGISTER_DEVICE'),
  (26, 'ADD_EVENT_NOTIFICATION'),
  (27, 'ADD_METER'),
  (28, 'FIND_EVENTS'),
  (29, 'REQUEST_PERIODIC_METER_DATA'),
  (30, 'SYNCHRONIZE_TIME'),
  (31, 'REQUEST_SPECIAL_DAYS'),
  (32, 'SET_ALARM_NOTIFICATIONS'),
  (33, 'SET_CONFIGURATION_OBJECT'),
  (34, 'SET_ADMINISTRATIVE_STATUS'),
  (35, 'GET_ADMINISTRATIVE_STATUS'),
  (36, 'SET_ACTIVITY_CALENDAR'),
  (37, 'REQUEST_ACTUAL_METER_DATA'),
  (38, 'READ_ALARM_REGISTER'),
  (39, 'PUSH_NOTIFICATION_ALARM'),
  (40, 'PUSH_NOTIFICATION_SMS'),
  (41, 'SEND_WAKEUP_SMS'),
  (42, 'GET_SMS_DETAILS'),
  (43, 'SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER'),
  (44, 'REPLACE_KEYS'),
  (45, 'SET_PUSH_SETUP_ALARM'),
  (46, 'SET_PUSH_SETUP_SMS'),
  (47, 'GET_CONFIGURATION_OBJECTS'),
  (48, 'SWITCH_CONFIGURATION_BANK'),
  (49, 'SWITCH_FIRMWARE'),
  (50, 'UPDATE_DEVICE_SSL_CERTIFICATION'),
  (51, 'SET_DEVICE_VERIFICATION_KEY');

INSERT INTO device_function_mapping(device_function_group_id, device_function_id) VALUES
  (0,  0),
  (0,  1),
  (0,  2),
  (0,  3),
  (0,  4),
  (0,  5),
  (0,  6),
  (0,  7),
  (0,  8),
  (0,  9),
  (0, 10),
  (0, 11),
  (0, 12),
  (0, 13),
  (0, 14),
  (0, 15),
  (0, 16),
  (0, 17),
  (0, 18),
  (0, 19),
  (0, 20),
  (0, 21),
  (0, 22),
  (0, 23),
  (0, 24),
  (0, 27),
  (0, 28),
  (0, 29),
  (0, 30),
  (0, 31),
  (0, 32),
  (0, 33),
  (0, 34),
  (0, 35),
  (0, 36),
  (0, 37),
  (0, 38),
  (0, 41),
  (0, 42),
  (0, 43),
  (0, 44),
  (0, 45),
  (0, 46),
  (0, 47),
  (0, 48),
  (0, 49),
  (0, 50),
  (0, 51),
  (1,  0),
  (1,  1),
  (1,  3),
  (1, 27),
  (2,  2),
  (2,  3),
  (2, 13),
  (2, 14),
  (2, 15),
  (2, 19),
  (2, 20),
  (2, 21),
  (3,  3),
  (3,  4),
  (3,  6),
  (3, 16),
  (3, 22),
  (3, 23),
  (3, 50),
  (3, 51),
  (4,  3),
  (4,  7),
  (4,  8),
  (4, 49),
  (5,  3),
  (5, 10),
  (6,  3),
  (6,  9),
  (7,  3),
  (7, 11),
  (7, 12),
  (7, 48),
  (8,  3),
  (8, 17),
  (8, 18);
