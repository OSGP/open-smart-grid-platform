-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- new privileges for firmware, device_model and manufacturer
 
GRANT ALL ON public.firmware TO osp_admin;
GRANT SELECT ON public.firmware TO osgp_read_only_ws_user;

GRANT ALL ON public.device_model TO osp_admin;
GRANT SELECT ON public.device_model TO osgp_read_only_ws_user;

GRANT ALL ON public.manufacturer TO osp_admin;
GRANT SELECT ON public.manufacturer TO osgp_read_only_ws_user;