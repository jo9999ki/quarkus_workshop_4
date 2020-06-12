package de.sn.quarkus.businessfunctions.exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.runtime.annotations.RegisterForReflection;

@Schema(name="List of errors", description="List of errors - by default each method might provides 1..n errors in on response")//OpenAPI
@RegisterForReflection
public class ErrorsResponse {
	//Swagger
	@Schema(description = "List of errors", required = true, format = "ErrorResponse") //OpenAPI
	private List<ErrorResponse> errorList = new ArrayList<>();

	//Timestamp error occured
    @Schema(description = "timestamp (UTC), when the error occured, JSONZ format", required = true, example = "\"2020-04-02T08:09:18.687701800Z\"", format = "String") //OpenAPI
    private String timestamp = Instant.now().toString();
	
    public List<ErrorResponse> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<ErrorResponse> errorList) {
		this.errorList = errorList;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
}
