package com.michaldabski.msqlite.models;

import java.io.Serializable;

/**
 * Created by Michal on 26/07/2014.
 */
public class SimpleModel implements Serializable
{
    public int id;
    public String name;

    public SimpleModel()
    {
    }

    public SimpleModel(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleModel testModel = (SimpleModel) o;

        if (id != testModel.id) return false;
        if (name != null ? !name.equals(testModel.name) : testModel.name != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "SimpleModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
