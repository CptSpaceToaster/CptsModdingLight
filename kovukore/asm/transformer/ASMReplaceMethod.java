package kovukore.asm.transformer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ASMReplaceMethod
{
	String obfuscatedName() default "";
	int replaceType() default 1;
}