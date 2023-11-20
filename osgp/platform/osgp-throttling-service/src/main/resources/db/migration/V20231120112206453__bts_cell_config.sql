CREATE TABLE IF NOT EXISTS bts_cell_config (
  id smallserial PRIMARY KEY,
  bts_id integer NOT NULL,
  cell_id integer NOT NULL,
  max_concurrency integer NOT NULL,
  CONSTRAINT bts_cell_config_max_concurrency_non_negative CHECK (max_concurrency > -1),
  CONSTRAINT bts_cell_config_bts_id_cell_id_key UNIQUE (bts_id, cell_id)
);

ALTER TABLE bts_cell_config OWNER TO osp_admin;

COMMENT ON TABLE bts_cell_config IS 'Configuration for distributed shared database throttling per bts cell.';

COMMENT ON COLUMN bts_cell_config.id IS 'Unique technical id of this Bts Cell Config.';
COMMENT ON COLUMN bts_cell_config.bts_id IS 'Identification of a Base Transceiver Station; determines a network segment with cell_id.';
COMMENT ON COLUMN bts_cell_config.cell_id IS 'Identification of a Cell belonging with the Base Transceiver Station identified by bts_id.';
COMMENT ON COLUMN bts_cell_config.max_concurrency IS 'Maximum number of concurrent permits to be granted by throttlers applying this Bts Cell Config.';