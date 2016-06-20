ALTER TABLE protocol_info ADD CONSTRAINT unique_protocol_version UNIQUE (protocol, protocol_version);
