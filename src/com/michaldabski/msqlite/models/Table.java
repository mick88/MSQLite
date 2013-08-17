package com.michaldabski.msqlite.models;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

public class Table
{
	protected final String name;
	protected final List<Column> columns;
	protected final List<Column> primaryKeys;
	
	public Table(Class<?> type)
	{
		this.name = type.getSimpleName();
		
		Field [] fields = type.getDeclaredFields();
		this.columns = new ArrayList<Column>(fields.length);
		this.primaryKeys = new ArrayList<Column>(1);
		for (Field field : fields)
		{
			if ((field.getModifiers() & (Modifier.TRANSIENT | Modifier.STATIC | Modifier.FINAL)) > 0) continue;
			Column column = new Column(field);
			if (column.getDataType() == null) continue;
			columns.add(column);
			if (column.PRIMARY_KEY) primaryKeys.add(column);
		}
	}
	
	public void setRowID(Object object, long id)
	{
		Column primaryKey = getPrimaryKey();
		if (primaryKey == null) return;
		primaryKey.setValue(object, id);
	}
	
	public String getName()
	{
		return name;
	}
	
	public List<Column> getColumns()
	{
		return columns;
	}
	
	public int getNumColumns()
	{
		return columns.size();
	}
	
	public List<Column> getPrimaryKeys()
	{
		return primaryKeys;
	}
	
	/**
	 *	Gets Primary Key only if there's only 1 primary key
	 */
	public Column getPrimaryKey()
	{
		if (primaryKeys.size() == 1) return primaryKeys.get(0);
		else return null;
	}
	
	public ContentValues getContentValues(Object object)
	{
		ContentValues values = new ContentValues(columns.size());
		for (Column column : columns)
		{
			Object value;
			try
			{
				value = column.getValue(object);
				if (value == null) values.putNull(column.name);
				else values.put(column.name, value.toString());
			} catch (Exception e)
			{
				e.printStackTrace();
				values.putNull(column.name);
			}			
		}
		return values;
	}
	
	public <T> T getRow(Cursor cursor, Class<T> type) throws InstantiationException
	{
		T result = null;
		try
		{
			result = type.newInstance();
//			type.getDeclaredConstructor(parameterTypes)
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
			return null;
		}
		
		int columnId = -1;
		for (Column column : columns)
		{
			if ((columnId = cursor.getColumnIndex(column.name)) == -1) continue;
			column.setStringValue(result, cursor.getString(columnId));
		}
		
		return result;
	}
	
	public Object[] getValues(Object object)
	{
		Object [] result = new Object[columns.size()];
		int n=0;
		for (Column column : columns)
		{
			try
			{
				result[n] = object
						.getClass()
						.getField(column.getName())
						.get(object);
			} catch (Exception e)
			{
				e.printStackTrace();
				result[n] = null;
			}
			n++;
		}
		return null;
		
	}
}
