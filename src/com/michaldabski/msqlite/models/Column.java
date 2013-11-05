package com.michaldabski.msqlite.models;

import java.lang.reflect.Field;

import android.database.Cursor;
import android.util.Log;

import com.michaldabski.msqlite.Annotations.ColumnName;
import com.michaldabski.msqlite.Annotations.DataType;
import com.michaldabski.msqlite.DataTypes;

/**
 * Represents Table column
 * @author Michal
 *
 */
public class Column
{	
	private final Field field;
	protected boolean 
		NOT_NULL, 
		PRIMARY_KEY;
	
	protected final String 
		name,
		dataType,
		// table.field
		uniqueName;
	private final int fieldType;
	
	public static Column fromCursor(Cursor cursor, Table table)
	{
		final int FIELD_NAME = 1,
				FIELD_TYPE = 2,
				FIELD_NOT_NULL = 3,
				FIELD_DEF_VALUE = 4,
				FIELD_PK = 5;
		String name = cursor.getString(FIELD_NAME);
		Column column = new Column(null, cursor.getInt(FIELD_NOT_NULL)==1, cursor.getInt(FIELD_PK)==1, name, 
				cursor.getString(FIELD_TYPE), table.getName()+"."+name, DataTypes.TYPE_NA);
		
		return column;
	}
	
	private Column(Field field, boolean nOT_NULL, boolean pRIMARY_KEY,
			String name, String dataType, String uniqueName,
			int fieldType)
	{
		this.field = field;
		NOT_NULL = nOT_NULL;
		PRIMARY_KEY = pRIMARY_KEY;
		this.name = name;
		this.dataType = dataType;
		this.uniqueName = uniqueName;
		this.fieldType = fieldType;
	}

	public Column(Field field, Table table)
	{
		this.field =field;
		
		if (field.isAnnotationPresent(ColumnName.class)) this.name = field.getAnnotation(ColumnName.class).value();
		else this.name = field.getName();
		
		this.uniqueName = String.format("%s.%s", table.getName(), name);
		
		fieldType = DataTypes.getFieldType(field.getType());
		if (fieldType == DataTypes.TYPE_OTHER)
		{
			Log.w("MSQLite", field.getType().getSimpleName()+" is not supported as a table field yet. Consider making this field transient.");
			dataType = null;
			return;
		}
		
		if (field.isAnnotationPresent(DataType.class))
			this.dataType = field.getAnnotation(DataType.class).value();
		else 
			this.dataType = DataTypes.getDataType(fieldType);
		
		this.PRIMARY_KEY = field.isAnnotationPresent(com.michaldabski.msqlite.Annotations.PrimaryKey.class);
		this.NOT_NULL = field.isAnnotationPresent(com.michaldabski.msqlite.Annotations.NotNull.class);
	}
	
	public String getDataType()
	{
		return dataType;
	}
	
	public CharSequence getBuilder()
	{
		StringBuilder builder = new StringBuilder()
			.append('`')
			.append(name)
			.append('`')
			.append(' ')
			.append(this.dataType);
		
		
		if (NOT_NULL == true) builder.append(" NOT");
		builder.append(" NULL");
		
		return builder;
	}
	
	@Override
	public String toString()
	{
		return uniqueName;
	}
	
	public String getName()
	{
		return name;
	}
	
	@Override
	public int hashCode()
	{
		return uniqueName.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Column)
			return uniqueName.equals(((Column) obj).uniqueName);
		else return super.equals(obj);
	}
	
	public Object getValue(Object object) throws IllegalArgumentException, NoSuchFieldException
	{
//		return fieldClass.cast(object.getClass().getField(fieldName).get(object))
		try
		{
			Object value = field.get(object);
			if (value instanceof Boolean)
				return (((Boolean)value == true) ? 1 : 0);
			return value;
		} catch (IllegalAccessException e)
		{
			field.setAccessible(true);
			Object value = getValue(object);
			field.setAccessible(false);
			return value;
		}
	}
	
	/**
	 * Sets raw value without interpreting or converting
	 */
	public void setValue(Object object, Object value)
	{
		try
		{
			field.set(object, value);
		} catch (IllegalAccessException e)
		{
			field.setAccessible(true);
			setValue(object, value);
			field.setAccessible(false);
		}
	}
	
	public void setValue(Object object, Long value) throws IllegalArgumentException
	{
		try
		{
			switch (fieldType)
			{
				case DataTypes.TYPE_INT:
					field.set(object, value.intValue());
					break;
				case DataTypes.TYPE_LONG:
					field.set(object, value);
					break;
				case DataTypes.TYPE_BYTE:
					field.set(object, value.byteValue());
					break;
			}
		} catch (IllegalAccessException e)
		{
			field.setAccessible(true);
			setValue(object, value);
			field.setAccessible(false);
		}
	}
	
	public int getFieldType()
	{
		return fieldType;
	}
	
	public boolean isPRIMARY_KEY()
	{
		return PRIMARY_KEY;
	}
	
	public void setValueFromString(Object object, String value) throws IllegalArgumentException
	{
		try
		{
			if (value == null)
				field.set(object, null);
			switch (fieldType)
			{
				case DataTypes.TYPE_STRING:
					field.set(object, value);
					break;

				case DataTypes.TYPE_INT:
					field.set(object, Integer.valueOf(value));
					break;
					
				case DataTypes.TYPE_LONG:
					field.set(object, Long.valueOf(value));
					break;
					
				case DataTypes.TYPE_DOUBLE:
					field.set(object, Double.valueOf(value));
					break;
					
				case DataTypes.TYPE_FLOAT:
					field.set(object, Float.valueOf(value));
					break;
					
				case DataTypes.TYPE_BYTE:
					field.set(object, Byte.valueOf(value));
					break;
					
				case DataTypes.TYPE_CHAR:
					field.set(object, value.charAt(0));
					break;					
					
				case DataTypes.TYPE_SHORT:
					field.set(object, Short.valueOf(value));
					break;
					
				case DataTypes.TYPE_BOOL:
					int i = Integer.valueOf(value);
					field.set(object, i != 0);
					break;
			}				
		} 
		catch (IllegalAccessException e)
		{
			field.setAccessible(true);
			setValueFromString(object, value);
			field.setAccessible(false);
		}
	}
}
