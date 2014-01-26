package kovukore.asm.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractIterator;

public class JavaUtils
{
	public static <T> T safeArrayAccess(T[] array, int index)
	{
		return arrayIndexExists(array, index) ? array[index] : null;
	}

	public static boolean arrayIndexExists(Object[] array, int index)
	{
		return index >= 0 && index < array.length;
	}

	public static <T> T defaultedArrayAccess(T[] array, int index, T defaultValue)
	{
		return arrayIndexExists(array, index) ? array[index] : defaultValue;
	}

	public static boolean listIndexExists(List<?> list, int index)
	{
		return index >= 0 && index < list.size();
	}

	public static <T> T safeListAccess(List<T> list, int index)
	{
		return listIndexExists(list, index) ? list.get(index) : null;
	}

	public static <T> Iterator<T> nCallsIterator(final Supplier<T> supplier, final int n)
	{
		return new AbstractIterator<T>()
		{
			private int counter = 0;

			@Override
			protected T computeNext()
			{
				return ++counter <= n ? supplier.get() : endOfData();
			}
		};
	}

	public static <T> Iterable<T> nCalls(final Supplier<T> supplier, final int n)
	{
		return new Iterable<T>()
		{
			@Override
			public Iterator<T> iterator()
			{
				return nCallsIterator(supplier, n);
			}
		};
	}

	public static <T> List<T> nullToEmpty(List<T> nullable)
	{
		return nullable == null ? Collections.<T> emptyList() : nullable;
	}

	public static <T> void foreach(Iterable<T> it, MethodConsumer<T> c)
	{
		foreach(it.iterator(), c);
	}

	public static <T> void foreach(Iterator<T> it, MethodConsumer<T> c)
	{
		while (it.hasNext())
		{
			c.apply(it.next());
		}
	}

	public static <T> void foreach(T[] arr, MethodConsumer<T> c)
	{
		for (T t : arr)
		{
			c.apply(t);
		}
	}

	public static <T, R> R[] transform(T[] in, R[] out, Function<T, R> func)
	{
		int l = in.length;
		checkArgument(l == out.length);
		for (int i = 0; i < l; ++i)
		{
			out[i] = func.apply(in[i]);
		}
		return out;
	}

	public static RuntimeException throwUnchecked(Throwable t)
	{
		JavaUtils.<RuntimeException> throwUnchecked0(t);
		throw new AssertionError("unreachable");
	}

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void throwUnchecked0(Throwable t) throws T
	{
		throw (T) t;
	}

	public static long encodeInts(int a, int b)
	{
		return (((long) a) << 32) | (a & 0xffffffffL);
	}

	public static int decodeIntA(long l)
	{
		return (int) (l >> 32);
	}

	public static int decodeIntB(long l)
	{
		return (int) l;
	}
}