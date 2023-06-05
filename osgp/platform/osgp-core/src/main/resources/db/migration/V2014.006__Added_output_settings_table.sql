-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

-- Create table
CREATE TABLE device_output_setting (
    device_id bigint NOT NULL,
    internal_id smallint NOT NULL,
    external_id smallint NOT NULL,
    output_type smallint NOT NULL 
);

-- Set table owner
ALTER TABLE public.device_output_setting OWNER TO osp_admin;

-- Set foreign key
ALTER TABLE device_output_setting ADD CONSTRAINT fk_device_output_setting_device FOREIGN KEY (device_id) REFERENCES device (id);
  
-- Set unique constraint
ALTER TABLE device_output_setting ADD UNIQUE (device_id, internal_id);
  
