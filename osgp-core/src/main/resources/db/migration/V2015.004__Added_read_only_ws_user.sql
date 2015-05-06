-- This user should be created by our create database and users script, not using flyway


--CREATE USER osgp_read_only_ws_user WITH ENCRYPTED PASSWORD 'qMB3i7RLRVYH0xM' NOSUPERUSER;
GRANT SELECT ON ALL SEQUENCES IN SCHEMA public TO osgp_read_only_ws_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO osgp_read_only_ws_user;

-- source: http://jamie.curle.io/blog/creating-a-read-only-user-in-postgres/
-- CREATE USER backup_user  WITH ENCRYPTED PASSWORD 'password';
-- GRANT CONNECT ON DATABASE production to backup_user;
-- \c production
-- GRANT USAGE ON SCHEMA public to backup_user; /*thanks Dominic!*/
-- GRANT SELECT ON ALL SEQUENCES IN SCHEMA public TO backup_user;
-- GRANT SELECT ON ALL TABLES IN SCHEMA public TO backup_user;