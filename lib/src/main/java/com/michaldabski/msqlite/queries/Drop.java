package com.michaldabski.msqlite.queries;

import com.michaldabski.msqlite.models.Table;

public class Drop extends QueryBuilder
{
	private boolean ifExists = false;
	
	public Drop(Class<?> type)
	{
		this(new Table(type));
	}

    public Drop(Table table)
    {
        super(table);
    }

    @Override
	public String build()
	{
		StringBuilder builder = new StringBuilder("DROP TABLE ");
		if (ifExists) builder.append("IF EXISTS ");
		builder.append('`')
			.append(getTable().getName())
			.append('`')
			.append(';');
		return builder.toString();
	}
	
	public Drop setIfExists(boolean ifNotExists)
	{
		this.ifExists = ifNotExists;
		return this;
	}
	
}
