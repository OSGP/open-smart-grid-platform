-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- see V2015.005
REVOKE SELECT ON TABLE device_authorization FROM osgp_core_db_api_user;
REVOKE SELECT ON TABLE organisation FROM osgp_core_db_api_user;