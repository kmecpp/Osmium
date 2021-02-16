package com.kmecpp.osmium.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.kmecpp.osmium.api.util.StringUtil;

public class StringUtilTest {

	public static void main(String[] args) {
		//		System.out.println("-a".substring(0, "-a".length() - 1));
		System.out.println(StringUtil.zfill("1", 0));
	}

	@Test
	public void testIsAlpha() {
		assertTrue(StringUtil.isAlpha("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		assertTrue(StringUtil.isAlpha("wASIFJWEOUIFWBHUYEWWEFIFUW"));
		assertFalse(StringUtil.isAlpha("hello world"));
		assertFalse(StringUtil.isAlpha("["));
		assertFalse(StringUtil.isAlpha("`"));
		assertFalse(StringUtil.isAlpha("'"));
		assertFalse(StringUtil.isAlpha("{"));
		assertFalse(StringUtil.isAlpha("@"));
		assertFalse(StringUtil.isAlpha("7"));
	}

	@Test
	public void testIsAlphaNumeric() {
		assertTrue(StringUtil.isAlphaNumeric("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		assertTrue(StringUtil.isAlphaNumeric("wASIFJWEOUIFWBHUYEWWEFIFUW"));
		assertTrue(StringUtil.isAlphaNumeric("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ013456789"));
		assertTrue(StringUtil.isAlphaNumeric("0123456789"));
		assertTrue(StringUtil.isAlphaNumeric("7"));
		assertFalse(StringUtil.isAlphaNumeric("hello world"));
		assertFalse(StringUtil.isAlphaNumeric("["));
		assertFalse(StringUtil.isAlphaNumeric("`"));
		assertFalse(StringUtil.isAlphaNumeric("'"));
		assertFalse(StringUtil.isAlphaNumeric("{"));
		assertFalse(StringUtil.isAlphaNumeric("@"));
		assertFalse(StringUtil.isAlphaNumeric("/"));
		assertFalse(StringUtil.isAlphaNumeric(":"));

	}

}
