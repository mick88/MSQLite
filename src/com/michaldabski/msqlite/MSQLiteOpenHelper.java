package com.michaldabski.msqlite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.annotation.TargetApi;
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
	public static void insert(SQLiteDatabase database, Collection<?> items)
	{
		for (Object row : items)
		{
			insert(database, row);
		}
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
		Table table = new Table(item.getClass());
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
	public void insert(Collection<?> items)
	{
		SQLiteDatabase database = getWritableDatabase();
		insert(database, items);
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
