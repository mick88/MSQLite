package com.michaldabski.msqlite.models;

/**
 * Created by Michal on 27/07/2014.
 */
public class PrivateModel
{
    private int id;
    private String name;

    public PrivateModel()
    {
    }

    public PrivateModel(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrivateModel that = (PrivateModel) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
