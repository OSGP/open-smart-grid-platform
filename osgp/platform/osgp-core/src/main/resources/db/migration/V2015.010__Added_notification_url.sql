-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

ALTER TABLE organisation ADD COLUMN notificationurl character varying(255);

UPDATE organisation SET notificationurl='https://localhost:443/web-api-smartmeter/smartMetering/notificationService/SmartMeteringNotification';