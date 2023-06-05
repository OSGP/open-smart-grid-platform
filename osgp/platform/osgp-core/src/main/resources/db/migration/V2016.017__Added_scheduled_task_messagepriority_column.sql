-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE public.scheduled_task ADD COLUMN messagepriority smallint not null default 4;