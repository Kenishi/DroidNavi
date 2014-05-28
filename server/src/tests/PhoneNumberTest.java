package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pctelelog.PhoneNumber;

public class PhoneNumberTest {
	private PhoneNumber testNum = null;
	
	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void shouldShowNumber()  {
		// Setup
		testNum = new PhoneNumber("123-456-7890");
		
		// Exercise
		String testString = testNum.toString();
		
		// Test
		assertEquals(testString, testNum.toString());
	}

}
