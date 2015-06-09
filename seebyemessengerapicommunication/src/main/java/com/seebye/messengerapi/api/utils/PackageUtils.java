package com.seebye.messengerapi.api.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.seebye.messengerapi.api.App;

import java.util.List;

/**
 * Created by Seebye on 07.04.2015.
 * This file is needed for the communication between Commandr and Seebye Messenger API
 */
public class PackageUtils
{
	/**
	 * Determines whether an app with the passed package is installed
	 *
	 * @param strPackage
	 * @return
	 */
	public static boolean exists(String strPackage)
	{
		boolean bRet = false;
		PackageManager pm = App.getInstance().getPackageManager();

		if(pm != null)
		{
			try
			{
				pm.getPackageInfo(strPackage, PackageManager.GET_META_DATA);
				bRet = true;
			}
			catch (PackageManager.NameNotFoundException e)
			{
			//	e.printStackTrace();
			}
		}

		return bRet;
	}

	public static String generateSignatureHash(String strPackage)
	{
		StringBuilder stringBuilder = new StringBuilder();

		try
		{
			PackageInfo packageInfo = App.getInstance().getPackageManager().getPackageInfo(strPackage, PackageManager.GET_SIGNATURES);

			for(int i = 0; i < packageInfo.signatures.length; i++)
			{
				stringBuilder.append(packageInfo.signatures[i].toCharsString());
			}

		}
		catch(PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}

		return HashUtils.sha256(stringBuilder.toString());
	}

	/**
	 * Searches for the {@link ComponentName} of the BroadcastReceiver of specific package and action.<br>
	 * Useful if we want to interact with other apps right after the install..<br>
	 * as android blocks non explicit ({@link Intent#setPackage(String)} doesn't count as explicit in this case)<br> broadcasts until the first start of the app.
	 *
	 * @param strPackage
	 * @param strAction
	 * @return				the componentname (=first occurrence) or null
	 */
	public static ComponentName getBroadcastWithAction(String strPackage, String strAction)
	{
		ComponentName cn = null;
		PackageManager pm = App.getInstance().getPackageManager();
		Intent intent = new Intent(strAction);
		intent.setPackage(strPackage);
		List<ResolveInfo> aApps = pm.queryBroadcastReceivers(intent, 0);

		if(aApps.size() > 0)
		{
			cn = new ComponentName(strPackage, aApps.get(0).activityInfo.name);
		}

		return cn;
	}

}
