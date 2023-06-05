-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE public.scheduled_task ADD COLUMN status smallint;

UPDATE scheduled_task SET status = 0 WHERE status is null;

ALTER TABLE ONLY public.scheduled_task ALTER COLUMN status SET DEFAULT 0;

ALTER TABLE ONLY public.scheduled_task ALTER COLUMN status SET NOT NULL;
