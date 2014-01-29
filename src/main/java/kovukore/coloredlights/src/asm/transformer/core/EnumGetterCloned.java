package kovukore.coloredlights.src.asm.transformer.core;

class EnumGetterCloned implements kovukore.coloredlights.src.asm.transformer.core.JavaUtils.EnumValueGetter
{
	@Override
	public <T extends Enum<T>> T[] getEnumValues(Class<T> clazz)
	{
		return clazz.getEnumConstants();
	}
}
