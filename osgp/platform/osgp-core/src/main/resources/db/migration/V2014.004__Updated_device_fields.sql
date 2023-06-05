-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE public.device DROP COLUMN container_code;
ALTER TABLE public.device ADD COLUMN container_postal_code character varying(10);
ALTER TABLE public.device ADD COLUMN container_number character varying(10);

