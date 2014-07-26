package com.michaldabski.msqlite;

import android.content.Context;

import com.michaldabski.msqlite.models.shapes.Rectangle;
import com.michaldabski.msqlite.models.shapes.Shape;
import com.michaldabski.msqlite.models.shapes.Square;
import com.michaldabski.msqlite.utils.DatabaseHelper;
import com.michaldabski.msqlite.utils.GenericUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

/**
 * Created by Michal on 27/07/2014.
 */
@Config(emulateSdk = 18)
@RunWith(MsqliteTestRunner.class)
public class InheritanceTest
{
    MSQLiteOpenHelper msqLiteOpenHelper;

    @Before
    public void setUp() throws Exception
    {
        Context context = Robolectric.getShadowApplication().getApplicationContext();
        msqLiteOpenHelper = new DatabaseHelper(context);

    }

    @After
    public void tearDown()
    {
        msqLiteOpenHelper.close();
        Context context = Robolectric.getShadowApplication().getApplicationContext();
        context.deleteDatabase(DatabaseHelper.NAME);
    }

    @Test
    public void testBaseFields() throws Exception
    {
        msqLiteOpenHelper.createTable(Square.class, false);

        Square square = new Square(1, 20);
        msqLiteOpenHelper.insert(square);

        Square dbSquare = msqLiteOpenHelper.selectAll(Square.class).get(0);

        GenericUtils.testEqual(Square.class, square, dbSquare);
        GenericUtils.testEqual(Rectangle.class, square, dbSquare);
        GenericUtils.testEqual(Shape.class, square, dbSquare);

    }
}
