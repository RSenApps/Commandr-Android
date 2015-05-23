package com.seebye.messengerapi.api.utils;

import com.seebye.messengerapi.api.constants.General;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by Nico on 27.04.2015.
 */
public class LuckyUtils
{
	private static boolean s_bFoundInXposed = false;

	public static void checkXposedLog()
	{
		/**
		 * Should be readable
		 * @see https://github.com/rovo89/XposedBridge/blob/afcc3e1e788ea44bfd00245a24b5dfe6c86aa3d0/src/de/robv/android/xposed/XposedBridge.java#L102
		 */
		File f = new File("/data/data/de.robv.android.xposed.installer/log/error.log");
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(f));
			String strLine;

			while((strLine = br.readLine()) != null
					&& !s_bFoundInXposed)
			{
				s_bFoundInXposed = strLine.contains("Loading modules from /data/app/"+General.PKG_LUCKYPATCHER);
			}

			br.close();
		}
		catch(IOException e)
		{
			//e.printStackTrace();
		}
	}

	/**
	 * This method checks if Lucky Patcher is installed.
	 *
	 * (This method is used to block the communication between modules and the api on devices with LP)
	 */
	public static boolean isInstalled()
	{
		/*int nModifiersGetModifiersMethod = 0;
		int nModifiersGetModifiersConstructor = 0;
		int nModifiersExists = 0;
		int nModifiersFixSlashes = 0;
		int nModifiersFileConstructor = 0;
		int nModifiersCanonicalizePath = 0;
		int nModifiersVerify = 0;
		try
		{
			// no need to obfuscate.. this code is open source..
			nModifiersGetModifiersMethod = Method.class.getMethod("getModifiers").getModifiers();
			nModifiersGetModifiersConstructor = Method.class.getMethod("getModifiers").getModifiers();
			nModifiersExists = File.class.getMethod("exists").getModifiers();
			nModifiersFileConstructor = File.class.getConstructor(String.class).getModifiers();
			nModifiersFixSlashes = File.class.getDeclaredMethod("fixSlashes", String.class).getModifiers();
			nModifiersCanonicalizePath = File.class.getDeclaredMethod("canonicalizePath", String.class).getModifiers(); // this method is native
			nModifiersVerify = java.security.Signature.class.getMethod("verify", byte[].class).getModifiers();
		}
		catch(NoSuchMethodException e)
		{
			e.printStackTrace();
		}*/

		return
				s_bFoundInXposed
				// we should be able to look for the package as long as no modifications are made by Xposed
				|| PackageUtils.exists(General.PKG_LUCKYPATCHER)
				// Lucky Patcher Xposed-Directory
				|| new File("/data/lp/").exists()
				// Lucky Patcher Xposed-Directory - Settings-File or so..
				|| new File("/data/lp/xposed").exists()


				// Directories and Files are able to have different chmods.. -> /data/data isn't readable but the following directories and files should be ..

				// Apk-Directory
				|| new File("/data/app/"+General.PKG_LUCKYPATCHER+"-1").exists()
				|| new File("/data/app/"+General.PKG_LUCKYPATCHER+"-2").exists()

				// Apk-Directory Lib-Directory
				|| new File("/data/app/"+General.PKG_LUCKYPATCHER+"-1/lib").exists()
				|| new File("/data/app/"+General.PKG_LUCKYPATCHER+"-2/lib").exists()

				// Apk-Directory Apk-File
				|| new File("/data/app/"+General.PKG_LUCKYPATCHER+"-1/base.apk").exists()
				|| new File("/data/app/"+General.PKG_LUCKYPATCHER+"-2/base.apk").exists()
				|| new File("/data/app/"+General.PKG_LUCKYPATCHER+"-1.apk").exists()
				|| new File("/data/app/"+General.PKG_LUCKYPATCHER+"-2.apk").exists()

				// Data-Directory
				|| new File("/data/data/"+ General.PKG_LUCKYPATCHER).exists()

				// Data-Directory Lib-Directory
				|| new File("/data/data/"+General.PKG_LUCKYPATCHER+"/lib").exists()


				// nothing found..
				// let's do some checks whether there are some methods hocked by xposed
				// it's not acceptable to hook these methods as they could break our checks.. so we're going to handle them with the same treatment as lp
				/*|| (nModifiersExists & Modifier.NATIVE) != 0
				|| (nModifiersFixSlashes & Modifier.NATIVE) != 0
				|| (nModifiersFileConstructor & Modifier.NATIVE) != 0

				|| (nModifiersGetModifiersMethod & Modifier.NATIVE) != 0
				|| (nModifiersGetModifiersConstructor & Modifier.NATIVE) != 0

				/** let's check whether there are some changes on {@link java.security.Signature#verify(byte[])}* /
				|| (nModifiersVerify & Modifier.NATIVE) != 0

				// good.. looks like nothing was changed.. let's do a last check on a native method
				// to see whether the return value is always the same
				|| (nModifiersCanonicalizePath & Modifier.NATIVE) == 0*/
				;
	}
}
