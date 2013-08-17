package com.michaldabski.msqlite.queries;

public class Delete extends QueryBuilder
{
	
	public Delete(Class<?> type)
	{
		super(type);
	}
	
	@Override
	public String build()
	{
		return String.format("DELETE FROM `%s` WHERE %s;", getTable().getName(), condition);
	}
	
}
