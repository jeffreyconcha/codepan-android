package com.codepan.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RootDetectionUtils {

	//Check if the device is rooted
	public static boolean isDeviceCompromised() {
		return checkBuildTags() || checkSuperuserApk() || checkSuExists() || canExecuteSuCommand() ||
			detectFridaClasses() || isFridaProcessPresent();
	}

	private static boolean checkBuildTags() {
		String buildTags = android.os.Build.TAGS;
		return buildTags != null && buildTags.contains("test-keys");
	}

	private static boolean checkSuperuserApk() {
		final String[] paths = {
			"/system/app/Superuser.apk",
			"/sbin/su",
			"/system/bin/su",
			"/system/xbin/su",
			"/data/local/xbin/su",
			"/data/local/bin/su",
			"/system/sd/xbin/su",
			"/system/bin/failsafe/su",
			"/data/local/su",
			"/su/bin/su",
			"/magisk",
			"/system/bin/.ext/.su",
			"/system/usr/we-need-root/su.backup",
			"/system/xbin/mu",
			"/cache/magisk.log"
		};
		for(String path : paths) {
			if(new File(path).exists()) return true;
		}
		return false;
	}

	private static boolean checkSuExists() {
		try {
			Process process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			return in.readLine() != null;
		}
		catch(Exception e) {
			return false;
		}
	}

	private static boolean canExecuteSuCommand() {
		try {
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
			return process.exitValue() == 0;
		}
		catch(Exception e) {
			return false;
		}
	}

	private static boolean detectFridaClasses() {
		String[] suspiciousClasses = {
			"re.frida.server", "frida", "gumjs", "gum-js-loop", "gmain"
		};
		for(String className : suspiciousClasses) {
			try {
				Class.forName(className);
				return true;
			}
			catch(ClassNotFoundException ignored) {
			}
		}
		return false;
	}

	private static boolean isFridaProcessPresent() {
		try {
			Process process = Runtime.getRuntime().exec("ps");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while((line = reader.readLine()) != null) {
				if(line.contains("frida") || line.contains("gadget") || line.contains("gum-js")) {
					return true;
				}
			}
		}
		catch(IOException ignored) {
		}
		return false;
	}
}
