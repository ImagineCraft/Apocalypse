package org.imaginecraft.apocalypse.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigDesc {

	public String path();
	public String desc();
	
}
