package com.michaldabski.msqlite;


public class DataTypes
{	
	// Class field types
	public final static int
		TYPE_OTHER = 100,
		TYPE_STRING = 101,
		TYPE_COLLECTION = 102,
		TYPE_SERIALIZABLE = 103,
		TYPE_INT = 201,
		TYPE_LONG = 202,
		TYPE_SHORT = 203,
		TYPE_BYTE = 204,
		TYPE_CHAR = 205,
		TYPE_BOOL = 206,
		TYPE_FLOAT = 306,
		TYPE_DOUBLE = 307;

	// SQLite data types
	public static final String 
		DATA_TYPE_TEXT = "TEXT",
		DATA_TYPE_INTEGER = "INTEGER",
		DATA_TYPE_NUMERIC = "NUMERIC",
		DATA_TYPE_REAL = "REAL",
		DATA_TYPE_VARCHAR = DATA_TYPE_TEXT,	
		DATA_TYPE_DOUBLE = DATA_TYPE_REAL,
		DATA_TYPE_FLOAT = DATA_TYPE_REAL,
		DATA_TYPE_BOOLEAN = DATA_TYPE_NUMERIC,
		DATA_TYPE_NONE = "NONE",
		DATA_TYPE_BLOB = DATA_TYPE_NONE;
	
	/**
	 * Gets database data type
	 * @param fieldType class field type
	 * @return SQLite data type
	 */
	public static String getDataType(int fieldType)
	{
		switch (fieldType)
		{
			case TYPE_STRING:
				return DataTypes.DATA_TYPE_TEXT;
				
			case TYPE_INT:
			case TYPE_LONG:
			case TYPE_SHORT:
			case TYPE_BYTE:
			case TYPE_CHAR:
				return DataTypes.DATA_TYPE_INTEGER;
				
			case TYPE_BOOL:
				return DataTypes.DATA_TYPE_NUMERIC;
				
			case TYPE_FLOAT:
			case TYPE_DOUBLE:
				return DataTypes.DATA_TYPE_FLOAT;
				
			default:
				return DataTypes.DATA_TYPE_BLOB;
		}
	}
	
	public static int getFieldType(Class<?> cls)
	{
		if (cls.isAssignableFrom(String.class))
			return TYPE_STRING;
		if (Integer.class.isAssignableFrom(cls) || int.class.isAssignableFrom(cls))
			return TYPE_INT;
		else if (Long.class.isAssignableFrom(cls) || long.class.isAssignableFrom(cls))
			return TYPE_LONG;
		else if (Double.class.isAssignableFrom(cls) || double.class.isAssignableFrom(cls))
			return TYPE_DOUBLE;
		else if (Float.class.isAssignableFrom(cls) || float.class.isAssignableFrom(cls))
			return TYPE_FLOAT;
		else if (cls.isAssignableFrom(Character.class) || cls.isAssignableFrom(char.class))
			return TYPE_CHAR;
		else if (cls.isAssignableFrom(Byte.class) || cls.isAssignableFrom(byte.class))
			return TYPE_BYTE;
		else if (cls.isAssignableFrom(Short.class) || cls.isAssignableFrom(short.class))
			return TYPE_SHORT;
		else if (cls.isAssignableFrom(Boolean.class) || cls.isAssignableFrom(boolean.class))
			return TYPE_BOOL;
//		else if (cls.isAssignableFrom(Collection.class))
//			return TYPE_COLLECTION;
		else return TYPE_SERIALIZABLE;
	}
}
