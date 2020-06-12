package de.sn.quarkus.businessfunctions.exception;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> 
{
    @Override
    public Response toResponse(ConstraintViolationException exception) 
    {
    	Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
    	ErrorsResponse errors = new ErrorsResponse();
    	Iterator<ConstraintViolation<?>> iterator = violations.iterator();
        while(iterator.hasNext()) {
        	ConstraintViolation<?> violation = iterator.next();
            String value=null;
            if (violation.getInvalidValue() != null) {
            	value = violation.getInvalidValue().toString();
            }else {
            	value = "";
            }
            errors.getErrorList().add(new ErrorResponse(
            		"400001", 
            		violation.getMessage(),
            		violation.getPropertyPath().toString(),
            		value,
            		null)
            		);
        }
    	return Response.status(Status.BAD_REQUEST).entity(errors).build();  
    }
}
