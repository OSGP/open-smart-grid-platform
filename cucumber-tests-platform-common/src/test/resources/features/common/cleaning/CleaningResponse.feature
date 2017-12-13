@Common @Platform @Cleaning
Feature: Clean response data in OSGP
    
Scenario Outline: Clean obsolete response data
Given a record in the <response data table> of the <adapter> database
| creation_time	| 2017-01-01 00:00:00 |
| correlation_uid |	test-org|||TEST1024000000001|||20170101000000000 |
When OSGP checks for obsolete response data
Then the record is deleted

Examples:
| response data table	| adapter |
| meter_response_data	| osgp_adapter_ws_smartmetering |
| rtu_response_data	| osgp_adapter_ws_microgrids |
| rtu_response_data	| osgp_adapter_ws_distributionautomation |


Scenario Outline: Don't clean non-obsolete response data
Given a record in the <response data table> of the <adapter> database
| creation_time |	now |
| correlation_uid	| test-org|||TEST1024000000001|||20170101000000000 |
When OSGP checks for obsolete response data
Then the record in the <response data table> of the <adapter> database has values
| creation_time	| now |
| correlation_uid	| test-org|||TEST1024000000001|||20170101000000000 |

Examples:
| response data table	| adapter |
| meter_response_data	| osgp_adapter_ws_smartmetering |
| rtu_response_data	| osgp_adapter_ws_microgrids |
| rtu_response_data	| osgp_adapter_ws_distributionautomation |
      