package test;

public class TestWordMatch {

	public static void main( final String[] args)  {
		asciiTest();
	}
	
	static void matchTest() {
		final String s1 = "Pirate";
		final String s2 = "Pir.te";
		System.err.println( s1.matches( s2 ) ? "Yes" : "No" );
	}
	
	static void replaceTest() {
		final String s = "Pir?te";
		System.err.printf( "'%s' becomes '%s'\n", s, s.replaceAll( "[?]", "." ) );
	}
	
	static void asciiTest() {
		System.err.printf( "Key typed '%c' (%02X) vs '%c' (%02X)\n", 'A', (int)'A', 'a', (int)'a' );
		System.err.printf( "Key typed '%c' vs '%c'\n", 'A',  (char)( 'a' & 0xDF ) );
	}
}
