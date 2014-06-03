package com.aberdyne.droidnavi.client;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pctelelog.ContactInfo;
import pctelelog.ContactInfo.Email;
import pctelelog.ContactInfo.Photo;
import pctelelog.PhoneNumber;
import pctelelog.ContactInfo.Name;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Base64;

public class ContactInfoPoller {
	private static final Logger logger = LoggerFactory.getLogger(ContactInfoPoller.class);
	
	protected ContactInfoPoller() {}

	/**
	 * Checks if contact exists.
	 * 
	 * This check is not thorough and really only looks if
	 * there is an associated name with a number in the contact log.
	 * 
	 * @param context The current context
	 * @param number A number to check the contact DB for
	 * @return True if info/a name was found. False otherwise.
	 */
	static public boolean hasInfo(Context context, String number) {
		ContactInfo info = pollInfo(context, number);
		
		if(info.getName().equals(Name.UNKNOWN))
			return false;
		else
			return true;
	}
	
	static public ContactInfo pollInfo(Context context, String number) {
		logger.trace("ENTRY ContactInfoPoller.pollInfo({}, {})", context, number);
		// Without a context, polling contacts isn't possible
		if(context == null) {
			logger.trace("EXIT ContactInfoPoller.pollInfo: null");
			return null;
		}
		
		// Retrieve the lookup URI to the contact in the database
		Uri contactUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
												Uri.encode(number));
		if(contactUri == null) {
			return new ContactInfo(new PhoneNumber(number));
		}
		
		// Get a cursor to the contact's entry
		Cursor cursor;  // Cursor object
		String mime;    // MIME type
        int mimeIdx;    // Index of MIMETYPE column
        int nameIdx;    // Index of DISPLAY_NAME column
		
		cursor = context.getContentResolver().query(contactUri,
				new String[] { ContactsContract.Contacts.DISPLAY_NAME,
								ContactsContract.Contacts.PHOTO_ID,
								PhoneLookup._ID },
		null, null, null);
		
		/* Defaults */
		Name name = Name.UNKNOWN;
		Email email = Email.NO_EMAIL;
		PhoneNumber phoneNumber = new PhoneNumber(number);
		Photo photo = null;
		
		if(cursor.moveToFirst()) {
			// Get Display Name 
			nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			String displayName = cursor.getString(nameIdx);
			
			// Get Contact ID
			int contactidIdx = cursor.getColumnIndex(PhoneLookup._ID);
			long contactId = cursor.getLong(contactidIdx);
			
			// Get Photo ID
			int photoidIdx = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
			int photoId = cursor.getInt(photoidIdx);
			
			
			// Setup for further queries
			String[] projection = {
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.Data.DATA1,  // Display name (StructuredName type) / Email 
				ContactsContract.Contacts.Data.DATA2, // First name (StructuredName type)
				ContactsContract.Contacts.Data.DATA3, // Last name (StructuredName type)
				ContactsContract.Contacts.Data.MIMETYPE
			};
			
			cursor.close(); // Release resources
			
			// Query for data matching display name
			cursor = context.getContentResolver().query(
					ContactsContract.Data.CONTENT_URI,
					projection,
					ContactsContract.Data.DISPLAY_NAME + "= ?",
					new String[] { displayName },
					null);
			
			if(cursor.moveToFirst()) {
				mimeIdx = cursor.getColumnIndex(ContactsContract.Contacts.Data.MIMETYPE);

				// Check MIME type and store data
				do {
					mime = cursor.getString(mimeIdx);
					
					if(ContactsContract.CommonDataKinds.Email.
							CONTENT_ITEM_TYPE.equalsIgnoreCase(mime)) {
						email = getEmail(cursor);
					}
					else if(ContactsContract.CommonDataKinds.StructuredName
							.CONTENT_ITEM_TYPE.equalsIgnoreCase(mime)) {
						name = getName(cursor);
					}
					
				} while(cursor.moveToNext());
				
				// Try to get the photo
				photo = ContactInfoPoller.loadContactPhoto(context.getContentResolver(), contactId, photoId);
			}
			cursor.close(); // Release resources
		}
		
		ContactInfo info = new ContactInfo(name, phoneNumber, email, photo);
		
		logger.trace("EXIT ContactInfoPoller.pollInfo: {}", info);
		return info;
	}
	
	static private Name getName(Cursor cursor) {
		int dataIdx = cursor.getColumnIndex(ContactsContract.Contacts.Data.DATA1);
		String displayName = cursor.getString(dataIdx);
		
		dataIdx = cursor.getColumnIndex(ContactsContract.Contacts.Data.DATA2);
		String firstName = cursor.getString(dataIdx);
		
		dataIdx = cursor.getColumnIndex(ContactsContract.Contacts.Data.DATA3);
		String lastName = cursor.getString(dataIdx);
		
		return new Name(displayName, firstName, lastName);
	}
	
	static private Email getEmail(Cursor cursor) {
		int dataIdx = cursor.getColumnIndex(ContactsContract.Contacts.Data.DATA1);
		String emailStr = cursor.getString(dataIdx);
		return new Email(emailStr);
	}
	
	/**
	 * Grab the Photo from the Contact database.
	 * 
	 * This will attempt two kinds of searches for the contact Photo. First
	 * 	it will try to find the photo using the Contact ID and if that fails
	 *	then it will attempt to find it using the Photo Id.
	 * @param cr Content Rsolver to use for querying database
	 * @param id A content id to look for a photo for
	 * @param photo_id A photo id for a contact
	 * @return The photo for the contact if it succeeds. Returns null if it fails
	 * 			to find anything.
	 */
	public static Photo loadContactPhoto(ContentResolver cr, long  id,long photo_id) 
	{
		Photo ret = null;
		
		/* Attempt using Contact_ID */
	    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
	    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
	    if (input != null) 
	    {
	    	byte[] data = null;
	    	
	    	try {
		    	int size = input.available();
		    	logger.debug("PHOTO: size {{", Integer.toString(size));
		    	
		    	data = new byte[size];
				input.read(data);
				String encodedBase64 = new String(Base64.encode(data, Base64.NO_WRAP));
				ret = Photo.androidConstructor(encodedBase64);
			} catch (IOException e) {
				logger.error(e.toString());
			}
	        
	    	return ret;
	    }
	    else
	    {
	        logger.debug("PHOTO: first try failed to load photo");
	    }
	    
	    /* Attempt using Photo_ID */
	    byte[] photoBytes = null;

	    Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);
	    Cursor c = cr.query(photoUri, new String[] {ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);

	    try 
	    {
	        if (c.moveToFirst()) 
	            photoBytes = c.getBlob(0);
	    } catch (Exception e) {
	        logger.error("Failed to get photo data blob.");
	    } finally {
	        c.close();
	    }           

	    if (photoBytes != null) {
	    	String encodedBase64 = new String(Base64.encode(photoBytes, Base64.NO_WRAP));
	    	ret = Photo.androidConstructor(encodedBase64);
	    	return ret;
	    }
	    else
	        logger.debug("PHOTO: second try also failed");
	    return null;
	}
}
