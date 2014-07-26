package com.michaldabski.msqlite.models.shapes;

/**
 * Created by Michal on 27/07/2014.
 */
public class Square extends Rectangle
{
    public Square()
    {
    }

    public Square(int id, int size)
    {
        super(id, size, size);
    }

    @Override
    public boolean equals(Object o)
    {
        return (o instanceof Rectangle) && (super.equals(o));
    }
}
