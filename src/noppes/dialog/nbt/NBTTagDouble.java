package noppes.dialog.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagDouble extends NBTBase.NBTPrimitive
{
    /**
     * The double value for the tag.
     */
    private double data;
    private static final String __OBFID = "CL_00001218";

    NBTTagDouble() {}

    public NBTTagDouble(double p_i45130_1_)
    {
        this.data = p_i45130_1_;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeDouble(this.data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    void load(DataInput par1DataInput, int par2) throws IOException
    {
        this.data = par1DataInput.readDouble();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)6;
    }

    public String toString()
    {
        return "" + this.data + "d";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        return new NBTTagDouble(this.data);
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagDouble nbttagdouble = (NBTTagDouble)par1Obj;
            return this.data == nbttagdouble.data;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        long i = Double.doubleToLongBits(this.data);
        return super.hashCode() ^ (int)(i ^ i >>> 32);
    }

    public long func_150291_c()
    {
        return (long)Math.floor(this.data);
    }

    public int func_150287_d()
    {
        return floor_double(this.data);
    }
    
    public int floor_double(double par0)
    {
        int i = (int)par0;
        return par0 < (double)i ? i - 1 : i;
    }

    public short func_150289_e()
    {
        return (short)(floor_double(this.data) & 65535);
    }

    public byte func_150290_f()
    {
        return (byte)(floor_double(this.data) & 255);
    }

    public double func_150286_g()
    {
        return this.data;
    }

    public float func_150288_h()
    {
        return (float)this.data;
    }
}