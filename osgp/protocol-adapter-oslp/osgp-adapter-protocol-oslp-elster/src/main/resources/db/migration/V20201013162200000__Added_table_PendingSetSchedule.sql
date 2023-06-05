-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

CREATE TABLE IF NOT EXISTS public.pending_set_schedule_request
(
    id bigserial NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    modification_time timestamp without time zone NOT NULL,
    version bigint,
    device_identification varchar(40) NOT NULL,
    expired_at timestamp with time zone NOT NULL,
    schedule_message_data_container_dto bytea NOT NULL,
    device_request bytea NOT NULL,
    PRIMARY KEY(id)
);

ALTER TABLE public.pending_set_schedule_request
    OWNER TO osp_admin;
