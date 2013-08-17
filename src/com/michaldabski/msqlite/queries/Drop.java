package com.michaldabski.msqlite.queries;

public class Drop extends QueryBuilder
{
	
	public Drop(Class<?> type)
	{
		super(type);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String build()
	{
		return String.format("DROP TABLE `%s`;", getTable().getName());
	}
	
}
