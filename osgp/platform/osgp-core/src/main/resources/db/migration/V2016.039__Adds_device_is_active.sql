-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

alter table device add is_active boolean;
update device set is_active=is_activated;