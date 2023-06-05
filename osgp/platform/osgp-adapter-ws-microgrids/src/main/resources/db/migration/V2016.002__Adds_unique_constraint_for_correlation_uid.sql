-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DELETE FROM rtu_response_data 
      WHERE correlation_uid IN ( 
            SELECT   correlation_uid 
            FROM     rtu_response_data 
            GROUP BY correlation_uid 
            HAVING   count(correlation_uid) > 1);

ALTER TABLE rtu_response_data 
            ADD CONSTRAINT "rtu_response_data_correlation_uid_key" UNIQUE( "correlation_uid" );