package com.michaldabski.msqlite.queries;

import com.michaldabski.msqlite.models.Table;


public abstract class QueryBuilder
{
	protected Table table;
	protected String condition = "1";	
	
	@Deprecated
	public QueryBuilder(Class<?> type)
	{
		this.table = new Table(type);
	}
	
	public QueryBuilder(Table table)
	{
		this.table = table;
	}
	
	public Table getTable()
	{
		return table;
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
