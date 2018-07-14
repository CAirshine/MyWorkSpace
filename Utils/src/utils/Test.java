package utils;

import java.io.File;
import java.io.IOException;

public class Test {

	public static void main(String[] args) throws IOException {
		
		File file = new File(".gitignore");
		System.out.println(file.getParent());
	}
}
