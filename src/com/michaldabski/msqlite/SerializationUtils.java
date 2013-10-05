package com.michaldabski.msqlite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

public class SerializationUtils
{
	private SerializationUtils()
	{
		
	}
	
	public static byte[] serialize(Object object) throws IOException, NotSerializableException
	{
		if (object instanceof Serializable == false)
			throw new NotSerializableException();

		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		ObjectOutput objectOut = null;
		
		try
		{
			objectOut = new ObjectOutputStream(byteOutStream);
			objectOut.writeObject(object);
			return byteOutStream.toByteArray();
		}
		finally
		{
			byteOutStream.close();
			if (objectOut != null)
				objectOut.close();
		}
		
	}
	
	public static Object deserialize(byte [] bytes) throws StreamCorruptedException, IOException, ClassNotFoundException
	{
		ByteArrayInputStream inStream = new ByteArrayInputStream(bytes);
		ObjectInput objectIn = null;
		
		try
		{
			objectIn = new ObjectInputStream(inStream);
			return objectIn.readObject();
		}
		finally 
		{
			inStream.close();
			if (objectIn != null)
				objectIn.close();
		}
	}	
}
