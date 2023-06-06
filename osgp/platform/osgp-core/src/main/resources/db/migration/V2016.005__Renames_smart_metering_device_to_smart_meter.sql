REVOKE ALL ON public.smart_metering_device FROM osp_admin;
REVOKE SELECT ON public.smart_metering_device FROM osgp_read_only_ws_user;

ALTER TABLE smart_metering_device RENAME TO smart_meter;

GRANT ALL ON public.smart_meter TO osp_admin;
GRANT SELECT ON public.smart_meter TO osgp_read_only_ws_user;