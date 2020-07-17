package services;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;

import org.apache.http.HttpStatus;
import org.junit.Test;

import com.tngtech.java.junit.dataprovider.*;


public class BookTests
{
	private static String ENDPOINT_GET_BOOK_BY_ISBN = "http://dummy.restapiexample.com/api/v1/employee/6";

	@Test
	@UseDataProvider("userDetails")
	public void testGetByISBN(String name, String salary, String age){
		given().
		when().
		get(ENDPOINT_GET_BOOK_BY_ISBN)
		.then().
		statusCode(HttpStatus.SC_OK).
		body(	"totalItems", equalTo(24),
				"status", equalTo("success"),
				"data.employee_name", containsInAnyOrder(name),
				"data.employee_salarys",containsInAnyOrder((salary),
				"data.employee_age", containsInAnyOrder(age)));
				
	}
	
	 @DataProvider 
	  public static Object[][] userDetails() { return new
	  Object[][] { { "Archana1", "1234", "23" } }; }
	 
}
