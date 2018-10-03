DO
$$
BEGIN

  -- New event NTP_SYNC_SUCCESS = 43 has been introduced.
  -- Migrage existing event values (43, 44, 45) to new value.
  UPDATE event SET event = 46 WHERE event = 45;
  UPDATE event SET event = 45 WHERE event = 44;
  UPDATE event SET event = 44 WHERE event = 43 AND description LIKE 'reportId: A, timeOfEntry%';

END;
$$