package eu.neclab.iotplatform.entitycomposer;

/*
 * To be thrown if an entity was not requested.
 */
public class NotRequestedException extends Exception {

	public NotRequestedException(String string) {
		super(string);
	}

}
