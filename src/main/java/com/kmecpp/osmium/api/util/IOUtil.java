package com.kmecpp.osmium.api.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;

public class IOUtil {

	public static final short DEFAULT_BUFFER_SIZE = 4096;

	protected IOUtil() {
	}

	public static File createFile(String path) {
		return createFile(new File(path));
	}

	/**
	 * Creates the given file if it does not exist. If the file already exists
	 * this method will fail silently.
	 * 
	 * @param file
	 *            the file to create
	 * @return the original file instance passed to the method
	 */
	public static File createFile(File file) {
		try {
			if (!file.exists()) {
				File parent = file.getParentFile();
				if (parent != null) {
					parent.mkdirs();
				}
				file.createNewFile();
			}
		} catch (IOException e) {
			throw new RuntimeException();
		}
		return file;
	}

	/**
	 * Copies the specified file or folder to the new directory
	 * 
	 * @param source
	 *            the source file to copy
	 * @param destination
	 *            the destination to copy the file to
	 * @throws IOException
	 *             if an IOException occurs
	 */
	public static void copyFile(File source, File destination) throws IOException {
		if (source.isDirectory()) {
			if (!destination.exists()) {
				destination.mkdirs();
			}
			String files[] = source.list();
			if (files != null) {
				for (String file : files) {
					File srcFile = new File(source, file);
					File destFile = new File(destination, file);
					copyFile(srcFile, destFile);
				}
			}
		} else {
			try (FileChannel in = new FileInputStream(source).getChannel(); FileChannel out = new FileOutputStream(destination).getChannel()) {
				out.transferFrom(in, 0, in.size());
			}
		}
	}

	/**
	 * Deletes a file or folder if it exists, regardless of its contents
	 * 
	 * @param file
	 *            the file or directory to delete
	 */
	public static void deleteFile(File file) {
		if (file.exists() && file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isFile()) {
						f.delete();
					} else {
						deleteFile(f);
					}
				}
			}
			file.delete();
		}
	}

	/**
	 * Reads the given URL into a byte array
	 * 
	 * @param url
	 *            the url to read
	 * @return the contents of the URL as a byte array
	 * @throws IOException
	 *             if an IO exception occurs
	 */
	public static byte[] readBytes(URL url) throws IOException {
		InputStream inputStream = url.openStream();
		ByteArrayOutputStream data = new ByteArrayOutputStream();

		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int pos = 0;
		while ((pos = inputStream.read(buffer, 0, buffer.length)) != -1) {
			data.write(buffer, 0, pos);
		}

		data.flush();

		return data.toByteArray();
	}

	/**
	 * Splits the contents of the data at the URL into an array of its lines,
	 * which this method assumes are separated by '\n' characters.
	 * 
	 * @param url
	 *            the URL to read
	 * @return an array of lines read from the URL content
	 * @throws IOException
	 *             if an error occurs while reading from the url
	 */
	public static String[] readLines(URL url) throws IOException {
		return StringUtil.getLines(readString(url));
	}

	/**
	 * Splits the contents of the file into an array of its lines, which this
	 * method assumes are separated by '\n' characters.
	 * 
	 * @param file
	 *            the file to read
	 * @return an array of the file's lines
	 * @throws IOException
	 *             if an error occurs while reading from file
	 */
	public static String[] readLines(File file) throws IOException {
		return StringUtil.getLines(readString(file));
	}

	/**
	 * Attempts to read data into a String from the given path and returns that
	 * String
	 * 
	 * @param path
	 *            the path to read from
	 * @return the data contained at the given path
	 * @throws IOException
	 *             if an error occurs while reading from the source
	 */
	public static String readString(String path) throws IOException {
		return readString(new File(path));
	}

	/**
	 * Attempts to read data into a String from the given {@link File} and
	 * returns the contents
	 * 
	 * @param file
	 *            the file to read from
	 * @return the data contained in the file
	 * @throws IOException
	 *             if an error occurs while reading
	 */
	public static String readString(File file) throws IOException {
		return readString(file.toURI().toURL());
	}

	/**
	 * Attempts to read data into a String from the given URL and returns that
	 * String
	 * 
	 * @param url
	 *            the URL to read from
	 * @return the data read from the URL
	 * @throws IOException
	 *             if an error occurs while reading from the URL
	 */
	public static String readString(URL url) throws IOException {
		return readString(url.openStream());
	}

	/**
	 * High performance read from an {@link InputStream} into a String
	 * 
	 * @param inputStream
	 *            the input stream from which to read
	 * @return the string read from the reader
	 * @throws IOException
	 *             if an IOException occurs
	 */
	public static String readString(InputStream inputStream) throws IOException {
		InputStreamReader reader = new InputStreamReader(inputStream);
		StringWriter sw = new StringWriter();
		char[] buffer = new char[4096];
		int pos = 0;
		while ((pos = reader.read(buffer)) != -1) {
			sw.write(buffer, 0, pos);
		}
		return sw.toString();
	}

	/**
	 * Gets an {@link HttpURLConnection} for the given URL with the given
	 * connection timeout and read timeout. This method also creates the
	 * connection with a default User-Agent of Mozilla/5.0 to avoid being
	 * filtered by many sites.
	 * 
	 * @param url
	 *            the URL to connect to
	 * @param connectTimeout
	 *            the timeout to use when opening a communications link to the
	 *            resource referenced by this URLConnection
	 * @param readTimeout
	 *            the timeout to use when reading from an input stream when a
	 *            connection is established to a resource
	 * @return the HTTP URL connection
	 * @throws IOException
	 *             if an IOException occurs
	 */
	public static HttpURLConnection getHttpConnection(URL url, int connectTimeout, int readTimeout) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		connection.setConnectTimeout(connectTimeout);
		connection.setReadTimeout(readTimeout);
		return connection;
	}

	/**
	 * Helper method for closing resources. If the resource cannot be closed, an
	 * exception is printed but not thrown.
	 * 
	 * @param close
	 *            the autocloseable's to close
	 */
	public static void close(AutoCloseable... close) {
		for (AutoCloseable closeable : close) {
			if (closeable == null) {
				continue;
			}
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Writes the given text to the file and throws an {@link IOException} if an
	 * error occurs
	 * 
	 * @param file
	 *            the file to write to
	 * @param text
	 *            the text to write
	 * @throws IOException
	 *             if an error occurs
	 */
	public static void write(File file, String text) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file), DEFAULT_BUFFER_SIZE)) {
			writer.write(text);
		}
	}

}
