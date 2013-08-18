package com.michaldabski.msqlite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.michaldabski.msqlite.models.Table;
import com.michaldabski.msqlite.queries.CreateTable;
import com.michaldabski.msqlite.queries.Drop;

public abstract class MSQLiteOpenHelper extends SQLiteOpenHelper
{

	// Constructors
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public MSQLiteOpenHelper(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}
	
	public MSQLiteOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	// Static methods
	public static void createTable(SQLiteDatabase database, Class<?> type, boolean ifNotExist)
	{
		database
			.execSQL(new CreateTable(type).setIF_NOT_EXIST(ifNotExist).build());
	}
	
	/**
	 * Drops table in the database
	 * @param database SQLite database
	 * @param type Class of the database to be dropped
	 */
	public static void dropTable(SQLiteDatabase database, Class<?> type)
	{
		database.execSQL(new Drop(type).build());
	}
	
	/**
	 * Delete Rows from database. Wrapper for SQLiteDatabase.delete()
	 */
	public static void deleteFrom(SQLiteDatabase database, Class<?> type, String whereClause, String [] whereArgs)
	{
		database.delete(new Table(type).getName(), whereClause, whereArgs);
	}
	
	/**
	 * Query database for a number of rows
	 * @param database SQLiteDatabase to use
	 * @param type Type of object expected
	 * @return List of objects selected from database
	 * @throws InstantiationException Exception thrown if selected type could not be instantiated.
	 */
	public static <T> List<T> select(SQLiteDatabase database, Class<T> type, String selection, String [] selectionArgs, String orderBy, String limit) throws InstantiationException
	{
		List<T> result = new ArrayList<T>();
		Table table = new Table(type);
		
		Cursor cursor = database.query(table.getName(), null, selection, selectionArgs, null, null, orderBy, limit);
		while (cursor.moveToNext())
		{
			result.add(table.getRow(cursor, type));
		}
		return result;
	}
	
	/**
	 * Insert multiple rows into database
	 */
	public static<T> void insert(SQLiteDatabase database, Class<T> typeOfItem, Collection<T> items)
	{
		Table table = new Table(typeOfItem);
		for (Object row : items)
		{
			insert(database, table, row);
		}
	}
	
	/**
	 * Update corresponding row in the database.
	 * 
	 * All values in existing row will be updated with values in the object.
	 * 
	 * Object must declare at least one primary key and values of 
	 * primary keys must be the same as values for this row existing 
	 * in the database.
	 * 
	 * @param database database to use
	 * @param object an object to be updated, must have at least 1 PrimaryKey
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 */
	public static void update(SQLiteDatabase database, Object object) throws IllegalArgumentException, NoSuchFieldException
	{
		Table table = new Table(object.getClass());
		update(database, table, object, table.getPrimaryWhereClause(), table.getPrimaryWhereArgs(object));
	}
	
	/**
	 * Update row(s) with values in given object.
	 */
	private static void update(SQLiteDatabase database, Table table, Object object, String whereClause, String[] whereArgs) throws IllegalArgumentException, NoSuchFieldException
	{
		database.update(table.getName(), table.getContentValues(object), whereClause, whereArgs);
		update(database, table, table.getContentValues(object), whereClause, whereArgs);
	}
	
	private static void update(SQLiteDatabase database, Table table, Object object, Collection<String> columns, String whereClause, String[] whereArgs) throws IllegalArgumentException, NoSuchFieldException
	{
		update(database, table, table.getContentValues(object, columns), whereClause, whereArgs);
	}
	
	public static void update(SQLiteDatabase database, Object object, Collection<String> columns, String whereClause, String[] whereArgs) throws IllegalArgumentException, NoSuchFieldException
	{
		Table table = new Table(object.getClass());
		update(database, table, object, columns, table.getPrimaryWhereClause(), table.getPrimaryWhereArgs(object));
	}
	
	private static void update(SQLiteDatabase database, Table table, ContentValues contentValues, String whereClause, String [] whereArgs)
	{
		database.update(table.getName(), contentValues, whereClause, whereArgs);
	}	
	
	/**
	 * Convenience method for static {@code update()}
	 * Gets its own instance of Writable database and disposes of it afterwards
	 * @param object
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 */
	public void update(Object object) throws IllegalArgumentException, NoSuchFieldException
	{
		SQLiteDatabase database = getWritableDatabase();
		update(database, object);
		database.close();
	}
	
	/**
	 * Selects multiple rows from an array. 
	 * This method gets its own instance of Database
	 */
	public <T> List<T> select(Class<T> type, String selection, String [] selectionArgs, String orderBy, String limit) throws InstantiationException
	{
		SQLiteDatabase database = getReadableDatabase();
		List<T> result = select(database, type, selection, selectionArgs, orderBy, limit);
		database.close();
		return result;
	}
	
	/**
	 * Selected all rows from given table
	 */
	public <T> List<T> selectAll(Class<T> type) throws InstantiationException
	{
		return select(type, null, null, null, null);
	}
	
	/**
	 * Insert single row to database
	 * If one of the fields is a Private Key, id will be assigned to it.
	 * @return id of the row
	 */
	public static long insert(SQLiteDatabase database, Object item)
	{
		return insert(database, new Table(item.getClass()), item);
	}
	
	private static long insert(SQLiteDatabase database, Table table, Object item)
	{
		long id = database.insert(table.getName(), null, table.getContentValues(item));
		table.setRowID(item, id);
		return id;
	}
	
	/**
	 * Insert single row to database.
	 * If one of the fields is a Private Key, id will be assigned to it.
	 * This method creates new instance of SQLiteDatabase
	 */
	public long insert(Object item)
	{
		SQLiteDatabase database = getWritableDatabase();
		long insertId =  insert(database, item);
		database.close();
		return insertId;
	}
	
	/**
	 * Insert multiple rows to database.
	 * If one of the fields is a Private Key, id will be assigned to it.
	 * This method creates new instance of SQLiteDatabase
	 */
	public <T> void insert(Class<T> classOfItem, Collection<T> items)
	{
		
		SQLiteDatabase database = getWritableDatabase();
		insert(database, classOfItem, items);
		database.close();
	}
	
	/**
	 * Runs a create table query on the database.
	 * This method creates new instance of SQLiteDatabase
	 * @param type Class defining the table
	 * @param ifNotExist table will not be created if already exists in database
	 */
	public void createTable(Class<?> type, boolean ifNotExist)
	{
		SQLiteDatabase database = getWritableDatabase();
		createTable(database, type, ifNotExist);
		database.close();
	}
}
