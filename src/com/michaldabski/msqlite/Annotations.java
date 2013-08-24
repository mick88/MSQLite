package com.michaldabski.msqlite;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Annotations
{
	/**
	 * Specifies whether field is the primary key in the  table.b
	 * @author Michal
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface PrimaryKey{}
	
	/**
	 * Specify that this database field cannot be null
	 * @author Michal
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface NotNull{}
	
	/**
	 * Specify data type for this field.
	 * If not set, most suitable field will be picked automatically 
	 * @author Michal
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface DataType{
		String value();
	}
	
	/**
	 * Name of this class in database.
	 * By default class name is used.
	 * @author Michal
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface TableName{
		String value();
	}
	
	/**
	 * Name of database column corresponding to this field.
	 * By default, field name is used.
	 * @author Michal
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ColumnName{
		String value();
	}
}
