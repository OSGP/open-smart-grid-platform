-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

INSERT INTO device_protocol_info(id, creation_time, modification_time, version, protocol, protocol_version, requests_queue, responses_queue)
    VALUES (nextval('device_protocol_info_id_seq'),'2014-10-23 00:00:00','2014-10-23 00:00:00',0,'OSLP 1.0','1.0','osgp.protocol.adapter.oslp.requests','osgp.protocol.adapter.oslp.responses');
