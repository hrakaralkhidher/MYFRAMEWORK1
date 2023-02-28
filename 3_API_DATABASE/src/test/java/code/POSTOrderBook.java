package code;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class POSTOrderBook {

    String baseURI = RestAssured.baseURI="https://simple-books-api.glitch.me";


    @Test(description = "Given a baseURI and token When user wants to POST to /orders Then Verify Sttus code is 201")
    void orderBook(){

        //Order Book Call
        //Needed information: request payload(request body r message), token, endpoint and header(content-type)

        //Given
        //Get a Toke
        String token = utils.generateBearerToken();

        //Create Payload for the request
        Faker faker = new Faker();
        String customerName = faker.name().fullName();
        String bookId = utils.getABookId();

        //putting into json body
        JSONObject object = new  JSONObject();
        object.put("bookId",bookId);
        object.put("customerName",customerName);

        String requestPayload = object.toString();

        RequestSpecification orderBookRequest = given()
                .header("Content-Type","application/json")
                .header("Authorization",token)
                .body(requestPayload);
        //When
        Response orderBookResponse = orderBookRequest.when().post("/orders");

        //Then
        orderBookResponse.then().assertThat().statusCode(201);
        System.out.println("Order Book Response Payload: "+orderBookResponse.getBody().asString());

        String orderId = orderBookResponse.jsonPath().getString("orderId");

        //RETRIEVE ORDER(S)
        //Lets Make the Second Call : List of Ordered Book(s)

        //Given
        RequestSpecification listOfOrdersRequest = given()
                .header("Authorization",token);

        //when
        Response listOfOrderResponse = listOfOrdersRequest.when().get("/orders");

        //Then
        listOfOrderResponse.then().assertThat().statusCode(200);
        System.out.println(listOfOrderResponse.getBody().asString());

        String actualCustomerName = listOfOrderResponse.jsonPath().getString("customerName");
        Assert.assertTrue(actualCustomerName.contains(customerName));

        //UPDATE ORDER - Patch
        //Token, Content-Type, path param, requestBody

        String newCustomerName = "Onder";
        JSONObject objectNewName = new JSONObject();
        objectNewName.put("customerName",newCustomerName);
        String updateOrderRequestPayload = objectNewName.toString();

        //Given
        RequestSpecification updateOrderRequest = given()
                .pathParam("orderId",orderId)
                .header("Content-Type","application/json")
                .header("Authorization",token)
                .body(updateOrderRequestPayload);

        //When
        Response updateOrderResponse = updateOrderRequest.when().patch("/orders/{orderId");

        updateOrderResponse.then().assertThat().statusCode(204);

        //MAKE ANOTHER CALL TO : List of the Ordered Book(S)
        listOfOrdersRequest=given()
                .header("Authorization",token);
        listOfOrderResponse=listOfOrdersRequest.when().get("/orders");

        listOfOrderResponse.then().assertThat().statusCode(200);

        System.out.println(listOfOrderResponse.getBody().asString());

        String actualNewCustomerName = listOfOrderResponse.jsonPath().getString("customerName");

        Assert.assertTrue(actualNewCustomerName.contains(newCustomerName));

        //DELETE ORDER

        //Given
        //Token,path params, Content-Type Delete(HTTP) Body(if required)

        RequestSpecification deleteOrderRequest = given()
                .pathParam("orderId",orderId)
                .header("Content-Type","application/json")
                .header("Authorization",token)
                .body("{}");// providing an empty json file

        //When
        Response deleteOrderResponse = deleteOrderRequest.when().delete("/orders/{orderId}");

        //Then
        deleteOrderResponse.then().assertThat().statusCode(204);

        //LAST CALL TO VERIFY IF IT GOT DELETED - List of the oOrdered Books
        listOfOrdersRequest = given()
                .header("Authorization",token);
        listOfOrderResponse = listOfOrdersRequest.when().get("/orders");
        listOfOrderResponse.then().assertThat().statusCode(200);

        System.out.println(listOfOrderResponse.getBody().asString());
        String listOfTheOrdersResponseBody = listOfOrderResponse.getBody().asString();
        Assert.assertTrue(!listOfTheOrdersResponseBody.contains(orderId));
    }


}
