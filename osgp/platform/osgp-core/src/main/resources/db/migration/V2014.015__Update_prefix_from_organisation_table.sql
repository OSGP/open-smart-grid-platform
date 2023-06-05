-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

UPDATE organisation SET organisation_identification = 'LianderNetManagement' WHERE organisation_identification = 'Alliander';

UPDATE organisation SET prefix = 'LIA' WHERE organisation_identification = 'LianderNetManagement';

UPDATE organisation SET prefix = 'FOP' WHERE organisation_identification = 'FlexOvlProject';

UPDATE organisation SET prefix = 'ARN' WHERE organisation_identification = 'GemeenteArnhem';
