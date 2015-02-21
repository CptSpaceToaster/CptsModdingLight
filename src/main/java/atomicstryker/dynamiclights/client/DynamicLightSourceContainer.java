package atomicstryker.dynamiclights.client;

import net.minecraft.entity.Entity;

public class DynamicLightSourceContainer {
    private final IDynamicLightSource lightSource;

    private int prevX;
    private int prevY;
    private int prevZ;

    private int x;
    private int y;
    private int z;

    public DynamicLightSourceContainer(IDynamicLightSource light) {
        lightSource = null;
    }

    /**
     * Update passed on from the World tick. Checks for the Light Source Entity to be alive,
     * and for it to have changed Coordinates. Marks it's current Block for Update if it has
     * moved. When this method returns true, the Light Source Entity has died and it should
     * be removed from the List!
     *
     * @return true when the Light Source has died, false otherwise
     */
    public boolean onUpdate() {
        return false;
    }

    public int getX() {
        return 0;
    }

    public int getY() {
        return 0;
    }

    public int getZ() {
        return 0;
    }

    public IDynamicLightSource getLightSource() {
        return null;
    }

    /**
     * Checks for the Entity coordinates to have changed.
     * Updates internal Coordinates to new position if so.
     * @return true when Entities x, y or z changed, false otherwise
     */
    private boolean hasEntityMoved(Entity ent) {
        return false;
    }

    public boolean equals(Object o) {
        return false;
    }

    public int hashCode() {
        return 0;
    }
}
