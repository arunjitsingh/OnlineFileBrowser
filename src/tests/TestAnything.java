package tests;

public class TestAnything {

	private static void RE_test() {
		String myfilename = "/.arun/.jpg";
		System.out.println(!ajs.util.Regex.find("^[^\\\\./:\\*\\?\"<>\\|]{1}[^\\/:\\*\\?\"<>\\|]{0,254}$", myfilename));
	}

	public static void main(String[] args) {
		RE_test();
	}
}