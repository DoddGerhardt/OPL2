import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.filefilter.FileFilterUtils;

public class FileExtractor {
	
	private File path;
	private String extension;
	
	public FileExtractor(File path, String extension) {
		this.path = path;
		this.extension = extension;
	}
	public FileExtractor(String path, String extension) {
		this(new File(path),extension);
	}

	public List<File> extract() {
		List<File> list = new LinkedList<File>();
		extract(path,list);
		return list;
	}
	
	private void extract(File currentFile, List<File> files) {
		if (currentFile != null) {
			if (currentFile.isFile()) {
				if (currentFile.toString().endsWith(extension)) {
					files.add(currentFile);
				}
			} else {
				for (File f : currentFile.listFiles()) {
					extract(f,files);
				}
			}
		}
	}
	
	public static List<File> extract(File path, String extension) {
		return new FileExtractor(path,extension).extract();
	}
	public static List<File> extract(String path, String extension) {
		return new FileExtractor(path,extension).extract();
	}
}
