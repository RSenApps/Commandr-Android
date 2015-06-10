package com.seebye.messengerapi.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.seebye.messengerapi.api.constants.MessageType;

/**
 * Created by Seebye on 21.01.2015.
 * This file is needed for the communication between Commandr and Seebye Messenger API
 */
public class Message implements Parcelable
{
	private long m_lId = -1;
	/**
	 * //@see com.seebye.messengerapi.database.scheme.contacts#COLM_ID_MESSENGER
	 */
	private String m_strIDMessenger;
	/**
	 * //@see com.seebye.messengerapi.database.scheme.contacts#COLM_MSGTYPE
	 */
	private int m_nMessenger;

	private String m_strData;
	private MessageType m_type;
	private boolean m_bOut;
	private long m_tsCreated;
	private String m_strGroupContact;

	private Message(Parcel in)
	{
		readFromParcel(in);
	}
	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return String.format("id %d idmessenger %s messenger %d data %s type %d out %d created %d groupcontact %s"
				, m_lId
				, m_strIDMessenger
				, m_nMessenger
				, m_strData
				, m_type.ordinal()
				, m_bOut ? 1 : 0
				, m_tsCreated
				, m_strGroupContact);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeLong(m_lId);
		dest.writeString(m_strIDMessenger);
		dest.writeInt(m_nMessenger);
		dest.writeString(m_strData);
		dest.writeInt(m_type.ordinal());
		dest.writeByte((byte)(m_bOut ? 1 : 0));
		dest.writeLong(m_tsCreated);
		dest.writeString(m_strGroupContact);
	}

	public void readFromParcel(Parcel in)
	{
		m_lId = in.readLong();
		m_strIDMessenger = in.readString();
		m_nMessenger = in.readInt();
		m_strData = in.readString();
		m_type = MessageType.fromOrdinal(in.readInt());
		m_bOut = in.readByte() == 1;
		m_tsCreated = in.readLong();
		m_strGroupContact = in.readString();
	}

	public static final Parcelable.Creator<Message> CREATOR =
			new Parcelable.Creator<Message>()
			{
				public Message createFromParcel(Parcel in)
				{
					return new Message(in);
				}

				public Message[] newArray(int size)
				{
					return new Message[size];
				}
			};
}
