package kovu.asm.transformer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ASMReplaceMethod
{
	String obfuscatedName() default "";

	int replaceType() default 1; // 1 - full, 2 - accessonly
}
