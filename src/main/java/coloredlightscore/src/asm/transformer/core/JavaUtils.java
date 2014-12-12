package coloredlightscore.src.asm.transformer.core;

import static coloredlightscore.src.asm.ColoredLightsCoreLoadingPlugin.CLLog;
import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;

/**
 * The JavaUtils.class was was written by diesieben07, 
 * who has given express permission for its use in our code.
 * 
 * diesieben07's code had not been classified under the GPL license at the time
 * before we had obtained a copy.
 * 
 * Source: https://github.com/diesieben07/SevenCommons/blob/master/src/main/java/de/take_weiland/mods/commons/util/JavaUtils.java
 * 
 * @author diesieben07
 */

public final class JavaUtils {

    private JavaUtils() {
    }

    public static <T> T safeArrayAccess(T[] array, int index) {
        return arrayIndexExists(array, index) ? array[index] : null;
    }

    public static boolean arrayIndexExists(Object[] array, int index) {
        return index >= 0 && index < array.length;
    }

    public static <T> T defaultedArrayAccess(T[] array, int index, T defaultValue) {
        return arrayIndexExists(array, index) ? array[index] : defaultValue;
    }

    public static boolean listIndexExists(List<?> list, int index) {
        return index >= 0 && index < list.size();
    }

    public static <T> T safeListAccess(List<T> list, int index) {
        return listIndexExists(list, index) ? list.get(index) : null;
    }

    public static <T> Iterator<T> nCallsIterator(final Supplier<T> supplier, final int n) {
        return new AbstractIterator<T>() {

            private int counter = 0;

            @Override
            protected T computeNext() {
                return ++counter <= n ? supplier.get() : endOfData();
            }

        };
    }

    public static <T> Iterable<T> nCalls(final Supplier<T> supplier, final int n) {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return nCallsIterator(supplier, n);
            }

        };
    }

    public static <T> List<T> nullToEmpty(List<T> nullable) {
        return nullable == null ? Collections.<T> emptyList() : nullable;
    }

    public static <T> Iterable<T> concatNullable(Iterable<T> a, Iterable<T> b) {
        return a == null ? (b == null ? Collections.<T> emptyList() : b) : (b == null ? a : Iterables.<T> concat(a, b));
    }

    public static <T> void foreach(Iterable<T> it, Consumer<T> c) {
        foreach(it.iterator(), c);
    }

    public static <T> void foreach(Iterator<T> it, Consumer<T> c) {
        while (it.hasNext()) {
            c.apply(it.next());
        }
    }

    public static <T> void foreach(T[] arr, Consumer<T> c) {
        for (T t : arr) {
            c.apply(t);
        }
    }

    public static <T, R> R[] transform(T[] in, R[] out, Function<T, R> func) {
        int l = in.length;
        checkArgument(l == out.length);
        for (int i = 0; i < l; ++i) {
            out[i] = func.apply(in[i]);
        }
        return out;
    }

    public static RuntimeException throwUnchecked(Throwable t) {
        JavaUtils.<RuntimeException> throwUnchecked0(t);
        throw new AssertionError("unreachable");
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwUnchecked0(Throwable t) throws T {
        throw (T) t;
    }

    public static long encodeInts(int a, int b) {
        return (((long) a) << 32) | (b & 0xffffffffL);
    }

    public static int decodeIntA(long l) {
        return (int) (l >> 32);
    }

    public static int decodeIntB(long l) {
        return (int) l;
    }

    public static <T extends Enum<T>> T[] getEnumValues(Class<T> clazz) {
        return ENUM_GETTER.getEnumValues(clazz);
    }

    public static <T extends Enum<T>> T byOrdinal(Class<T> clazz, int ordinal) {
        return safeArrayAccess(getEnumValues(clazz), ordinal);
    }

    interface EnumValueGetter {

        <T extends Enum<T>> T[] getEnumValues(Class<T> clazz);

    }

    private static EnumValueGetter ENUM_GETTER;

    static {
        try {
            Class.forName("sun.misc.SharedSecrets");
            ENUM_GETTER = Class.forName("de.take_weiland.mods.commons.util.EnumGetterShared").asSubclass(EnumValueGetter.class).newInstance();
        } catch (Exception e) {
            CLLog.info("sun.misc.SharedSecrets not found. Falling back to default EnumGetter");
            ENUM_GETTER = new EnumGetterCloned();
        }
    }

    private static Object unsafe;
    private static boolean unsafeChecked;

    private static void initUnsafe() {
        if (unsafeChecked) {
            return;
        }
        try {
            Field field = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = field.get(null);
        } catch (Exception e) {
        }
    }

    public static boolean hasUnsafe() {
        initUnsafe();
        return unsafe != null;
    }

    public static Object getUnsafe() {
        initUnsafe();
        return unsafe;
    }
}