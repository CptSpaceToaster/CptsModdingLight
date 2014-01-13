package com.ajwgeek.geekcore.common.asm.transformer.manualTransformations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ReplacementCode
{
	 public int id() default -1;
}