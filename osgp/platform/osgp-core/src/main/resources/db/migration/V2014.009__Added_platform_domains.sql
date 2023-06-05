-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

--
-- Add platform domains column to organisation table
--

ALTER TABLE organisation ADD COLUMN domains character varying(255);

--
-- Set a default domain.
--

UPDATE organisation SET domains = 'COMMON;PUBLIC_LIGHTING;TARIFF_SWITCHING;';

--
-- Create NOT NULL constraint for this added column.
--

ALTER TABLE organisation ALTER COLUMN domains SET NOT NULL;