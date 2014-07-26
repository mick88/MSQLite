package com.michaldabski.msqlite;

import android.content.Context;

import com.michaldabski.msqlite.models.ComplexModel;
import com.michaldabski.msqlite.models.InvalidModel;
import com.michaldabski.msqlite.models.NonSerializableClass;
import com.michaldabski.msqlite.models.PrimitiveModel;
import com.michaldabski.msqlite.models.PrivateModel;
import com.michaldabski.msqlite.models.SimpleModel;
import com.michaldabski.msqlite.utils.DatabaseHelper;
import com.michaldabski.msqlite.utils.GenericUtils;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import java.io.NotSerializableException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Michal on 26/07/2014.
 */
@Config(emulateSdk = 18)
@RunWith(MsqliteTestRunner.class)
public class InsertTest
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
    public void testSimpleInsert()
    {
        msqLiteOpenHelper.createTable(SimpleModel.class, false);

        SimpleModel testModel = new SimpleModel();
        testModel.id = 32;
        testModel.name = "testname";
        msqLiteOpenHelper.insert(testModel);

        List<SimpleModel> testModelList = msqLiteOpenHelper.selectAll(SimpleModel.class);
        Assert.assertEquals(1, testModelList.size());

        SimpleModel selected = testModelList.get(0);

        Assert.assertNotNull(selected);
        Assert.assertTrue(testModel.equals(selected));
    }

    @Test
    public void testNonSerializable() throws Exception
    {
        try
        {
            msqLiteOpenHelper.createTable(InvalidModel.class, false);
            msqLiteOpenHelper.insert(new InvalidModel(new NonSerializableClass()));
            Assert.fail("Exception wasn't thrown");
        }
        catch (RuntimeException e)
        {
            Assert.assertTrue(e.getCause() instanceof NotSerializableException);
        }
    }

    @Test
    public void testPrivate() throws Exception
    {
        msqLiteOpenHelper.createTable(PrivateModel.class, false);

        PrivateModel privateModel = new PrivateModel(30, "privTest");
        msqLiteOpenHelper.insert(privateModel);

        List<PrivateModel> privateModels = msqLiteOpenHelper.selectAll(PrivateModel.class);
        Assert.assertEquals(1, privateModels.size());

        GenericUtils.testEqual(PrivateModel.class, privateModel, privateModels.get(0));
    }

    @Test
    public void testComplexInsert() throws Exception
    {
        msqLiteOpenHelper.createTable(ComplexModel.class, false);

        ComplexModel complexModel = new ComplexModel(new SimpleModel(1, "name"), Arrays.asList(
                "teststring", "test"
        ),
        Arrays.asList(
                new SimpleModel(2, "testmodel1"),
                new SimpleModel(3, "testmodel2")
        ));

        msqLiteOpenHelper.insert(complexModel);
        List<ComplexModel> complexModels = msqLiteOpenHelper.selectAll(ComplexModel.class);
        Assert.assertEquals(1, complexModels.size());

        ComplexModel complexModel1 = complexModels.get(0);

        GenericUtils.testNotNull(complexModel1, null);
        GenericUtils.testEqual(ComplexModel.class, complexModel, complexModel1);
    }

    @Test
    public void testPrimitiveFieldsWithNulls() throws Exception
    {
        msqLiteOpenHelper.createTable(PrimitiveModel.class, false);

        PrimitiveModel primitiveModel = new PrimitiveModel(20, 10l, 13.4f, 20.5d, 'a', (byte)0xf3, null, null, null, null, null, null, null);
        msqLiteOpenHelper.insert(primitiveModel);

        PrimitiveModel dbPrimitive = msqLiteOpenHelper.selectAll(PrimitiveModel.class).get(0);

        GenericUtils.testEqual(PrimitiveModel.class, primitiveModel, dbPrimitive);
    }

    @Test
    public void testPrimitiveFields() throws Exception
    {
        msqLiteOpenHelper.createTable(PrimitiveModel.class, false);

        PrimitiveModel primitiveModel = new PrimitiveModel(20, 10l, 13.4f, 20.5d, 'a', (byte)0xf3, "string", 40, 100l, 300f, 400d, 'f', (byte)0xd4);
        msqLiteOpenHelper.insert(primitiveModel);

        PrimitiveModel dbPrimitive = msqLiteOpenHelper.selectAll(PrimitiveModel.class).get(0);

        GenericUtils.testEqual(PrimitiveModel.class, primitiveModel, dbPrimitive);
    }
}
