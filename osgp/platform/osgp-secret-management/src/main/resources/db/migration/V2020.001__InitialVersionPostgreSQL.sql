-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
      BEGIN
		 IF NOT EXISTS(
                 SELECT 1 FROM information_schema.tables WHERE table_schema = current_schema AND table_name = 'encryption_key_reference')
            THEN
                 CREATE TABLE "encryption_key_reference"
                    (
                        id                  BIGINT    PRIMARY KEY,
                        reference           CHARACTER VARYING(32)       NOT NULL,
                        encryption_provider_type CHARACTER VARYING(32)  NOT NULL,
                        valid_from          timestamp without time zone NOT NULL,
                        valid_to            timestamp without time zone,
                        creation_time       timestamp without time zone NOT NULL,
                        modification_time   timestamp without time zone NOT NULL DEFAULT now(),
                        modified_by         CHARACTER VARYING(64)       NOT NULL,
                        version             BIGINT                      NOT NULL DEFAULT 0
                    );
                 ALTER TABLE "encryption_key_reference" OWNER TO osp_admin;
                 CREATE SEQUENCE encryption_key_reference_id_seq
                        START WITH 1
                        INCREMENT BY 1
                        NO MINVALUE
                        NO MAXVALUE
                        CACHE 1;
                 ALTER TABLE public.encryption_key_reference ALTER COLUMN id SET DEFAULT nextval('encryption_key_reference_id_seq');
  				 ALTER TABLE public.encryption_key_reference_id_seq OWNER TO osp_admin;
				 ALTER SEQUENCE public.encryption_key_reference_id_seq OWNED BY encryption_key_reference.id;
  				 CREATE INDEX encryption_key_reference_ix_valid_from
                        ON public.encryption_key_reference USING btree
                        (valid_from DESC)
                        TABLESPACE pg_default;
                 CREATE INDEX encryption_key_reference_ix_valid_to
                        ON public.encryption_key_reference USING btree
                        (valid_to DESC)
                        TABLESPACE pg_default;
         END IF;

         IF NOT EXISTS(
                 SELECT 1 FROM information_schema.tables WHERE table_schema = current_schema AND table_name = 'encrypted_secret')
            THEN
                 CREATE TABLE "encrypted_secret"
                    (
                        id                      BIGINT    PRIMARY KEY,
                        device_identification   CHARACTER VARYING(32)   NOT NULL,
                        secret_type             CHARACTER VARYING(32)   NOT NULL,
                        encoded_secret          CHARACTER VARYING(64)   NOT NULL,
                        creation_time           timestamp without time zone NOT NULL DEFAULT now(),
                        encryption_key_reference_id BIGINT              NOT NULL,
                        CONSTRAINT encryption_key_reference_fk FOREIGN KEY (encryption_key_reference_id)
                            REFERENCES public.encryption_key_reference (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION
                    );
                    ALTER TABLE "encrypted_secret" OWNER TO osp_admin;
                    CREATE SEQUENCE encrypted_secret_id_seq
                        START WITH 1
                        INCREMENT BY 1
                        NO MINVALUE
                        NO MAXVALUE
                        CACHE 1;
                    ALTER TABLE public.encrypted_secret_id_seq OWNER TO osp_admin;
					ALTER TABLE public.encrypted_secret ALTER COLUMN id SET DEFAULT nextval('encrypted_secret_id_seq');
  				    ALTER SEQUENCE public.encrypted_secret_id_seq OWNED BY encrypted_secret.id;
  				    CREATE INDEX encrypted_secret_ix_device_identification ON encrypted_secret (device_identification);
                    CREATE INDEX encrypted_secret_ix_secret_type ON encrypted_secret (secret_type);
                    CREATE INDEX encrypted_secret_ix_creation_time ON encrypted_secret (creation_time);
         END IF;
    END;
$$