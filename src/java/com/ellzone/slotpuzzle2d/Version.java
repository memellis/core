/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.logging.FileHandler;

public class Version {
	public static final String VERSION_FILE = "version_info/version";
	public static final String VERSION;
	public static final int MAJOR;
	public static final int MINOR;
	public static final int REVISION;

	private static final short MAJOR_PARSE_POSITION = 1;
	private static final short MINOR_PARSE_POSITION = 2;
	private static final short REVISION_PARSE_POSITION = 3;

	static {
		VERSION = readVersion();
		try {
			String[] v = VERSION.split("\\.");
			MAJOR = v.length < MAJOR_PARSE_POSITION ? 0 : Integer.valueOf(v[0]);
			MINOR = v.length < MINOR_PARSE_POSITION ? 0 : Integer.valueOf(v[1]);
			REVISION = v.length < REVISION_PARSE_POSITION ? 0 : Integer.valueOf(v[2]);
		}
		catch (Throwable t) {
			throw new GdxRuntimeException("Invalid version " + VERSION, t);
		}
	}

	private static String readVersion() {
		return Gdx.files.internal(VERSION_FILE).readString();
	}

	public static boolean isHigher (int major, int minor, int revision) {
		return isHigherEqual(major, minor, revision+1);
	}

	public static boolean isHigherEqual (int major, int minor, int revision) {
		if (MAJOR != major)
			return MAJOR > major;
		if (MINOR != minor)
			return MINOR > minor;
		return REVISION >= revision;
	}

	public static boolean isLower (int major, int minor, int revision) {
		return isLowerEqual(major, minor, revision-1);
	}

	public static boolean isLowerEqual (int major, int minor, int revision) {
		if (MAJOR != major)
			return MAJOR < major;
		if (MINOR != minor)
			return MINOR < minor;
		return REVISION <= revision;
	}
}