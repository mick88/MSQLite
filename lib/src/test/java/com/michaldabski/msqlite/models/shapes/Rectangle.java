package com.michaldabski.msqlite.models.shapes;

/**
 * Created by Michal on 27/07/2014.
 */
public class Rectangle extends Shape
{
    int width, height;

    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public Rectangle()
    {
    }

    public Rectangle(int id, int width, int height)
    {
        super(id);
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Rectangle rectangle = (Rectangle) o;

        if (height != rectangle.height) return false;
        if (width != rectangle.width) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }
}
