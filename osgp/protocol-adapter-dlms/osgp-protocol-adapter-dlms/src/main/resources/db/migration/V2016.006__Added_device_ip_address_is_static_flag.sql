-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE dlms_device ADD COLUMN ip_address_is_static boolean not null default false;