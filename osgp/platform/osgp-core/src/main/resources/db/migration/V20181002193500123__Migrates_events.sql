DO
$$
BEGIN

  -- New event NTP_SYNC_SUCCESS = 43 has been introduced.
  -- Migrate existing event values (43, 44, 45) to new value.
  UPDATE event SET event = event + 1 WHERE event >= 43;

END;
$$
