package com.michaldabski.msqlite.models;

import java.io.IOException;
import java.io.NotSerializableException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.michaldabski.msqlite.Annotations.TableName;
import com.michaldabski.msqlite.DataTypes;
import com.michaldabski.msqlite.SerializationUtils;

/**
 * Represents a table in SQLite database. 
 * 
 * An instance of Table can be created from java Class definition.
 * 
 * @author Michal
 *
 */
public class Table
{
	protected final String name;
	protected final List<Column> columns;
	protected final List<Column> primaryKeys;
	
	/**
	 * Create Table from java class definition
	 * 
	 * All fields that are not static, final or transient, 
	 * will become columns in the table as long 
	 * as they are of any of the supported type.
	 * Supported types are currently most primitives,like
	 * int, long, short, float, double etc, and
	 * their wrappers: Integer, Float etc.
	 * and String.
	 * 
	 * If field has unsupported type, warning will be output to LogCat.
	 * 
	 * @param type Class to be used, must have an empty constructor
	 */
	public Table(Class<?> type)
	{
		if (type.isAnnotationPresent(TableName.class)) 
			this.name = type.getAnnotation(TableName.class).value();
		else 
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
	
	/**
	 * Get INTEGER PRIMARY KEY column if one exists.
	 * If primary key is not integer, or there are multiple
	 * primary keys, returna null
	 */
	public Column getIntegerPrimaryKey()
	{
		Column primaryKey = getPrimaryKey();
		if (primaryKey == null || primaryKey.getDataType() != DataTypes.DATA_TYPE_INTEGER) return null;
		return primaryKey;
	}
	
	/**
	 * Set value to the integer primary key
	 * Fails silently if none exists
	 * @param object row object
	 * @param id rowid
	 */
	public void setRowID(Object object, long id)
	{
		Column primaryKey = getIntegerPrimaryKey();
		if (primaryKey != null && id != -1)
			primaryKey.setValue(object, id);
	}
	
	/**
	 * Getter for table name.
	 * 
	 * Current implementation of Table 
	 * generates name from Class.getSimpleName()
	 * 
	 * @return table name generated from table name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Gets list of columns in this Table
	 */
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
		return getContentValues(object, null);
	}
	
	/**
	 * Get content values only for selected columns
	 * @param object Object from which values are used
	 * @param colNames Subset of object's field names to use
	 * @throws IOException 
	 * @throws NotSerializableException 
	 */
	public ContentValues getContentValues(Object object, Collection<String> colNames)
	{
		ContentValues values = new ContentValues(columns.size());
		for (Column column : columns)
			if (colNames == null || colNames.contains(column.name))
		{
			Object value;
			try
			{
				value = column.getValue(object);
				if (value == null) 
					values.putNull(column.name);
				else if (column.getFieldType() == DataTypes.TYPE_SERIALIZABLE)
					values.put(column.name, SerializationUtils.serialize(value));
				else 
					values.put(column.name, value.toString());
			} catch (NoSuchFieldException e)
			{
				e.printStackTrace();
				values.putNull(column.name);
			} catch (IOException e)
			{
				throw new RuntimeException(e);
			}			
		}
		return values;
	}
	
	private String getWhereClause(List<Column> columns)
	{
		StringBuilder builder = new StringBuilder();
		String glue = "";
		for (Column col : columns)
		{
			builder.append(glue)
			.append('`').append(col.name).append('`')
			.append('=')
			.append('?');
			
			glue = " AND ";
		}
		
		return builder.toString();
	}
	

	private String [] getWhereArgs(List<Column> columns, Object object)
	{
		String [] result = new String[columns.size()];

		for (int i=0; i < result.length; i++)
		{
			try
			{
				Object value = columns.get(i).getValue(object);
				
				if (value == null) result[i] = null;
				else result[i] = value.toString();
			} catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public String getPrimaryWhereClause()
	{
		return getWhereClause(primaryKeys);
	}
	
	public String getFullWhereClause()
	{
		return getWhereClause(columns);
	}
	
	public String [] getPrimaryWhereArgs(Object object)
	{
		return getWhereArgs(primaryKeys, object);
	}
	
	public String [] getFullWhereArgs(Object object)
	{
		return getWhereArgs(columns, object);
	}
	
	/**
	 * Reads SQLite row and converts it into java object.
	 * @param cursor 
	 * @param type Class to be instantiated. Must have an empty constructor and fields should match table definition.
	 * @return Instance of type
	 * @throws InstantiationException exception thrown when type doesnt instantiate successfully (No empty constructor?)
	 */
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
			if (column.getFieldType() == DataTypes.TYPE_SERIALIZABLE)
			{
				try
				{
					column.setValue(result, SerializationUtils.deserialize(cursor.getBlob(columnId)));
				} catch (Exception e)
				{
					e.printStackTrace();
					throw new InstantiationException(e.getMessage());
				}
			}
			else
				column.setValueFromString(result, cursor.getString(columnId));
		}
		
		return result;
	}
	
	@Deprecated
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
