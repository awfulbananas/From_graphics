package tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

//a class for creating a new eclipse project already formatted for making a From application
public class FromProjectMaker {
	public static final String WORKSPACE_FILE_PATH = "C:\\Users\\" + System.getProperty("user.name") + "\\eclipse-workspace\\";
	public static final File REFERENCE_PROJECT_FILE = new File("project-file-reference.txt");
	public static final File REFERENCE_CLASSPATH_FILE = new File("classpath-file-reference.txt");
	public static final File REFERENCE_FROM_FILE = new File("from-file-reference.txt");
	public static final File REFERENCE_SCREEN_FILE = new File("screen-file-reference.txt");
	
	public static void main(String[] args) {
		System.out.println("loading resources");
		
		String projectFileReference;
		try {
			FileInputStream projectFileReferenceIn = new FileInputStream(REFERENCE_PROJECT_FILE);
			byte[] projectFileRefBytes = projectFileReferenceIn.readAllBytes();
			projectFileReferenceIn.close();
			projectFileReference = new String(projectFileRefBytes);
		} catch (IOException e) {
			projectFileReference = "";
			e.printStackTrace();
		}
		String classpathFileReference;
		try {
			FileInputStream classpathFileReferenceIn = new FileInputStream(REFERENCE_CLASSPATH_FILE);
			byte[] classpathFileRefBytes = classpathFileReferenceIn.readAllBytes();
			classpathFileReferenceIn.close();
			classpathFileReference = new String(classpathFileRefBytes);
		} catch (IOException e) {
			classpathFileReference = "";
			e.printStackTrace();
		}
		String fromFileReference;
		try {
			FileInputStream fromFileReferenceIn = new FileInputStream(REFERENCE_FROM_FILE);
			byte[] fromFileRefBytes = fromFileReferenceIn.readAllBytes();
			fromFileReferenceIn.close();
			fromFileReference = new String(fromFileRefBytes);
		} catch (IOException e) {
			fromFileReference = "";
			e.printStackTrace();
		}
		String screenFileReference;
		try {
			FileInputStream screenFileReferenceIn = new FileInputStream(REFERENCE_SCREEN_FILE);
			byte[] screenFileRefBytes = screenFileReferenceIn.readAllBytes();
			screenFileReferenceIn.close();
			screenFileReference = new String(screenFileRefBytes);
		} catch (IOException e) {
			screenFileReference = "";
			e.printStackTrace();
		}
		System.out.println("done");
		
		Scanner userIn = new Scanner(System.in);
		
		String projectName = prompt(userIn, "enter project name(default is from_project): ");
		if(projectName.length() > 0) {
			projectFileReference.replaceAll("%NAME%", projectName);
		} else {
			projectFileReference.replaceAll("%NAME%", "from_project");
		}
		
		String mainClassName = prompt(userIn, "enter main class name(default is Main): ");
		if(mainClassName.length() > 0) {
			fromFileReference.replaceAll("%CLASSNAME%", mainClassName);
		} else {
			fromFileReference.replaceAll("%CLASSNAME%", "Main");
		}
		
		String screenClassName = prompt(userIn, "enter first screen class name(default is GameScreen): ");
		if(screenClassName.length() > 0) {
			screenFileReference.replaceAll("%CLASSNAME%", screenClassName);
			fromFileReference.replaceAll("%FIRSTSCREENNAME%", screenClassName);
		} else {
			screenFileReference.replaceAll("%CLASSNAME%", "GameScreen");
			fromFileReference.replaceAll("%FIRSTSCREENNAME%", "GameScreen");
		}
		
		File projectFolder = new File(projectName);
		projectFolder.mkdir();
		String classpathFilePath = projectName + "\\.classpath";
		String projectFilePath = projectName + "\\.project";
		String srcFolderPath = projectName + "\\src\\";
		File srcFolder = new File(srcFolderPath);
		srcFolder.mkdir();
		String binFolderPath = projectName + "\\bin\\";
		File binFolder = new File(binFolderPath);
		binFolder.mkdir();
		
		fillFile(classpathFilePath, classpathFileReference);
		fillFile(projectFilePath, projectFileReference);
		fillFile(srcFolderPath + mainClassName + ".java", fromFileReference);
		fillFile(srcFolderPath + screenClassName + ".java", screenFileReference);
	}
	
	private static String prompt(Scanner userIn, String prompt) {
		System.out.println();
		System.out.print(prompt);
		String input = userIn.nextLine();
		return input;
	}
	
	private static void fillFile(String filePath, String data) {
		File f = new File(filePath);
		PrintStream out;
		try {
			out = new PrintStream(f);
		} catch (FileNotFoundException e) {
			out = null;
			e.printStackTrace();
		}
		out.print(data);
	}
}
