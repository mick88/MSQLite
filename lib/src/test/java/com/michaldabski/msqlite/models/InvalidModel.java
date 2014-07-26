package com.michaldabski.msqlite.models;

/**
 * Created by Michal on 27/07/2014.
 */
public class InvalidModel
{
    NonSerializableClass nonSerializableClass;

    public InvalidModel(NonSerializableClass nonSerializableClass)
    {
        this.nonSerializableClass = nonSerializableClass;
    }

    public InvalidModel()
    {
    }
}
