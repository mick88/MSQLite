package com.michaldabski.msqlite.queries;

import com.michaldabski.msqlite.models.Table;


public abstract class QueryBuilder
{
	protected Class<?> type;
	protected String condition = "1";	
	
	public QueryBuilder(Class<?> type)
	{
		this.type = type;
	}
	
	public Table getTable()
	{
		return new Table(type);
	}
	
//	public abstract <T> Result<T> execute(Class<T> type);
	public abstract String build();
	
	/**
	 * Set conditions for WHERE clause
	 * @param conditions
	 */
	public QueryBuilder setCondition(String conditions)
	{
		this.condition = conditions;
		return this;
	}
}
