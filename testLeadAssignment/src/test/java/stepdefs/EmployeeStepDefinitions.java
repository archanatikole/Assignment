package stepdefs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class EmployeeStepDefinitions {

	private Response response;
	private ValidatableResponse json;
	private RequestSpecification request;
	private static String reaponseJsonString, jsonData, jsonDataPut, jsonDataDelete;
	String employeeId,employeeStatus;
	String cookieValue;
	int statusCode,statusCodeGet,statusCodePut,statusCodeDelete;
	boolean isEmployeeName, isEmployeeAge, isEmployeeSal;

	private String BASE_URL = "http://dummy.restapiexample.com/api/v1";
	

	//For creating html
	String path = System.getProperty("user.dir");
	Date d = new Date();
    ExtentReports extent = new ExtentReports(path+"./report/result"+d.toString().replace(":", "_").replace(" ","_")+".html",true);

//For creating each test
ExtentTest RestTest = extent.startTest("Rest Assured CRUD Operations","CRUD Operations in dummy page.");
public void writeDataInSheet(Object[] post) {
	Workbook workbook;
	Sheet newSheet;
	try {
		//   FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
		workbook=new XSSFWorkbook();
		newSheet = workbook.createSheet("Data");

		
		// write data
		Iterator<Row> rowIterator = newSheet.iterator(); 
        while (rowIterator.hasNext()) { 
            Row row = rowIterator.next(); 
            // For each row, iterate through all the columns 
            Iterator<Cell> cellIterator = row.cellIterator(); 

            while (cellIterator.hasNext()) { 
            	for (Object field : post) {
            	Cell cell = cellIterator.next(); 
                // Check the cell type and format accordingly 
                if (field instanceof String) {
					cell.setCellValue((String) field);
				} else if (field instanceof Integer) {
					cell.setCellValue((Integer) field);

				}
                     
            } 
            
            }
        }
				FileOutputStream outputStream = new FileOutputStream(path+"//report//testResult"+d.toString().replace(":", "_").replace(" ","_")+".xlsx");
				workbook.write(outputStream);
				workbook.close();
				outputStream.close();
			
			} catch (IOException  ex) {
				ex.printStackTrace();
			} catch (EncryptedDocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}

	@Given("Page to add employee accessed")
	public void accessBaseURL() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();
		RestTest.log(LogStatus.PASS,BASE_URL);
		
	}

	@When("I add an emplyee to site (.*) (.*) (.*)")
	public void addEmployee(String eName, String salary, String age) {
		String payload = "{\n" + "	\"name\":" + eName + ", \n" + " 	\"salary\":" + salary + ",\n" + "	\"age\":"
				+ age + "\n" + "}";

		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		response = request.body(payload).post("/create");
		cookieValue = response.header("Set-Cookie");

		reaponseJsonString = response.asString();
		System.out.println(reaponseJsonString);
		RestTest.log(LogStatus.PASS, reaponseJsonString);
		
		Map<String,String> dataValue = JsonPath.from(reaponseJsonString).get("data");
		employeeId = String.valueOf(dataValue.get("id"));
		
		 Assert.assertTrue(dataValue.size() > 0);
				
		 isEmployeeName = dataValue.containsValue(eName);
		 Assert.assertTrue(isEmployeeName);
		 if(isEmployeeName) {
				RestTest.log(LogStatus.PASS, "Name matched");
			}
			else {
				RestTest.log(LogStatus.FAIL, "Name NOT matched");
			}
		 isEmployeeSal= dataValue.containsValue(salary);
		 Assert.assertTrue(isEmployeeSal);
		 if(isEmployeeSal) {
				RestTest.log(LogStatus.PASS, "Salary matched");
			}
			else {
				RestTest.log(LogStatus.FAIL, "Salary NOT matched");
			}
		 isEmployeeAge = dataValue.containsValue(age);
		 Assert.assertTrue(isEmployeeAge);
		 if(isEmployeeAge) {
				RestTest.log(LogStatus.PASS, "Age matched");
			}
			else {
				RestTest.log(LogStatus.FAIL, "Age NOT matched");
			}
	
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
			
			employeeStatus=JsonPath.from(reaponseJsonString).getString("status");
				
			Assert.assertEquals(field.getValue(),employeeStatus);
			if(field.getValue().equals(employeeStatus)) {
				RestTest.log(LogStatus.PASS, "Status matched");
			}
			else {
				RestTest.log(LogStatus.FAIL, "Status NOT matched");
			}
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
	
		//Verify data
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
		jsonDataPut = response.asString();
		System.out.println(jsonDataPut);
				
		String newEmployee = JsonPath.from(jsonDataPut).getString("status");
		if(newEmployee.contains("success")) {
			RestTest.log(LogStatus.PASS, "Record Updated");
			RestTest.log(LogStatus.PASS, jsonDataPut);
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
		jsonDataDelete = response.asString();
		System.out.println(jsonDataDelete);
				
		String newEmployee = JsonPath.from(jsonDataDelete).getString("status");
		if(newEmployee.contains("success")) {
			
			RestTest.log(LogStatus.PASS, "Record Deleted");
			RestTest.log(LogStatus.PASS, jsonDataDelete);
		}
		else {
			RestTest.log(LogStatus.FAIL, "Record Not Deleted");
			
		}
		}catch(JsonPathException e) {
			RestTest.log(LogStatus.FAIL, "Record Not Deleted");
			
		}
	String[] StringData = { "Created Employee",reaponseJsonString,"Employee Details",jsonData,"Updated Employee",jsonDataPut,"Deleted Employee", jsonDataDelete };
	Object[] completeData = StringData;
	writeDataInSheet(completeData);
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

