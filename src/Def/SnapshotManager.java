package Def;
import java.io.*;
import java.nio.file.*;
import java.util.*;
public class SnapshotManager {

	public static Map<String, String> takeSnapshot(String folderPath) throws Exception {
		
		Map <String, String> snapshot = new HashMap<>();
		Path folder = Paths.get(folderPath);
		Files.walk(folder)
	    .filter(Files::isRegularFile) // skip folders only get files
	    .forEach(file -> {
	        // file is each individual file as a Path
	        try {
				snapshot.put(file.toString(), FileHasher.hashFile(file));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    });
		return snapshot;
	}
	public static void saveSnapshot(Map<String, String> snapshot, String savePath) throws Exception {
		try(FileOutputStream fos = new FileOutputStream(savePath);
		         ObjectOutputStream out = new ObjectOutputStream(fos)) {
			out.writeObject(snapshot);
		}
	}
	public static Map<String, String> loadSnapshot(String savePath) throws Exception {
		
		try(FileInputStream fis = new FileInputStream(savePath);
				ObjectInputStream in = new ObjectInputStream(fis)){
			return (Map<String, String>) in.readObject();
		}
	}
	
}