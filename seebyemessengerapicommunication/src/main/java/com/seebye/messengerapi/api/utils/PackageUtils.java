package com.seebye.messengerapi.api.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.seebye.messengerapi.api.App;
import com.seebye.messengerapi.api.constants.General;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nico on 07.04.2015.
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

	/**
	 * Determines whether an app with the passed package is installed
	 *
	 * @param strPackage
	 * @return
	 */
	public static boolean hasPermission(String strPackage, String strPermission)
	{
		boolean bRet = false;
		PackageManager pm = App.getInstance().getPackageManager();
		PackageInfo pi;

		if(pm != null)
		{
			/*try
			{
				pi = pm.getPackageInfo(strPackage, PackageManager.GET_PERMISSIONS);

				for(int i = 0;
					i < pi.permissions.length
					&& !bRet
						; i++)
				{
					bRet = pi.permissions[i].name.equals(strPermission);
				}*/
				bRet = PackageManager.PERMISSION_GRANTED == pm.checkPermission(strPermission, strPackage);
			/*}
			catch (PackageManager.NameNotFoundException e)
			{
				e.printStackTrace();
			}*/
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

	public static boolean isSystemApp(String strPackage)
	{
		int nMask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
		ApplicationInfo ai = getApplicationInfo(strPackage);
		boolean bSystemApp = false;

		if(ai != null)
		{
			bSystemApp = (ai.flags & nMask) != 0;
		}

		return bSystemApp;
	}

	/**
	 * Loads the {@link ApplicationInfo} by the package
	 *
	 * @param strPackage
	 * @return				The ApplicationInfo or null on a failure
	 */
	public static ApplicationInfo getApplicationInfo(String strPackage)
	{
		ApplicationInfo ai = null;

		try
		{
			ai = App.getInstance().getPackageManager().getApplicationInfo(strPackage, 0);
		}
		catch(PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}

		return ai;
	}

	/**
	 * Determines the name of the app by it's package name
	 *
	 * @param strPackage	The package of the app
	 * @return				The name of the app or null on a failure
	 */
	public static String getAppName(String strPackage)
	{
		ApplicationInfo ai = getApplicationInfo(strPackage);
		CharSequence acName = null;

		if(ai != null)
		{
			acName = App.getInstance().getPackageManager().getApplicationLabel(ai);
		}

		return acName == null ? null : acName.toString();
	}

	public static boolean isModule(String strPackage)
	{
		return hasPermission(strPackage, General.PERMISSION_MESSENGERAPI);
	}

	public static ArrayList<String> queryModules()
	{
		ArrayList<String> astrPackages = new ArrayList<>();
		PackageManager pm = App.getInstance().getPackageManager();
		Intent intent = new Intent(General.ACTION_MESSENGERAPI);
		List<ResolveInfo> aApps = pm.queryBroadcastReceivers(intent, 0);

		for(ResolveInfo ai : aApps)
		{
			if(isModule(ai.resolvePackageName))
			{
				astrPackages.add(ai.resolvePackageName);
			}
		}

		return astrPackages;
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
