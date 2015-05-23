package com.seebye.messengerapi.api;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.seebye.messengerapi.api.constants.Extra;
import com.seebye.messengerapi.api.constants.MessageType;

import java.util.ArrayList;

/**
 * Created by Nico on 21.01.2015.
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

	public Message(long lId, String strIDMessenger, int nMessenger, String strData, MessageType type, boolean bOut, long tsCreated, String strGroupContact)
	{
		m_lId = lId;
		m_strIDMessenger = strIDMessenger;
		m_nMessenger = nMessenger;
		m_strData = strData;
		m_type = type;
		m_bOut = bOut;
		m_tsCreated = tsCreated;
		m_strGroupContact = strGroupContact;
	}
	public Message(int nMessenger, String strIDMessenger, String strGroupContact, boolean bOut, MessageType type, String strData)
	{
		m_nMessenger = nMessenger;
		m_strData = strData;
		m_type = type;
		m_bOut = bOut;
		m_strIDMessenger = strIDMessenger;
		m_strGroupContact = strGroupContact;
	}

	public Message(Message msg)
	{
		m_lId = msg.m_lId;
		m_strIDMessenger = msg.m_strIDMessenger;
		m_nMessenger = msg.m_nMessenger;
		m_strData = msg.m_strData;
		m_type = msg.m_type;
		m_bOut = msg.m_bOut;
		m_tsCreated = msg.m_tsCreated;
		m_strGroupContact = msg.m_strGroupContact;
	}

	private Message(Parcel in)
	{
		readFromParcel(in);
	}

	public static ArrayList<Message> fromBundle(Bundle bundle)
	{
		return bundle.getParcelableArrayList(Extra.MESSAGES.getKey());
	}

	public long getId()
	{
		return m_lId;
	}

	public void setId(long lId)
	{
		m_lId = lId;
	}

	/**
	 * @return The identifier of the contact by the assigned messenger
	 */
	public String getIDMessenger()
	{
		return m_strIDMessenger;
	}

	/**
	 * @param m_strIDMessenger The identifier of the contact by the assigned messenger
	 */
	public void setIDMessenger(String m_strIDMessenger)
	{
		this.m_strIDMessenger = m_strIDMessenger;
	}

	/**
	 * Returns the integer which contains the flags which assigns the message to a messenger
	 *
	 * @return
	 */
	public int getMessenger()
	{
		return m_nMessenger;
	}

	public String getData()
	{
		return m_strData;
	}

	public boolean isImpossibleFetchableType()
	{
		return m_type == MessageType.LOCATION
				|| m_type == MessageType.CONTACT
				|| m_type == MessageType.UNKNOWN

				|| (m_bOut && (m_type == MessageType.AUDIO || m_type == MessageType.VIDEO));
	}

	public void setData(String strData)
	{
		m_strData = strData;
	}

	/**
	 * Returns the type of the message -> text, image, etc.
	 * @return
	 */
	public MessageType getType()
	{
		return m_type;
	}

	public boolean isOutgoing()
	{
		return m_bOut;
	}

	/**
	 * The time on which the message was added to our database
	 * @return Timestamp
	 */
	public long getTsCreated()
	{
		return m_tsCreated;
	}

	public String getGroupContact()
	{
		return m_strGroupContact;
	}
	public void setGroupContact(String strGroupContact)
	{
		m_strGroupContact = strGroupContact;
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
