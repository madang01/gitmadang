package javapackage;

import java.util.logging.Logger;

public class AbstractParent {
	protected Logger log = java.util.logging.Logger.getGlobal();
	public AbstractParent() {
		log.info("call AbstractParent");
	}
}
