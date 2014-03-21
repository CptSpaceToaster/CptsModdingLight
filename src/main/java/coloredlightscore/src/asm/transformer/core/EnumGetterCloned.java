package coloredlightscore.src.asm.transformer.core;

class EnumGetterCloned implements coloredlightscore.src.asm.transformer.core.JavaUtils.EnumValueGetter
{
	@Override
	public <T extends Enum<T>> T[] getEnumValues(Class<T> clazz)
	{
		return clazz.getEnumConstants();
	}
}
