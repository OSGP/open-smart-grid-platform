-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

--To add organisationIdentification to a protocol log message, osgp_core_db_api_user needs 
--read access to device_authorization and organisation
GRANT SELECT ON TABLE device_authorization TO osgp_core_db_api_user;
GRANT SELECT ON TABLE organisation TO osgp_core_db_api_user;


