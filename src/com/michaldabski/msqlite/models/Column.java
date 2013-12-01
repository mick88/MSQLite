package com.michaldabski.msqlite.models;

import java.lang.reflect.Field;

import android.database.Cursor;
import android.util.Log;

import com.michaldabski.msqlite.Annotations.ColumnName;
import com.michaldabski.msqlite.Annotations.DataType;
import com.michaldabski.msqlite.Annotations.Default;
import com.michaldabski.msqlite.DataTypes;
import com.michaldabski.msqlite.SerializationUtils;

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
	private final String defultValue;
	
	public static Column fromCursor(Cursor cursor, Table table)
	{
		final int FIELD_NAME = 1,
				FIELD_TYPE = 2,
				FIELD_NOT_NULL = 3,
				FIELD_DEF_VALUE = 4,
				FIELD_PK = 5;
		String name = cursor.getString(FIELD_NAME);
		Column column = new Column(null, cursor.getInt(FIELD_NOT_NULL)==1, cursor.getInt(FIELD_PK)==1, name, 
				cursor.getString(FIELD_TYPE), table.getName()+"."+name, DataTypes.TYPE_NA, cursor.getString(FIELD_DEF_VALUE));
		
		return column;
	}
	
	private Column(Field field, boolean nOT_NULL, boolean pRIMARY_KEY,
			String name, String dataType, String uniqueName,
			int fieldType, String defaultValue)
	{
		this.field = field;
		NOT_NULL = nOT_NULL;
		PRIMARY_KEY = pRIMARY_KEY;
		this.name = name;
		this.dataType = dataType;
		this.uniqueName = uniqueName;
		this.fieldType = fieldType;
		this.defultValue = defaultValue;
	}

	public Column(Field field, Table table)
	{
		this.field =field;
		
		if (field.isAnnotationPresent(ColumnName.class)) this.name = field.getAnnotation(ColumnName.class).value();
		else this.name = field.getName();
		
		if (field.isAnnotationPresent(Default.class)) this.defultValue = field.getAnnotation(Default.class).value();
		else this.defultValue = null; 
			
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
		
		if (defultValue != null)
			builder.append(" DEFAULT '").append(defultValue).append("'");
		
		
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
	
	public int getFieldType()
	{
		return fieldType;
	}
	
	public boolean isPRIMARY_KEY()
	{
		return PRIMARY_KEY;
	}
	
	public void setValue(Object object, Cursor cursor, final int columnId)
	{
		if (cursor.isNull(columnId))
			setValue(object, (Object)null);

		switch (fieldType)
		{
			case DataTypes.TYPE_STRING:
				setValue(object, cursor.getString(columnId));
				break;
				
			case DataTypes.TYPE_BOOL:
				setValue(object, cursor.getShort(columnId) != 0);
				break;
				
			case DataTypes.TYPE_BYTE:
				setValue(object, (byte)cursor.getShort(columnId));
				break;
			case DataTypes.TYPE_CHAR:
				setValue(object, (char)cursor.getShort(columnId));
				break;
			case DataTypes.TYPE_SHORT:
				setValue(object, cursor.getShort(columnId));
				break;
				
			case DataTypes.TYPE_DOUBLE:
				setValue(object, cursor.getDouble(columnId));
				break;
			case DataTypes.TYPE_FLOAT:
				setValue(object, (float)cursor.getDouble(columnId));
				break;
				
			case DataTypes.TYPE_INT:
				setValue(object, cursor.getInt(columnId));
				break;
				
			case DataTypes.TYPE_LONG:
				setValue(object, cursor.getLong(columnId));
				break;
			
			case DataTypes.TYPE_SERIALIZABLE:
				byte [] bytes = cursor.getBlob(columnId);
				try
				{
					setValue(object, SerializationUtils.deserialize(bytes));
				} catch (Exception e)
				{
					throw new RuntimeException(e);
				}
				break;
		}
	}

	protected void setIntegerValue(Object instance, long number)
	{
		switch (fieldType)
		{
			case DataTypes.TYPE_LONG:
				setValue(instance, number);
				break;
				
			case DataTypes.TYPE_INT:
				setValue(instance, (int)number);
				break;
				
			case DataTypes.TYPE_SHORT:
				setValue(instance, (short) number);
				break;
				
			case DataTypes.TYPE_BYTE:
				setValue(instance, (byte) number);
				break;
				
			case DataTypes.TYPE_CHAR:
				setValue(instance, (char) number);
				break;
				
			default:
				throw new IllegalArgumentException("Invalid integer field type");
		}
	}
}
