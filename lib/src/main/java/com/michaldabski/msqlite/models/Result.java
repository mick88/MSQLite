package com.michaldabski.msqlite.models;

import java.util.List;

/**
 * SQL query result
 * @author Michal
 *
 * @param <T> Table that the query was executed on
 */
public class Result<T>
{
	protected List<T> columns = null;
	protected int affectedRows=0;
	protected String queryString;
	
	public Result(List<T> columns)	
	{
		this.columns = columns;
		this.affectedRows = columns.size();
	}
	
	public Result(int affectedRows)
	{
		this.affectedRows = affectedRows;
	}

	public Result(String queryString)
	{
		this.queryString = queryString;
	}
	
	public String getQueryString()
	{
		return queryString;
	}
}
