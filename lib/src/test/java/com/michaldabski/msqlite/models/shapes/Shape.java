package com.michaldabski.msqlite.models.shapes;

/**
 * Created by Michal on 27/07/2014.
 */
public abstract class Shape
{
    int id;

    protected Shape(int id)
    {
        this.id = id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    protected Shape()
    {
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shape shape = (Shape) o;

        if (id != shape.id) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return id;
    }
}
