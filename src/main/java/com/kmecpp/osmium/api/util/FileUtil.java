package com.kmecpp.osmium.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;

public abstract class FileUtil {

	/**
	 * Creates a file if it does not exist
	 * 
	 * @param file
	 *            the file
	 * @return true if a file was created
	 */
	public static boolean createFile(File file) {
		if (!file.exists()) {
			try {
				File parent = file.getParentFile();
				if (parent != null) {
					parent.mkdirs();
				}
				file.createNewFile();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
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
				try (FileInputStream in = new FileInputStream(source); FileOutputStream out = new FileOutputStream(destination)) {
					out.getChannel().transferFrom(in.getChannel(), 0, in.getChannel().size());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
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
	public static void copyExcluding(File source, File destination, String... excludes) {
		HashSet<String> excludeSet = new HashSet<>();
		for (String exclude : excludes) {
			excludeSet.add(exclude);
		}
		copyExcluding(source, destination, excludeSet);
	}

	/**
	 * Copies the specified file or folder to the new directory
	 * 
	 * @param source
	 *            the source file to copy
	 * @param destination
	 *            the destination to copy the file to
	 */
	public static void copyExcluding(File source, File destination, HashSet<String> excludes) {
		try {
			if (source.isDirectory()) {
				if (!destination.exists()) {
					destination.mkdirs();
				}
				String files[] = source.list();
				if (files != null) {
					for (String file : files) {
						if (excludes.contains(file)) {
							continue;
						}
						File srcFile = new File(source, file);
						File destFile = new File(destination, file);
						copyExcluding(srcFile, destFile);
					}
				}
			} else {
				try (FileInputStream in = new FileInputStream(source); FileOutputStream out = new FileOutputStream(destination)) {
					out.getChannel().transferFrom(in.getChannel(), 0, in.getChannel().size());
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
	 * @return whether or not the file was deleted successfully or doesn't exist
	 */
	public static boolean delete(File file) {
		if (file.isDirectory()) {
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
		}
		return !file.exists() || file.delete();
	}

	public static boolean isSymlink(File file) {
		return file != null && Files.isSymbolicLink(file.toPath());
	}

}
