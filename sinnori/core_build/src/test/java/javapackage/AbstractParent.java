package javapackage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractParent {
	protected Logger log = LoggerFactory.getLogger(AbstractParent.class);
	
	public AbstractParent() {
		log.info("call AbstractParent");
	}
}
