package Phase3Tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EndToEndDM03 {

	Response response;
	RequestSpecification request;
	JsonPath jpath;
	List<String> names;
	List<Integer> employeeIds;
	int employeeId;

	Map<String, Object> MapObj = new HashMap<String, Object>();

	@Test
	public void test() {
		prepare();

		// GET - Display all Employees
		System.out.println("GET - All Employees");
		response = GetAllEmployees();
		System.out.println(response.getBody().asString());
		Assert.assertEquals(200, response.statusCode());
		System.out.println("Status Code for Get All Employees = " + response.statusCode() + "\r\n");

		// Add Employee
		System.out.println("PUT - Add Employee");
		addEmployee("New Employee", "999000");
		Assert.assertEquals(201, response.statusCode());
		jpath = response.jsonPath();
		employeeId = jpath.get("id");
		System.out.println(response.getBody().asString());
		Assert.assertEquals(201, response.statusCode());
		System.out.println("Status Code for Add Employee = " + response.statusCode() + "\r\n");

		// Get-Single Employee
		System.out.println("GET - Single Existing Employee");
		RequestSpecification request = RestAssured.given();
		Response response = request.param("id", "2").get("employees");
		System.out.println(response.getBody().asString());
		Assert.assertEquals(200, response.statusCode());
		System.out.println("Status Code for Get Single Employee = " + response.statusCode() + "\r\n");

		// PUT-Update Employee
		System.out.println("PATCH - Update New Employee Name ");
		response = UpdateEmployee(employeeId, "UpdatedName", "99000");
		System.out.println(response.getBody().asString());
		Assert.assertEquals(200, response.statusCode());
		System.out.println("Status Code for Update New Employee Name = " + response.statusCode() + "\r\n");

		// POST-Before Delete Employee
		System.out.println("DELETE - Delete New Employee");
		response = DeleteEmployee(employeeId);
		Assert.assertEquals(200, response.statusCode());
		System.out.println(response.getBody().asString() + ("Deleted Employee Id: ") + (employeeId));
		System.out.println("Status Code for Delete New Employee = " + response.statusCode() + "\r\n");

		// GET - Attempt to Get Deleted Employee
		System.out.println("GET - Attempt to Display Deleted (New) Employee");
		RequestSpecification request1 = RestAssured.given();
		request1.get("employees/" + employeeId);
		System.out.println(response.getBody().asString());
		response = GetSingleEmployee(employeeId);
		Assert.assertEquals(404, response.statusCode());

		// System.out.println(response1.getBody().asString());
		System.out.println("Status Code for Not Finding New Employee = " + response.statusCode() + "\r\n");
		System.out.println("CRUD Tests Completed" + "\r\n");

		// GET - Display all Employees
		response = GetAllEmployees();
		System.out.println("GET - All Employees");
		System.out.println(response.getBody().asString());
		Assert.assertEquals(200, response.statusCode());
		System.out.println("Status Code for Get All Employees = " + response.statusCode() + "\r\n");

	}

	public void prepare() {
		RestAssured.baseURI = "http://localhost:3000";

		request = RestAssured.given();
	}

	public Response GetAllEmployees() {

		request = RestAssured.given();
		response = request.get("employees");
		System.out.println(response.getBody().asString());
		return response;
	}

	public Response GetSingleEmployee(int employeeId) {
		response = request.get("employees/" + employeeId);

		return response;
	}

	public void addEmployee(String employeeName, String employeeSalary) {
		Map<String, Object> MapObj = new HashMap<String, Object>();

		MapObj.put("name", employeeName);
		MapObj.put("salary", employeeSalary);

		response = request.contentType(ContentType.JSON).accept(ContentType.JSON).body(MapObj).post("employees/create");
	}

	public Response UpdateEmployee(int employeeId, String Name, String Salary) {

		RestAssured.baseURI = "http://localhost:3000";
		request = RestAssured.given();
		MapObj.put("id", employeeId);
		MapObj.put("name", Name);
		MapObj.put("salary", Salary);

		response = request.contentType(ContentType.JSON).accept(ContentType.JSON).body(MapObj)
				.patch("employees/" + employeeId);
		return response;
	}

	public Response DeleteEmployee(int employeeId) {

		request = RestAssured.given();
		response = request.delete("employees/" + employeeId);
		return response;
	}
}