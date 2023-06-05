-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DROP DATABASE IF EXISTS osgp_secret_management;
CREATE DATABASE osgp_secret_management
    WITH
    OWNER = osp_admin
    ENCODING = 'UTF8';