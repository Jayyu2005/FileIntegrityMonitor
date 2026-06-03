package Def;
import java.io.*;
import java.nio.file.*;
import java.security.*;
public class FileHasher {
	
	public static String hashFile(Path filePath) throws Exception {
		
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] fileByte = Files.readAllBytes(filePath);
		byte[] hashbyte = digest.digest(fileByte);
		StringBuilder hexString = new StringBuilder();
		for (byte b : hashbyte) {
		    hexString.append(String.format("%02x", b));
		}
		
		return hexString.toString();
		
	}
}
