CREATE TABLE IF NOT EXISTS throttling_config (
  id smallserial PRIMARY KEY,
  name character varying(100) NOT NULL,
  max_concurrency integer NOT NULL,
  CONSTRAINT throttling_config_max_concurrency_non_negative CHECK (max_concurrency > -1),
  CONSTRAINT throttling_config_name_key UNIQUE (name)
);

ALTER TABLE throttling_config OWNER TO osp_admin;

COMMENT ON TABLE throttling_config IS 'Configuration for distributed shared database throttling.';

COMMENT ON COLUMN throttling_config.id IS 'Unique technical id of this Throttling Config.';
COMMENT ON COLUMN throttling_config.name IS 'Business key; identification of this Throttling Config.';
COMMENT ON COLUMN throttling_config.max_concurrency IS 'Maximum number of concurrent permits to be granted by throttlers applying this Throttling Config.';

CREATE TABLE IF NOT EXISTS client (
  id serial PRIMARY KEY,
  name character varying(100) NOT NULL,
  registered_at timestamp without time zone NOT NULL,
  unregistered_at timestamp without time zone,
  last_seen_at timestamp without time zone,
  CONSTRAINT client_registration_name_key UNIQUE (name)
);

ALTER TABLE client OWNER TO osp_admin;

COMMENT ON TABLE client IS 'Registration of clients taking part in distributed throttling.';

COMMENT ON COLUMN client.id IS 'Unique technical id of this Client.';
COMMENT ON COLUMN client.name IS 'Business key; identification of this Client.';
COMMENT ON COLUMN client.registered_at IS 'Creation timestamp for this Client.';
COMMENT ON COLUMN client.unregistered_at IS 'Deactivation timestamp for this Client.';
COMMENT ON COLUMN client.last_seen_at IS 'Last seen timestamp for this Client.';

CREATE TABLE IF NOT EXISTS permit (
  id bigserial PRIMARY KEY,
  throttling_config_id smallint NOT NULL,
  client_id integer NOT NULL,
  bts_id integer NOT NULL,
  cell_id integer NOT NULL,
  request_id integer NOT NULL,
  created_at timestamp without time zone NOT NULL
);

CREATE INDEX permit_idx ON permit (bts_id, cell_id, throttling_config_id, client_id, request_id);
CREATE UNIQUE INDEX permit_client_request_idx ON permit (client_id, request_id) WHERE request_id > -1;

ALTER TABLE permit OWNER TO osp_admin;

COMMENT ON TABLE permit IS 'Granted permit requests in distributed throttling.';

COMMENT ON COLUMN permit.id IS 'Unique technical id of this permit.';
COMMENT ON COLUMN permit.throttling_config_id IS 'Identification of a throttling configuration the client used requesting this permit.';
COMMENT ON COLUMN permit.client_id IS 'Identification of a throttling client that requested this permit.';
COMMENT ON COLUMN permit.bts_id IS 'Identification of a Base Transceiver Station; determines a network segment with cell_id.';
COMMENT ON COLUMN permit.cell_id IS 'Identification of a Cell belonging with the Base Transceiver Station identified by bts_id.';
COMMENT ON COLUMN permit.request_id IS 'An ID managed by the client, identifying its request that lead to this permit.';
COMMENT ON COLUMN permit.created_at IS 'Creation timestamp for this permit.';
