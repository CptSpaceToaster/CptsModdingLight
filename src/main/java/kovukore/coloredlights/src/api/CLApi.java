package kovukore.coloredlights.src.api;

public class CLApi
{
	public static float l[] = new float[] {0F, 1F/15, 2F/15, 3F/15, 4F/15, 5F/15, 6F/15, 7F/15, 8F/15, 9F/15, 10F/15, 11F/15, 12F/15, 13F/15, 14F/15, 1F};
	
	public static float r[] = new float[] {l[0], l[15], l[0], l[8], l[0], l[10], l[15], l[5], l[10], l[15], l[8], l[15], l[0], l[15], l[15], l[15]};
	public static float g[] = new float[] {l[0], l[0], l[15], l[3], l[0], l[0], l[0], l[5], l[10], l[5], l[15], l[15], l[8], l[0], l[8], l[15]};
	public static float b[] = new float[] {l[0], l[0], l[0], l[0], l[15], l[15], l[15], l[5], l[10], l[13], l[0], l[0], l[15], l[15], l[0], l[15]};
	
	public static int makeColorLightValue(float r, float g, float b, int currentLightValue)
	{
		currentLightValue &= 15;
		return currentLightValue | ((((int) (15.0F * b)) << 15) + (((int) (15.0F * g)) << 10) + (((int) (15.0F * r)) << 5));
	}
	
	public static int makeColorLightValue(float r, float g, float b)
	{
		int brightness = (int)(15.0F * Math.max(Math.max(r, g), b));
		return brightness | ((((int) (15.0F * b)) << 15) + (((int) (15.0F * g)) << 10) + (((int) (15.0F * r)) << 5));
	}
}