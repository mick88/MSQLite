package com.michaldabski.msqlite.utils;

import com.google.common.collect.Iterables;

import junit.framework.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Michal on 07/07/2014.
 */
public class GenericUtils
{
    private static final String LOG_TAG = "SuperComparator";

    /**
     * Use generics to compare all fields in the object.
     * Fields are read of the Class object provided. Inherited fields are ignored.
     * To compare objects using inherited fields, invoke this method again providing the super class.
     * Equality is evaluated using equals() or Iterables.elementsEqual() for collections.
     * @param objectType Class used to compare objects
     * @param first first object to compare
     * @param second object to compare against
     * @param <T> type of objects being compared
     * @throws IllegalAccessException
     */
    public static <T> void testEqual(Class<?> objectType, T first, T second) throws IllegalAccessException
    {
        Field[] fields = objectType.getDeclaredFields();
        for (Field field : fields)
        {
            if (field.isAccessible() == false)
                field.setAccessible(true);
            Object firstValue = field.get(first);
            Object secondValue = field.get(second);

            if (firstValue == null || secondValue == null)
            {
                Assert.assertSame(String.format("%s.%s differs. Value different in %s and %s.",
                        objectType.getSimpleName(), field.getName(), first, second), firstValue, secondValue);
            }
            else
            {
                if (firstValue instanceof Iterable<?>)
                {
                    Assert.assertTrue("Elements not equal for iterable "+field.getName()+": "+firstValue+" and "+secondValue, Iterables.elementsEqual((Iterable<?>)firstValue, (Iterable<?>)secondValue));
                }
                else Assert.assertEquals(String.format("%s.%s differs. Value different in %s and %s.",
                                objectType.getSimpleName(), field.getName(), first, second),
                        firstValue, secondValue);
            }
        }
    }

    /**
     * Gets all fields for given class, including inherited fields from superclass
     */
    public static List<Field> getAllFields(Class<?> type)
    {
        List<Field> fields = new ArrayList<Field>();
        while (type != Object.class)
        {
            fields.addAll(Arrays.asList(type.getDeclaredFields()));
            type = type.getSuperclass();
        }
        return fields;
    }


    /**
     * Asserts that object, including all child objects and list items is not null.
     * @param object object to be tested for null fields
     * @param nullableFields fields to be excluded from null check
     */
    public static <T> void testNotNull(T object, Collection<Field> nullableFields) throws IllegalAccessException
    {
        Assert.assertNotNull("Null object passed to method", object);
        if (object instanceof Iterable<?>)
        {
            for (Object item : (Iterable) object)
            {
                if (item == null) throw new NullPointerException(object.getClass().getSimpleName() + " contains null element");
                testNotNull(item, nullableFields);
            }
        }
        else
        {
            for (Field field : getAllFields(object.getClass()))
                if (nullableFields == null || nullableFields.contains(field) == false)
                {
                    field.setAccessible(true);

                    Object value = field.get(object);
                    Assert.assertNotNull(object.getClass().getSimpleName() + "." + field.getName() + " is null in "+object.toString(), value);
                    if (field.getType().isPrimitive() == false && value instanceof String == false)
                    {
                        try
                        {
                            testNotNull(value, nullableFields);
                        }
                        catch (NullPointerException e)
                        {
                            Assert.fail(object.getClass().getSimpleName() + "." + field.getName() + " contains null in " + object.toString() + ". " + e.getMessage());
                        }
                    }
                }
        }
    }
}