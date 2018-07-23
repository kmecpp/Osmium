package com.kmecpp.osmium.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public abstract class FileUtil {

	/**
	 * Creates a file if it does not exist
	 * 
	 * @param file
	 *            the file
	 */
	public static void createFile(File file) {
		if (!file.exists()) {
			try {
				File parent = file.getParentFile();
				if (parent != null) {
					parent.mkdirs();
				}
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Copies the specified file or folder to the new directory
	 * 
	 * @param source
	 *            the source file to copy
	 * @param destination
	 *            the destination to copy the file to
	 */
	public static void copy(File source, File destination) {
		try {
			if (source.isDirectory()) {
				if (!destination.exists()) {
					destination.mkdirs();
				}
				String files[] = source.list();
				if (files != null) {
					for (String file : files) {
						File srcFile = new File(source, file);
						File destFile = new File(destination, file);
						copy(srcFile, destFile);
					}
				}
			} else {
				try (FileChannel in = new FileInputStream(source).getChannel(); FileChannel out = new FileOutputStream(destination).getChannel()) {
					out.transferFrom(in, 0, in.size());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Deletes a file or folder if it exists, regardless of its contents
	 * 
	 * @param file
	 *            the file or directory to delete
	 */
	public static void delete(File file) {
		if (file.exists() && file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isFile()) {
						f.delete();
					} else {
						delete(f);
					}
				}
			}
			file.delete();
		}
	}

}
