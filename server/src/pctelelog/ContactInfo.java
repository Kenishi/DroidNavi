package pctelelog;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

@JsonTypeInfo(use=Id.CLASS, include=As.WRAPPER_OBJECT)
public class ContactInfo {
	
	@JsonProperty("name") private Name m_name = null;
	@JsonProperty("number") private PhoneNumber m_number = null;
	@JsonProperty("email") private Email m_email = null;
	@JsonProperty("photo") private Photo m_photo = null;
	
	@JsonCreator
	public ContactInfo(@JsonProperty("name") Name name,
			@JsonProperty("number") PhoneNumber number,
			@JsonProperty("email") Email email,
			@JsonProperty("photo") Photo photo) {
		m_name = name;
		m_number = number;
		m_email = email;
		m_photo = photo;
	}
	
	public ContactInfo(Name name, PhoneNumber number, Email email) {
		this(name, number, email, null);
	}
	
	public ContactInfo(Name name, PhoneNumber number) {
		this(name, number, Email.NO_EMAIL, null);
	}
	
	public ContactInfo(PhoneNumber number) {
		this(Name.UNKNOWN, number, Email.NO_EMAIL, null);
	}
	
	public Name getName() {
		return m_name;
	}
	
	public PhoneNumber getNumber() {
		return m_number;
	}
	
	public Email getEmail() {
		return m_email;
	}
	
	public Photo getPhoto() {
		return m_photo;
	}
	
	@Override
	public String toString() {
		String str = m_name.toString() + "\n" +
				m_number.toString() +"\n" +
				m_email.toString();
		return str;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		
		try {
			// If cast attempt fails, then not ContactInfo and not equal
			ContactInfo info = ContactInfo.class.cast(obj);
			
			//Compare Name if both have one
			if(info.getName() != null && this.getName() != null) {
				if(! info.getName().equals(this.getName()))
					return false;
			}
			else if(info.getName() != this.getName()) { // Check if matching nulls
				return false;
			}
			
			// Compare Number if both have one
			if(info.getNumber() != null && this.getNumber() != null) {
				if(! info.getNumber().equals(this.getNumber()))
					return false;
			}
			else if(info.getNumber() != this.getNumber()) { // Check if matching nulls
				return false;
			}
			
			// Compare Email if both have one
			if(info.getEmail() != null && this.getEmail() != null) {
				if(! info.getEmail().equals(this.getEmail()))
					return false;
			}
			else if(info.getEmail() != this.getEmail()) { // Check if matching nulls
				return false;
			}

			// Compare Photo if both have one
			if(info.getPhoto() != null && this.getPhoto() != null) {
				if(! info.getPhoto().equals(this.getPhoto()))
					return false;
			}
			else if(info.getPhoto() != this.getPhoto()) { // Check if matching nulls
				return false;
			}
		} catch(ClassCastException e) {
			return false;
		}
		
		return true;
	}
	
	@JsonTypeInfo(use=Id.CLASS, include=As.WRAPPER_OBJECT)
	public static class Email {
		@JsonProperty("email") private String m_email = null;
		
		@JsonCreator
		public Email(@JsonProperty("email") String email) {
			m_email = email;
			validate();
		}
		
		@JsonProperty("email")
		public String getEmail() {
			return m_email;
		}
		
		@Override
		public String toString() {
			return m_email;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null)
				return false;
			
			try {
				Email email = Email.class.cast(obj);
				
				// Compare email address if both have one
				if(email.getEmail() != null && this.getEmail() != null) {
					if(! email.getEmail().equals(this.getEmail()))
						return false;
				}
				else if(email.getEmail() != this.getEmail()) { // Check if matching nulls
					return false;
				}
			} catch(ClassCastException e) {
				return false;
			}
			
			return true;
		}
		
		private void validate() {
			if(getEmail() == null) {
				setEmail(NO_EMAIL.getEmail());
			}
		}
		
		private void setEmail(String email) {
			m_email = email;
		}
		public static final Email NO_EMAIL = new Email("");
	}
	
	@JsonTypeInfo(use=Id.CLASS, include=As.WRAPPER_OBJECT)
	public static class Name {
		@JsonProperty("display_name") private String m_displayName = null;
		@JsonProperty("first") private String m_first = null;
		@JsonProperty("last") private String m_last = null;
		
		@JsonCreator
		public Name(@JsonProperty("display_name") String displayname,
				@JsonProperty("first") String first,
				@JsonProperty("last") String last) {
			setDisplayName(displayname);
			setFirst(first);
			setLast(last);
			validate();
		}
		
		@JsonProperty("display_name")
		public String getDisplayName() {
			return m_displayName;
		}
		
		public String getFirst() {
			return m_first;
		}
		
		public String getLast() {
			return m_last;
		}
		
		@Override
		public String toString() {
			return getDisplayName() + "(" +getFirst() + " " + getLast() + ")";
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null) {
				return false;
			}
			
			try {
				Name name = Name.class.cast(obj);
				
				if(name.getDisplayName() != null && this.getDisplayName() != null) {
					if(! name.getDisplayName().equals(this.getDisplayName())) {
						return false;
					}
				}
				else if(name.getDisplayName() != this.getDisplayName()) {
					return false;
				}
				
				if(name.getFirst() != null && this.getFirst() != null) {
					if(! name.getFirst().equals(this.getFirst())) {
						return false;
					}
				}
				else if(name.getFirst() != this.getFirst()) { // Check if matching nulls
					return false;
				}
				
				if(name.getLast() != null && this.getLast() != null) {
					if(! name.getLast().equals(this.getLast())) {
						return false;
					}
				}
				else if(name.getLast() != this.getLast()) { // Check if matching nulls
					return false;
				}
			
			} catch(ClassCastException e) {
				return false;
			}

			return true; 
		}
		
		/**
		 * Validates the contents of the class
		 */
		private void validate() {			
			// Replace nulls with empty strings
			if(getDisplayName() == null) {
				setDisplayName("");
			}
			if(getFirst() == null) {
				setFirst("");
			}
			if(getLast() == null) {
				setLast("");
			}
			
			// Change to UNKNOWN if all are blank
			if(getDisplayName().equals("") && 
					getFirst().equals("") &&
					getLast().equals("")) {
				setDisplayName(Name.UNKNOWN.getDisplayName());
				setFirst(Name.UNKNOWN.getFirst());
				setLast(Name.UNKNOWN.getLast());
			}
		}
		
		/** Private Setters **/
		private void setDisplayName(String displayName) {
			m_displayName = displayName;
		}
		private void setFirst(String first) {
			m_first = first;
		}
		private void setLast(String last) {
			m_last = last;
		}
		
		/**
		 * In cases where there is no data on the caller, this can be used.
		 */
		public static final Name UNKNOWN = new Name("UNKNOWN CALLER", "UNKNOWN", "CALLER");
	}
	
	@JsonTypeInfo(use=Id.CLASS, include=As.WRAPPER_OBJECT)
	public static class Photo {
		@JsonIgnore private byte[] m_decodedBase64 = null;
		private String m_encodedBase64 = null;
		
		private Photo() {}
		
		public Photo(byte[] encodedBase64) {
			m_decodedBase64 = Base64.decodeBase64(encodedBase64);
		}
		
		@JsonCreator
		public Photo(@JsonProperty("photoData")String encodedBase64) {
			m_decodedBase64 = Base64.decodeBase64(encodedBase64);
		}
		
		/**
		 * Used by the android client to build a Photo.
		 * 
		 * Note: Due to the internal based commons-codec in Android,
		 * 	its not possible to use a version newer than commons-codec v1.2.
		 * @param encodedBase64 A string containing a photo with its bytes encoded in Base64
		 * @return
		 */
		public static Photo androidConstructor(String encodedBase64) {
			Photo retPhoto = new Photo();
			retPhoto.m_encodedBase64 = encodedBase64;
			return retPhoto;
		}
		
		@JsonProperty("photoData")
		public String getEncodedPhoto() {
			return m_encodedBase64;
		}
		
		@JsonIgnore
		public byte[] getDecodedData() {
			return m_decodedBase64;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null) {
				return false;
			}
			try {
				Photo photo = Photo.class.cast(obj);
				if(photo.getDecodedData() != null && this.getDecodedData() != null) {
					if(! Arrays.equals(photo.getDecodedData(), this.getDecodedData())) {
						return false;
					}
				}
				else if(photo.getDecodedData() != this.getDecodedData()) {
					return false;
				}
			}
			catch(ClassCastException e) {
				return false;
			}
			
			return true;
		}
	}
}
