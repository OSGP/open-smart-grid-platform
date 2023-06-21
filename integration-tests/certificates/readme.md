<!--
SPDX-FileCopyrightText: Contributors to the GXF project

SPDX-License-Identifier: Apache-2.0
-->

The following certificates and keys are used within the cucumber-tests-platform project:

- LianderNetManagement.pfx, test-org.pfx and unknown-organization.pfx
  Copied from github.com/OSGP/Config/certificates/osgp-ca/certs
  Added to requests when sending a request to the platform.
  This is a test certificate which is selfsigned. Used for development/testing purposes only.

- oslp_sim_ecda_private.der
  Copied from github.com/OSGP/Config/certificates/osgp/
  Used within the OSLP MockServer when running the tests.
  
- oslp_test_ecda_private.der
  Copied from github.com/OSGP/Config/certificates/osgp/
  Used within the OSLP MockServer when running the tests.

