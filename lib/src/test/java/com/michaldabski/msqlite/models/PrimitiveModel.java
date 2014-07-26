package com.michaldabski.msqlite.models;

/**
 * Created by Michal on 27/07/2014.
 */
public class PrimitiveModel
{
    int i;
    long l;
    float f;
    double d;
    char c;
    byte b;
    String s;
    Integer integer;
    Long aLong;
    Float aFloat;
    Double aDouble;
    Character character;
    Byte aByte;

    public PrimitiveModel()
    {
    }

    public PrimitiveModel(int i, long l, float f, double d, char c, byte b, String s, Integer integer, Long aLong, Float aFloat, Double aDouble, Character character, Byte aByte)
    {
        this.i = i;
        this.l = l;
        this.f = f;
        this.d = d;
        this.c = c;
        this.b = b;
        this.s = s;
        this.integer = integer;
        this.aLong = aLong;
        this.aFloat = aFloat;
        this.aDouble = aDouble;
        this.character = character;
        this.aByte = aByte;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimitiveModel that = (PrimitiveModel) o;

        if (b != that.b) return false;
        if (c != that.c) return false;
        if (Double.compare(that.d, d) != 0) return false;
        if (Float.compare(that.f, f) != 0) return false;
        if (i != that.i) return false;
        if (l != that.l) return false;
        if (aByte != null ? !aByte.equals(that.aByte) : that.aByte != null) return false;
        if (aDouble != null ? !aDouble.equals(that.aDouble) : that.aDouble != null) return false;
        if (aFloat != null ? !aFloat.equals(that.aFloat) : that.aFloat != null) return false;
        if (aLong != null ? !aLong.equals(that.aLong) : that.aLong != null) return false;
        if (character != null ? !character.equals(that.character) : that.character != null)
            return false;
        if (integer != null ? !integer.equals(that.integer) : that.integer != null) return false;
        if (s != null ? !s.equals(that.s) : that.s != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = i;
        result = 31 * result + (int) (l ^ (l >>> 32));
        result = 31 * result + (f != +0.0f ? Float.floatToIntBits(f) : 0);
        temp = Double.doubleToLongBits(d);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) c;
        result = 31 * result + (int) b;
        result = 31 * result + (s != null ? s.hashCode() : 0);
        result = 31 * result + (integer != null ? integer.hashCode() : 0);
        result = 31 * result + (aLong != null ? aLong.hashCode() : 0);
        result = 31 * result + (aFloat != null ? aFloat.hashCode() : 0);
        result = 31 * result + (aDouble != null ? aDouble.hashCode() : 0);
        result = 31 * result + (character != null ? character.hashCode() : 0);
        result = 31 * result + (aByte != null ? aByte.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "PrimitiveModel{" +
                "i=" + i +
                ", l=" + l +
                ", f=" + f +
                ", d=" + d +
                ", c=" + c +
                ", b=" + b +
                ", s='" + s + '\'' +
                ", integer=" + integer +
                ", aLong=" + aLong +
                ", aFloat=" + aFloat +
                ", aDouble=" + aDouble +
                ", character=" + character +
                ", aByte=" + aByte +
                '}';
    }
}
