import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CompilerExecutor {
	private List<File> files;	
	private String targetPath;

	public CompilerExecutor(List<File> files, String targetPath) {
		this.files = files;
		this.targetPath = targetPath;
	}

	public void compile() {
		Runtime runtime = Runtime.getRuntime();
		Process process = null;

		// Compilation of /target/classes/
		StringBuilder sb = null;
		sb = new StringBuilder();

		for (File f : files) {
			sb.append("javac ").append(f.getAbsolutePath()).append(" -d ").append(targetPath)
			.append(Constants.TARGET_CLASSES_PATH);

			try { process = runtime.exec(sb.toString()); } catch (IOException ie) { ie.printStackTrace(); };
			try { process.waitFor(); } catch (InterruptedException ie) { ie.printStackTrace(); }		
		}
	}

	public static void compile(List<File> files, String targetPath) {
		new CompilerExecutor(files,targetPath).compile();
	}
}