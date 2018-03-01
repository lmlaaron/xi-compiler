package yh326.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HelperFunctions {

	public static void main(String[] args) throws IOException {
		generateFile(args[0], args[1], Integer.parseInt(args[2]));
	}
	
	/**
	 * Generate a file from an unformatted pdf version, but doesn't guarantee correctness. In rare circumstances 
	 * the result needs hand correction.
	 * @param source path to the source file (rough copy paste from the pdf file)
	 * @param dest path to the dest file (the .xi file)
	 * @param lineWidth how wide is the line markers (e.g. if there are > 100 lines, then 3, > 10 lines then 2, etc)
	 * @throws IOException
	 */
	public static void generateFile(String source, String dest, int lineWidth) throws IOException {
		FileReader reader = new FileReader(System.getProperty("user.dir") + "/" + source);
		FileWriter writer = new FileWriter(System.getProperty("user.dir") + "/" + dest);
		int data = reader.read();
		StringBuilder input = new StringBuilder();
		while (data != -1) {
			input.append((char)data);
			data = reader.read();
		}
		boolean[] input_marker = new boolean[input.length()];
		int nextLine = 1;
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			String lineNum;
			if (i < input.length() - lineWidth + 1) lineNum = input.substring(i, i + lineWidth).trim();
			else lineNum = "";
			try {
				if (Integer.parseInt(lineNum) == nextLine) {
					nextLine++;
					output.append('\n');
					for (int j = 0; j <= lineWidth; j++) {
						input_marker[i + j] = true;
					}
				}
				else {
					if (input_marker[i] == false) output.append(input.charAt(i));
				}
			}
			catch (NumberFormatException e) {
				if (input_marker[i] == false) output.append(input.charAt(i));
			}
			
		}
		if (output.length() > 0) output.deleteCharAt(0);
		writer.write(output.toString());
		writer.close();
		System.out.println(output.toString());
	}

}
