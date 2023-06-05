-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- This user should be created by our create database and users script, not using flyway


--CREATE USER osgp_core_db_api_user PASSWORD 'osgp_core_db_api_user' NOSUPERUSER;
GRANT SELECT ON public.device TO osgp_core_db_api_user;