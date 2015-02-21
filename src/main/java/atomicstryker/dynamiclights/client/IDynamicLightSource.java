package atomicstryker.dynamiclights.client;

import net.minecraft.entity.Entity;

public interface IDynamicLightSource {
    /**
     * Entity the Dynamic Light Source is associated with.
     * The Light will always be centered on this Entity and move with it.
     * Any Entity can only be associated with a single Light!
     * If the Entity is dead (eg. Entity.isDead() returns true), the Light will be removed aswell.
     */
    public Entity getAttachmentEntity();

    /**
     * Values above 15 will not be considered, 15 is the MC max level. Values below 1 are considered disabled.
     * Values can be changed on the fly.
     * @return int value of Minecraft Light level at the Dynamic Light Source
     */
    public int getLightLevel();
}
