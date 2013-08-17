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
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public MSQLiteOpenHelper(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}
	
	public MSQLiteOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public static void createTable(SQLiteDatabase database, Class<?> type, boolean ifNotExist)
	{
		database
			.execSQL(new CreateTable(type).setIF_NOT_EXIST(ifNotExist).build());
	}
	
	public static void dropTable(SQLiteDatabase database, Class<?> type)
	{
		database.execSQL(new Drop(type).build());
	}
	
	public static void deleteFrom(SQLiteDatabase database, Class<?> type, String whereClause, String [] whereArgs)
	{
		database.delete(new Table(type).getName(), whereClause, whereArgs);
	}
	
	public static <T> List<T> select(SQLiteDatabase database, Class<T> type, String selection, String [] selectionArgs, String orderBy, String limit)
	{
		List<T> result = new ArrayList<T>();
		Table table = new Table(type);
		
		Cursor cursor = database.query(table.getName(), null, selection, selectionArgs, null, null, orderBy, limit);
		while (cursor.moveToNext())
		{
			try
			{
				result.add(table.getRow(cursor, type));
			} catch (InstantiationException e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public <T> List<T> select(Class<T> type, String selection, String [] selectionArgs, String orderBy, String limit)
	{
		SQLiteDatabase database = getReadableDatabase();
		List<T> result = select(database, type, selection, selectionArgs, orderBy, limit);
		database.close();
		return result;
	}
	
	public <T> List<T> selectAll(Class<T> type)
	{
		return select(type, null, null, null, null);
	}
	
	public static void insert(SQLiteDatabase database, Collection<?> items)
	{
		for (Object row : items)
		{
			insert(database, row);
		}
	}
	
	public static long insert(SQLiteDatabase database, Object item)
	{
		Table table = new Table(item.getClass());
		long id = database.insert(table.getName(), null, table.getContentValues(item));
		table.setRowID(item, id);
		return id;
	}
	
	public long insert(Object item)
	{
		SQLiteDatabase database = getWritableDatabase();
		long insertId =  insert(database, item);
		database.close();
		return insertId;
	}
	
	public void insert(Collection<?> items)
	{
		SQLiteDatabase database = getWritableDatabase();
		insert(database, items);
		database.close();
	}
	
	public void createTable(Class<?> type, boolean ifNotExist)
	{
		SQLiteDatabase database = getWritableDatabase();
		createTable(database, type, ifNotExist);
		database.close();
	}
}
