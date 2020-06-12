package de.sn.quarkus.businessfunctions.exception;

public class RecordNotFoundException extends BusinessException 
{
	private static final long serialVersionUID = -8560227185169702914L;
	public RecordNotFoundException(String exception) {
		super(exception);
		this.setCode("40005");
	}
}