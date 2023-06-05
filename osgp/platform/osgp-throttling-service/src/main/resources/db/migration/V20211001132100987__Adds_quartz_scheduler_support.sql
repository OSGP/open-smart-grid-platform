-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
BEGIN

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'qrtz_job_details') THEN
    CREATE TABLE qrtz_job_details
      (
        SCHED_NAME VARCHAR(120) NOT NULL,
        JOB_NAME  VARCHAR(200) NOT NULL,
        JOB_GROUP VARCHAR(200) NOT NULL,
        DESCRIPTION VARCHAR(250) NULL,
        JOB_CLASS_NAME   VARCHAR(250) NOT NULL,
        IS_DURABLE BOOL NOT NULL,
        IS_NONCONCURRENT BOOL NOT NULL,
        IS_UPDATE_DATA BOOL NOT NULL,
        REQUESTS_RECOVERY BOOL NOT NULL,
        JOB_DATA BYTEA NULL,
        PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
    );
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'qrtz_triggers') THEN
    CREATE TABLE qrtz_triggers
      (
        SCHED_NAME VARCHAR(120) NOT NULL,
        TRIGGER_NAME VARCHAR(200) NOT NULL,
        TRIGGER_GROUP VARCHAR(200) NOT NULL,
        JOB_NAME  VARCHAR(200) NOT NULL,
        JOB_GROUP VARCHAR(200) NOT NULL,
        DESCRIPTION VARCHAR(250) NULL,
        NEXT_FIRE_TIME BIGINT NULL,
        PREV_FIRE_TIME BIGINT NULL,
        PRIORITY INTEGER NULL,
        TRIGGER_STATE VARCHAR(16) NOT NULL,
        TRIGGER_TYPE VARCHAR(8) NOT NULL,
        START_TIME BIGINT NOT NULL,
        END_TIME BIGINT NULL,
        CALENDAR_NAME VARCHAR(200) NULL,
        MISFIRE_INSTR SMALLINT NULL,
        JOB_DATA BYTEA NULL,
        PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
        FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
        REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
    );
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'qrtz_simple_triggers') THEN
    CREATE TABLE qrtz_simple_triggers
      (
        SCHED_NAME VARCHAR(120) NOT NULL,
        TRIGGER_NAME VARCHAR(200) NOT NULL,
        TRIGGER_GROUP VARCHAR(200) NOT NULL,
        REPEAT_COUNT BIGINT NOT NULL,
        REPEAT_INTERVAL BIGINT NOT NULL,
        TIMES_TRIGGERED BIGINT NOT NULL,
        PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
        FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    );
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'qrtz_cron_triggers') THEN
    CREATE TABLE qrtz_cron_triggers
      (
        SCHED_NAME VARCHAR(120) NOT NULL,
        TRIGGER_NAME VARCHAR(200) NOT NULL,
        TRIGGER_GROUP VARCHAR(200) NOT NULL,
        CRON_EXPRESSION VARCHAR(120) NOT NULL,
        TIME_ZONE_ID VARCHAR(80),
        PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
        FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    );
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'qrtz_simprop_triggers') THEN
    CREATE TABLE qrtz_simprop_triggers
      (
        SCHED_NAME VARCHAR(120) NOT NULL,
        TRIGGER_NAME VARCHAR(200) NOT NULL,
        TRIGGER_GROUP VARCHAR(200) NOT NULL,
        STR_PROP_1 VARCHAR(512) NULL,
        STR_PROP_2 VARCHAR(512) NULL,
        STR_PROP_3 VARCHAR(512) NULL,
        INT_PROP_1 INT NULL,
        INT_PROP_2 INT NULL,
        LONG_PROP_1 BIGINT NULL,
        LONG_PROP_2 BIGINT NULL,
        DEC_PROP_1 NUMERIC(13,4) NULL,
        DEC_PROP_2 NUMERIC(13,4) NULL,
        BOOL_PROP_1 BOOL NULL,
        BOOL_PROP_2 BOOL NULL,
        PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
        FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    );
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'qrtz_blob_triggers') THEN
    CREATE TABLE qrtz_blob_triggers
      (
        SCHED_NAME VARCHAR(120) NOT NULL,
        TRIGGER_NAME VARCHAR(200) NOT NULL,
        TRIGGER_GROUP VARCHAR(200) NOT NULL,
        BLOB_DATA BYTEA NULL,
        PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
        FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
            REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    );
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'qrtz_calendars') THEN
    CREATE TABLE qrtz_calendars
      (
        SCHED_NAME VARCHAR(120) NOT NULL,
        CALENDAR_NAME  VARCHAR(200) NOT NULL,
        CALENDAR BYTEA NOT NULL,
        PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
    );
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'qrtz_paused_trigger_grps') THEN
    CREATE TABLE qrtz_paused_trigger_grps
      (
        SCHED_NAME VARCHAR(120) NOT NULL,
        TRIGGER_GROUP  VARCHAR(200) NOT NULL,
        PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
    );
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'qrtz_fired_triggers') THEN
    CREATE TABLE qrtz_fired_triggers
      (
        SCHED_NAME VARCHAR(120) NOT NULL,
        ENTRY_ID VARCHAR(95) NOT NULL,
        TRIGGER_NAME VARCHAR(200) NOT NULL,
        TRIGGER_GROUP VARCHAR(200) NOT NULL,
        INSTANCE_NAME VARCHAR(200) NOT NULL,
        FIRED_TIME BIGINT NOT NULL,
        SCHED_TIME BIGINT NOT NULL,
        PRIORITY INTEGER NOT NULL,
        STATE VARCHAR(16) NOT NULL,
        JOB_NAME VARCHAR(200) NULL,
        JOB_GROUP VARCHAR(200) NULL,
        IS_NONCONCURRENT BOOL NULL,
        REQUESTS_RECOVERY BOOL NULL,
        PRIMARY KEY (SCHED_NAME,ENTRY_ID)
    );
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'qrtz_scheduler_state') THEN
    CREATE TABLE qrtz_scheduler_state
      (
        SCHED_NAME VARCHAR(120) NOT NULL,
        INSTANCE_NAME VARCHAR(200) NOT NULL,
        LAST_CHECKIN_TIME BIGINT NOT NULL,
        CHECKIN_INTERVAL BIGINT NOT NULL,
        PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
    );
END IF;

IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema=current_schema AND table_name = 'qrtz_locks') THEN
    CREATE TABLE qrtz_locks
      (
        SCHED_NAME VARCHAR(120) NOT NULL,
        LOCK_NAME  VARCHAR(40) NOT NULL,
        PRIMARY KEY (SCHED_NAME,LOCK_NAME)
    );
END IF;


IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_j_req_recovery'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_j_req_recovery ON qrtz_job_details(SCHED_NAME,REQUESTS_RECOVERY);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_j_grp'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_j_grp on qrtz_job_details(SCHED_NAME,JOB_GROUP);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_t_j'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_t_j on qrtz_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_t_jg'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_t_jg on qrtz_triggers(SCHED_NAME,JOB_GROUP);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_t_c'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_t_c on qrtz_triggers(SCHED_NAME,CALENDAR_NAME);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_t_g'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_t_g on qrtz_triggers(SCHED_NAME,TRIGGER_GROUP);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_t_state'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_t_state on qrtz_triggers(SCHED_NAME,TRIGGER_STATE);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_t_n_state'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_t_n_state on qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_t_n_g_state'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_t_n_g_state on qrtz_triggers(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_t_next_fire_time'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_t_next_fire_time on qrtz_triggers(SCHED_NAME,NEXT_FIRE_TIME);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_t_nft_st'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_t_nft_st on qrtz_triggers(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_t_nft_misfire'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_t_nft_misfire on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_t_nft_st_misfire'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_t_nft_st_misfire on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_t_nft_st_misfire_grp'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_t_nft_st_misfire_grp on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_ft_trig_inst_name'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_ft_trig_inst_name on qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_ft_inst_job_req_rcvry'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_ft_inst_job_req_rcvry on qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_ft_j_g'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_ft_j_g on qrtz_fired_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_ft_jg'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_ft_jg on qrtz_fired_triggers(SCHED_NAME,JOB_GROUP);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_ft_t_g'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_ft_t_g on qrtz_fired_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
END IF;

IF NOT EXISTS (
    SELECT 1
    FROM   pg_catalog.pg_class c
    JOIN   pg_catalog.pg_namespace n ON n.oid = c.relnamespace
    WHERE  c.relname = 'idx_qrtz_ft_tg'
    AND    n.nspname = current_schema
    ) THEN
    CREATE INDEX idx_qrtz_ft_tg on qrtz_fired_triggers(SCHED_NAME,TRIGGER_GROUP);
END IF;

END;
$$