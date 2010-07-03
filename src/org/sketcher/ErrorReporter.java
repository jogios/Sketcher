package org.sketcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

public class ErrorReporter implements Thread.UncaughtExceptionHandler {
	private static final String REPORT_EMAIL = "yavalek@gmail.com";

	private String versionName;
	private String packageName;
	private String filePath;
	private String phoneModel;
	private String androidVersion;
	private String board;
	private String brand;
	// String CPU_ABI;
	private String device;
	private String display;
	private String product;
	private String tags;
	private long time;
	private String type;
	private String user;

	private Thread.UncaughtExceptionHandler previousHandler;
	public final static ErrorReporter INSTANCE = new ErrorReporter();
	private Context context;

	private ErrorReporter() {
	}

	public void init(Context context) {
		// 8 is Build.VERSION_CODES.FROYO
		if (Build.VERSION.SDK_INT > 7) {
			// bypass
			return;
		}
		this.context = context;
		previousHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		recoltInformations();
		checkErrorAndSendMail();
	}

	private long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	private long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	private void recoltInformations() {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			// Version
			versionName = pi.versionName;
			// Package name
			packageName = pi.packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		// Files dir for storing the stack traces
		filePath = context.getFilesDir().getAbsolutePath();
		// Device model
		phoneModel = Build.MODEL;
		// Android version
		androidVersion = Build.VERSION.RELEASE;

		board = Build.BOARD;
		brand = Build.BRAND;
		device = Build.DEVICE;
		display = Build.DISPLAY;
		product = Build.PRODUCT;
		tags = Build.TAGS;
		time = Build.TIME;
		type = Build.TYPE;
		user = Build.USER;
	}

	private String createInformationString() {
		String info = "";
		info += "Version : " + versionName;
		info += "\n";
		info += "Package : " + packageName;
		info += "\n";
		info += "Phone Model : " + phoneModel;
		info += "\n";
		info += "Android Version : " + androidVersion;
		info += "\n";
		info += "Board : " + board;
		info += "\n";
		info += "Brand : " + brand;
		info += "\n";
		info += "Device : " + device;
		info += "\n";
		info += "Display : " + display;
		info += "\n";
		info += "Product : " + product;
		info += "\n";
		info += "Tags : " + tags;
		info += "\n";
		info += "Time : " + time;
		info += "\n";
		info += "Type : " + type;
		info += "\n";
		info += "User : " + user;
		info += "\n";
		info += "Total Internal memory : " + getTotalInternalMemorySize();
		info += "\n";
		info += "Available Internal memory : "
				+ getAvailableInternalMemorySize();
		info += "\n";

		return info;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		String report = "";
		Date curDate = new Date();
		report += "Error Report collected on : " + curDate.toString();
		report += "\n";
		report += "\n";
		report += "Informations :";
		report += "\n";
		report += "==============";
		report += "\n";
		report += "\n";
		report += createInformationString();

		report += "\n\n";
		report += "Stack : \n";
		report += "======= \n";
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		String stacktrace = result.toString();
		report += stacktrace;

		report += "\n";
		report += "Cause : \n";
		report += "======= \n";

		// If the exception was thrown in a background thread inside
		// AsyncTask, then the actual exception can be found with getCause
		Throwable cause = e.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			report += result.toString();
			cause = cause.getCause();
		}
		printWriter.close();
		report += "****  End of current Report ***";
		saveAsFile(report);
		// SendErrorMail( Report );
		previousHandler.uncaughtException(t, e);
	}

	private void sendErrorMail(String errorMsg) {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		String subject = "Crash report";
		sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { REPORT_EMAIL });
		sendIntent.putExtra(Intent.EXTRA_TEXT, errorMsg);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		sendIntent.setType("message/rfc822");
		context.startActivity(Intent.createChooser(sendIntent, "Title:"));
	}

	private void saveAsFile(String errorContent) {
		try {
			Random generator = new Random();
			int random = generator.nextInt(99999);
			String FileName = "stack-" + random + ".stacktrace";
			FileOutputStream trace = context.openFileOutput(FileName,
					Context.MODE_PRIVATE);
			trace.write(errorContent.getBytes());
			trace.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private String[] getErrorFileList() {
		File dir = new File(filePath + "/");
		// Try to create the files folder if it doesn't exist
		dir.mkdir();
		// Filter for ".stacktrace" files
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".stacktrace");
			}
		};
		return dir.list(filter);
	}

	private boolean isThereAnyErrorFile() {
		return getErrorFileList().length > 0;
	}

	private void checkErrorAndSendMail() {
		try {
			if (isThereAnyErrorFile()) {
				String wholeErrorText = "";
				String[] errorFileList = getErrorFileList();
				int curIndex = 0;
				// We limit the number of crash reports to send ( in order not
				// to be too slow )
				final int MaxSendMail = 5;
				for (String curString : errorFileList) {
					if (curIndex++ <= MaxSendMail) {
						wholeErrorText += "New Trace collected :\n";
						wholeErrorText += "=====================\n ";
						String filePath = this.filePath + "/" + curString;
						BufferedReader input = new BufferedReader(
								new FileReader(filePath));
						String line;
						while ((line = input.readLine()) != null) {
							wholeErrorText += line + "\n";
						}
						input.close();
					}

					// DELETE FILES !!!!
					File curFile = new File(filePath + "/" + curString);
					curFile.delete();
				}
				sendErrorMail(wholeErrorText);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}