package de.fhg.fokus.odp.portal.searchgui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * General string utils
 */
public class StringUtils {

	final public static char COMMA = ',';
	final public static String COMMA_STR = ",";
	final public static char COLON = ':';
	final public static String COLON_STR = ":";
	final public static char ESCAPE_CHAR = '\\';
	final public static char SLASH_CHAR = '/';

	/**
	 * Escape colon in the string using the default escape char
	 * 
	 * @param str
	 *            a string
	 * @return an escaped string
	 */
	public static String escapeColonString(String str) {
		return escapeString(str, ESCAPE_CHAR, COLON);
	}

	/**
	 * Escape commas in the string using the default escape char
	 * 
	 * @param str
	 *            a string
	 * @return an escaped string
	 */
	public static String escapeString(String str) {
		return escapeString(str, ESCAPE_CHAR, COMMA);
	}

	/**
	 * Escape <code>charToEscape</code> in the string with the escape char
	 * <code>escapeChar</code>
	 * 
	 * @param str
	 *            string
	 * @param escapeChar
	 *            escape char
	 * @param charToEscape
	 *            the char to be escaped
	 * @return an escaped string we escape only the :// in the string for URL
	 *         ckan has solr and ckan have problem with :
	 */
	public static String escapeString(String str, char escapeChar,
			char charToEscape) {
		if (str == null) {
			return null;
		}
		char nextChar = COMMA;
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char curChar = str.charAt(i);
			if (i < str.length() - 1)
				nextChar = str.charAt(i + 1);
			else
				nextChar = COMMA;
			if ((curChar == escapeChar || curChar == charToEscape)
					&& (nextChar == SLASH_CHAR)) {
				// special char
				result.append(escapeChar);
			}
			result.append(curChar);
		}
		return result.toString();
	}

	/*
	 * Leerzeichen werden durch AND Operatoren ersetzt. Strings nicht ersetzen,
	 * z.B. "Badestellen Berlin" Bei Einsatz von Operatoren im Suchbegriff nicht
	 * ersetzen, z.B. Badestellen AND Berlin
	 */
	public static String removeBlankStrings(String queryString) {
		StringBuilder sb = new StringBuilder();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(
				queryString);

		while (m.find()) {
			if (m.group(1).matches("AND|OR|NOT")) {
				sb.append(m.group(1));
				sb.append(" ");

			} else {
				sb.append(m.group(1));
				sb.append(" AND ");
			}
		}
		String outStr = sb.toString();
		if (sb.length() > 0) {
			outStr = outStr.replace("AND AND", "AND").replace("AND NOT", "NOT")
					.replace("AND OR", "OR");
			if (outStr.length() > 5 && outStr.endsWith(" AND ")) {
				outStr = outStr.substring(0, outStr.length() - 5);
			} else
				outStr = outStr.trim();
		}
		return outStr;
	}

	/*
	 * ANDs werden durch leerzeichen ersetzt
	 */
	public static String removeAnds(String queryString) {
		String outStr = queryString;
		if (outStr != null && outStr.length() > 0) {
			outStr = outStr.replace(" AND ", " ");
			outStr = outStr.trim();
		}
		return outStr;
	}
}
