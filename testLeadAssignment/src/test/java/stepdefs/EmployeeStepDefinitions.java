package stepdefs;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class EmployeeStepDefinitions {

	private Response response;
	private ValidatableResponse json;
	private RequestSpecification request;
	private static String jsonString,jsonData;
	String employeeId,employeeStatus;
	String cookieValue;
	int statusCode;
	boolean isEmployeeName,isEmployeeAge,isEmployeeSal;

	private String BASE_URL = "http://dummy.restapiexample.com/api/v1";

	//For creating html
	String path = System.getProperty("user.dir");
    ExtentReports extent = new ExtentReports(path+"./report/result.html",true);

//For creating each test
ExtentTest RestTest = extent.startTest("Rest Assured CRUD Operations","CRUD Operations in dummy page.");

	@Given("Page to add employee accessed")
	public void accessBaseURL() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();
		RestTest.log(LogStatus.PASS,BASE_URL);
		
	}

	@When("I add an emplyee to site")
	public void addEmployee() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		response = request.body("{\"name\":\"Archana\",\"salary\":\"1234\",\"age\":\"23\"}")
				.post("/create");
		cookieValue=response.header("Set-Cookie");
		
		jsonString = response.asString();
		System.out.println(jsonString);
		RestTest.log(LogStatus.PASS, jsonString);
	}

	@Then("The employee is added")
	public void employeeCreated() {
		statusCode=response.getStatusCode();
		Assert.assertEquals(200, statusCode);
		if(statusCode==200) {
		RestTest.log(LogStatus.PASS,"Employee Added successfully "+statusCode);
		}
		else {
			RestTest.log(LogStatus.FAIL,"Employee Not Added successfully "+statusCode);
		}
			
	}

	
	@And("response includes the following$")
	public void verifyResponse(Map<String, String> jsonForm) {
		for (Map.Entry<String, String> field : jsonForm.entrySet()) {
			
			employeeStatus=JsonPath.from(jsonString).getString("status");
				
			Assert.assertEquals(field.getValue(),employeeStatus);
			if(field.getValue().equals(employeeStatus)) {
				RestTest.log(LogStatus.PASS, "Status matched");
			}
			else {
				RestTest.log(LogStatus.FAIL, "Status NOT matched");
			}
		}

	}
	
	@And("response includes the following in any order")
	public void response_contains_in_any_order(Map<String,String> data){
		
			Map<String,String> dataValue = JsonPath.from(jsonString).get("data");
			employeeId = String.valueOf(dataValue.get("id"));
			
			 Assert.assertTrue(dataValue.size() > 0);
					
			 isEmployeeName = dataValue.containsValue("Archana");
			 Assert.assertTrue(isEmployeeName);
			 if(isEmployeeName) {
					RestTest.log(LogStatus.PASS, "Name matched");
				}
				else {
					RestTest.log(LogStatus.FAIL, "Name NOT matched");
				}
			 isEmployeeSal= dataValue.containsValue("1234");
			 Assert.assertTrue(isEmployeeSal);
			 if(isEmployeeSal) {
					RestTest.log(LogStatus.PASS, "Salary matched");
				}
				else {
					RestTest.log(LogStatus.FAIL, "Salary NOT matched");
				}
			 isEmployeeAge = dataValue.containsValue("23");
			 Assert.assertTrue(isEmployeeAge);
			 if(isEmployeeAge) {
					RestTest.log(LogStatus.PASS, "Age matched");
				}
				else {
					RestTest.log(LogStatus.FAIL, "Age NOT matched");
				}
	}
	
	@And("I access employee created")
	public void accessEmployee() {
	try {
		RestAssured.baseURI = BASE_URL;
		
		RequestSpecification request = RestAssured.given(); 
		
		System.out.println(employeeId);
		String cValue = cookieValue.substring(0, cookieValue.length()-6);
		response = request.log().all()
				.header("Cookie", "ezCMPCCS=true;"," ezepvv=0;active_template::133674=pub_site.1594754196; ezovuuidtime_133674=1594754197;"+cValue)
				.header("Content-Type", "application/json")
				.pathParam("id", employeeId)
				.get("/employee/{id}");
				
		jsonData = response.asString();
		
		
		String newEmployee = JsonPath.from(jsonData).getString("status");
		if(newEmployee.contains("success")) {
			RestTest.log(LogStatus.PASS, "Record displayed");
			RestTest.log(LogStatus.PASS, jsonData);
		}
		else {
			RestTest.log(LogStatus.FAIL, "Record NOT displayed");
		}
		}catch(JsonPathException e) {
			RestTest.log(LogStatus.FAIL, "Record NOT displayed");
		}
	}
	
	@Then("The employee is accessed")
	public void employeeAccessed() {
		statusCode=response.getStatusCode();
		Assert.assertEquals(200, response.getStatusCode());
		if(statusCode==200) {
			RestTest.log(LogStatus.PASS,"Employee displayed successfully "+statusCode);
			}
			else {
				RestTest.log(LogStatus.FAIL,"Employee Not displayed successfully "+statusCode);
			}
	}

	@And("I updated an employee")
	public void updateEmployee() {
	try {
		RestAssured.baseURI = BASE_URL;
		
		RequestSpecification request = RestAssured.given(); 
				
		System.out.println(employeeId);
		String cValue = cookieValue.substring(0, cookieValue.length()-6);
		response = request.log().all()
				.header("Cookie", "ezCMPCCS=true;"," ezepvv=0;active_template::133674=pub_site.1594754196; ezovuuidtime_133674=1594754197;"+cValue)
				.header("Content-Type", "application/json")
				.pathParam("id", employeeId)
				.body("{\"name\":\"Archana\",\"salary\":\"1234567\",\"age\":\"23\"}")
				.put("/update/{id}");
		jsonData = response.asString();
		System.out.println(jsonData);
				
		String newEmployee = JsonPath.from(jsonData).getString("status");
		if(newEmployee.contains("success")) {
			RestTest.log(LogStatus.PASS, "Record Updated");
			RestTest.log(LogStatus.PASS, jsonData);
		}
		else {
			RestTest.log(LogStatus.FAIL, "Record Not Updated");
			
		}
		}catch(JsonPathException e) {
			RestTest.log(LogStatus.FAIL, "Record Not Updated");
		}
	}
	
	@Then("The employee is updated")
	public void employeeUpdated() {
		statusCode=response.getStatusCode();
		Assert.assertEquals(200, response.getStatusCode());
		if(statusCode==200) {
			RestTest.log(LogStatus.PASS,"Employee updated successfully "+statusCode);
			}
			else {
				RestTest.log(LogStatus.FAIL,"Employee Not updated successfully "+statusCode);
			}

	}
	@And("I deleted an employee")
	public void deleteEmployee() {
	try {
		RestAssured.baseURI = BASE_URL;
		
		RequestSpecification request = RestAssured.given(); 
				
		System.out.println(employeeId);
		String cValue = cookieValue.substring(0, cookieValue.length()-6);
		response = request.log().all()
				.header("Cookie", "ezCMPCCS=true;"," ezepvv=0;active_template::133674=pub_site.1594754196; ezovuuidtime_133674=1594754197;"+cValue)
				.header("Content-Type", "application/json")
				.pathParam("id", employeeId)
				.delete("/delete/{id}");
		jsonData = response.asString();
		System.out.println(jsonData);
				
		String newEmployee = JsonPath.from(jsonData).getString("status");
		if(newEmployee.contains("success")) {
			
			RestTest.log(LogStatus.PASS, "Record Deleted");
			RestTest.log(LogStatus.PASS, jsonData);
		}
		else {
			RestTest.log(LogStatus.FAIL, "Record Not Deleted");
			
		}
		}catch(JsonPathException e) {
			RestTest.log(LogStatus.FAIL, "Record Not Deleted");
			
		}
	}
	@Then("The employee is deleted")
	public void employeeDeleted() {
		statusCode=response.getStatusCode();
		Assert.assertEquals(200, response.getStatusCode());
		if(statusCode==200) {
			RestTest.log(LogStatus.PASS,"Employee deleted successfully "+statusCode);
			}
			else {
				RestTest.log(LogStatus.FAIL,"Employee Not deleted successfully "+statusCode);
			}
		extent.endTest(RestTest);
		extent.flush();
	}
	
	
}

