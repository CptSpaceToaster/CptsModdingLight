package coloredlightscore.src.asm.transformer.core;

/**
 * The Consumer.class was written by diesieben07, who has given 
 * express permission for its use in our code.
 * 
 * diesieben07's code has been henceforth classified under the GPL license,
 * So please take that into consideration before copying it further.
 * (Meaning you'd need permission before you copy this outright, 
 *  as it's not technically under the WTFPL)
 * 
 * Source: https://github.com/diesieben07/SevenCommons/blob/master/src/main/java/de/take_weiland/mods/commons/util/Consumer.java
 * 
 * @author diesieben07
 */

public interface Consumer<T>
{
	void apply(T input);
}
