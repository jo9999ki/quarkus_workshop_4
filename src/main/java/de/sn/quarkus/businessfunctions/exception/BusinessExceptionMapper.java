package de.sn.quarkus.businessfunctions.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> 
{
    @Override
    public Response toResponse(BusinessException exception) 
    {
    	ErrorsResponse errors = new ErrorsResponse();
            errors.getErrorList().add(new ErrorResponse(
            		exception.getCode(), 
            		exception.getMessage(),
            		null,
            		null,
            		null)
            		);
    	return Response.status(Status.BAD_REQUEST).entity(errors).build();  
    }
}
