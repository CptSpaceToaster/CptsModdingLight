package coloredlightscore.src.asm.transformer.core;

/**
 * The Consumer.class was written by diesieben07, 
 * who has given express permission for its use in our code.
 * 
 * diesieben07's code had not been classified under the GPL license at the time
 * before we had obtained a copy.
 * 
 * Source: https://github.com/diesieben07/SevenCommons/blob/master/src/main/java/de/take_weiland/mods/commons/util/Consumer.java
 * 
 * @author diesieben07
 */

public interface Consumer<T> {
    void apply(T input);
}
