-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE public.pending_set_schedule_request
ADD COLUMN device_uid varchar(255) NOT NULL;
