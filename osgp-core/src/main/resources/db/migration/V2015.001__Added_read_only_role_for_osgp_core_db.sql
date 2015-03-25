CREATE USER osgp_core_db_api_user PASSWORD 'osgp_core_db_api_user' NOSUPERUSER;
GRANT SELECT ON public.device TO osgp_core_db_api_user;