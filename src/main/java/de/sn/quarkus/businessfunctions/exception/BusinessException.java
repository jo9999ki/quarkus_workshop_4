package de.sn.quarkus.businessfunctions.exception;

public class BusinessException extends RuntimeException 
{
 	private static final long serialVersionUID = -8560227185169702912L;

 	private String code;
 	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BusinessException(String exception) {
		super(exception);
	}
	public BusinessException(String code, String exception) {
		super(exception);
		this.code = code;
	}
}