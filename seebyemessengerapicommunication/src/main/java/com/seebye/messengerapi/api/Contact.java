package com.seebye.messengerapi.api;

import android.os.Bundle;
import android.os.Parcel;

import com.seebye.messengerapi.api.constants.ContactType;
import com.seebye.messengerapi.api.constants.Extra;

import java.util.ArrayList;

/**
 * Created by Seebye on 20.04.2015.
 * This file is needed for the communication between Commandr and Seebye Messenger API
 */
public class Contact
{
	private ContactType m_type;
	private String m_strDisplayname;
	private String m_strIDMessenger;
	/**
	 * exactly one flag will be set
	 * -> switch case possible
	 */
	private int m_nMessenger = 0;

	private Contact(Parcel parcel)
	{
		parcel.setDataPosition(0);
		m_type = ContactType.fromOrdinal(parcel.readInt());
		m_strDisplayname = parcel.readString();
		m_strIDMessenger = parcel.readString();
		m_nMessenger = parcel.readInt();
	}

	public static ArrayList<Contact> fromBundle(Bundle bundle)
	{
		ArrayList<byte[]> aDataContacts = (ArrayList<byte[]>)bundle.getSerializable(Extra.CONTACTS.getKey());
		ArrayList<Contact> aContacts = new ArrayList<>();

		for(byte[] aBytes : aDataContacts)
		{
			Parcel parcel = Parcel.obtain();
			parcel.unmarshall(aBytes, 0, aBytes.length);

			aContacts.add(new Contact(parcel));

			parcel.recycle();
		}

		return aContacts;
	}
	public String getDisplayname()
	{
		return m_strDisplayname;
	}

	public String getIDMessenger()
	{
		return m_strIDMessenger;
	}
}
