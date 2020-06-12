package de.sn.quarkus.businessfunctions.exception;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.runtime.annotations.RegisterForReflection;

@Schema(name="error", description="standardized attributes of and error.")//OpenAPI
@RegisterForReflection
public class ErrorResponse
{
    public ErrorResponse(String code, String message, String parameter, String value, List<String> details) {
        super();
        this.code = code;
        this.parameter = parameter;
        this.value = value;
        this.message = message;
        this.details = details;
    }
 
    //Unique error code
    @Schema(description = "unique code for single error type", required = true, example = "40001", format = "String") //OpenAPI
    private String code;
    
    //General error message about nature of error
    @Schema(description = "error message message", required = true, example = "valid value between 0 and 255", format = "String") //OpenAPI
    private String message;
 
    //Input parameter, which caused the error
    @Schema(description = "optional, e.g. if validation of input parameter fails", required = false, example = "lastName", format = "String") //OpenAPI
    private String parameter;

    //Input parameter, which caused the error
    @Schema(description = "optional, e.g. if validation of input parameter fails", required = false, example = "James", format = "String") //OpenAPI
    private String value;

    //Specific errors in API request processing
    @Schema(description = "optional, e.g. if more details can be posted", required = false, example = "stack trace exception", format = "String") //OpenAPI
    private List<String> details;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public List<String> getDetails() {
		return details;
	}

	public void setDetails(List<String> details) {
		this.details = details;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
    
}