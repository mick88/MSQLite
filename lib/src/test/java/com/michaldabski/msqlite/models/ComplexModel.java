package com.michaldabski.msqlite.models;

import java.util.List;

/**
 * Created by Michal on 27/07/2014.
 */
public class ComplexModel
{
    SimpleModel testObject;
    List<String> strings;
    List<SimpleModel> testModels;

    public ComplexModel()
    {
    }

    public ComplexModel(SimpleModel testObject, List<String> strings, List<SimpleModel> testModels)
    {
        this.testObject = testObject;
        this.strings = strings;
        this.testModels = testModels;
    }
}
