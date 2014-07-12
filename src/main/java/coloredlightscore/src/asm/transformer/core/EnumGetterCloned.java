package coloredlightscore.src.asm.transformer.core;

/**
 * The EnumGetterCloned.class was based on code written by diesieben07, 
 * who has given express permission for its use in our code.
 * 
 * diesieben07's code had not been classified under the GPL license at the time
 * before we had obtained a copy.
 * 
 * Source: https://github.com/diesieben07/SevenCommons/tree/master/src/main/java/de/take_weiland/mods/commons
 */

class EnumGetterCloned implements coloredlightscore.src.asm.transformer.core.JavaUtils.EnumValueGetter {
    @Override
    public <T extends Enum<T>> T[] getEnumValues(Class<T> clazz) {
        return clazz.getEnumConstants();
    }
}
