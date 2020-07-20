Feature: Employee details
Scenario Outline: Create new employee
Given Page to add employee accessed
When I add an emplyee to site "<name>" "<salary>" "<age>"
Then The employee is added
And response includes the following
	| status				| success 		|
And I access employee created
Then The employee is accessed
And I updated an employee
Then The employee is added
And I deleted an employee
Then The employee is deleted
Examples:
|name		|salary	|age|
|Archana	|1234	|23	|