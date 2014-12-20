package net.minecraft.world.chunk;

/**
 * Dummy class to prevent breakage due to Cauldron
 */
public class NibbleArray {
    public byte[] data;

    public NibbleArray(int a, int b) {}
    public NibbleArray(byte[] a, int b) {}

    // Added by Cauldron - don't call unless you know you're in Cauldron!
    public byte[] getValueArray() {return data;}
}