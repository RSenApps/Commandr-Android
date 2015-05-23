package com.seebye.messengerapi.api;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.seebye.messengerapi.api.constants.ContactType;
import com.seebye.messengerapi.api.constants.Extra;

import java.util.ArrayList;

/**
 * Created by Nico on 20.04.2015.
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
		}

		return aContacts;
	}

	public ContactType getType()
	{
		return m_type;
	}

	public String getDisplayname()
	{
		return m_strDisplayname;
	}

	public String getIDMessenger()
	{
		return m_strIDMessenger;
	}

	public int getMessenger()
	{
		return m_nMessenger;
	}


	public static final Parcelable.Creator<Contact> CREATOR =
			new Parcelable.Creator<Contact>()
			{
				public Contact createFromParcel(Parcel in)
				{
					return new Contact(in);
				}

				public Contact[] newArray(int size)
				{
					return new Contact[size];
				}
			};
}
