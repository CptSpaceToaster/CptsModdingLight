package net.minecraft.world.chunk.storage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.NibbleArray;

// Dummy class to avoid reflection for new methods/fields
public class ExtendedBlockStorage
{
    private NibbleArray rColorArray;
    private NibbleArray gColorArray;
    private NibbleArray bColorArray;

    public NibbleArray getRedColorArray() {
        return rColorArray;
    }

    public void setRedColorArray(NibbleArray rColorArray) {
        this.rColorArray = rColorArray;
    }

    public NibbleArray getGreenColorArray() {
        return gColorArray;
    }

    public void setGreenColorArray(NibbleArray gColorArray) {
        this.gColorArray = gColorArray;
    }

    public NibbleArray getBlueColorArray() {
        return bColorArray;
    }

    public void setBlueColorArray(NibbleArray bColorArray) {
        this.bColorArray = bColorArray;
    }

    public int getYLocation() {
        return 0;
    }
}
