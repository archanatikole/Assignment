Feature: Employee details
Scenario: Create new employee
Given Page to add employee accessed
When I add an emplyee to site 
Then The employee is added
And response includes the following
	| status				| success 		|
And response includes the following in any order
	| data.employee_name 				| Archana	|
	| data.employee_salary 				| 1234		|   
	| data.employee_age 				| 23		|
And I access employee created
Then The employee is accessed
And I updated an employee
Then The employee is added
And I deleted an employee
Then The employee is deleted
