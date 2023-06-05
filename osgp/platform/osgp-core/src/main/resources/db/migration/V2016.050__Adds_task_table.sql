-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- Creates the table for task along with the proper permissions
CREATE TABLE task (
    id BIGINT NOT NULL,
    creation_time TIMESTAMP WITHOUT TIME ZONE,
    modification_time TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT,
    task_identification VARCHAR(40),
    task_status VARCHAR(15),
    start_time TIMESTAMP WITHOUT TIME ZONE,
    end_time TIMESTAMP WITHOUT TIME ZONE
        
);

ALTER TABLE public.task OWNER TO osp_admin;

-- Creates the sequence for task id
CREATE SEQUENCE task_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE task_id_seq OWNED BY task.id;
 
ALTER TABLE ONLY task ALTER COLUMN id SET DEFAULT nextval('task_id_seq'::regclass);

ALTER TABLE ONLY task_id_seq OWNER TO osp_admin;

ALTER TABLE ONLY task ADD CONSTRAINT task_pkey PRIMARY KEY (id);

GRANT ALL ON public.task TO osp_admin;

