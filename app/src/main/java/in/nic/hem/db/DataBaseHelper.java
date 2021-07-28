package in.nic.hem.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataBaseHelper extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	private static String DB_PATH = "";
	private static String DB_NAME = "PFD.db";
	private SQLiteDatabase myDataBase;
	private final Context myContext;
	private SQLiteOpenHelper sqLiteOpenHelper;
	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DataBaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
		DB_PATH = myContext.getDatabasePath(DB_NAME).toString();
	}
	public void createDataBaseIfNotExist() throws IOException {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			// do nothing - database already exist
		} else {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		} catch (Exception e) {}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null;
	}
	private void copyDataBase() throws IOException {
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		String outFileName = DB_PATH;
		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}
//	public void BackupDatabase() throws IOException {
//		File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//		FileInputStream fileInputStream;
//		FileOutputStream fileOutputStream;
//		SimpleDateFormat sdf = new SimpleDateFormat("_yyyy_MM_dd_HHmmss", Locale.getDefault());
//		String currentDateandTime = sdf.format(new Date());
//		if (sd.canWrite()) {
//			File currentDB = new File(DB_PATH);
//			File backupDirectory = new File(sd, "HEM/Backup/");
//			if(!backupDirectory.exists()){
//				backupDirectory.mkdirs();
//			}
//			fileInputStream = new FileInputStream(currentDB);
//			FileChannel src = fileInputStream.getChannel();
//			fileOutputStream = new FileOutputStream(backupDirectory.getPath() + "/"+DB_NAME+currentDateandTime);
//			FileChannel dst = fileOutputStream.getChannel();
//			dst.transferFrom(src, 0, src.size());
//			src.close();
//			dst.close();
//			Toast.makeText(myContext, "Backup Saved at Download/HEM/Backup/: ", Toast.LENGTH_LONG).show();
//		}
//	}
	public void createDatabaseBackup(FileOutputStream fileOutputStream) throws IOException{
		FileInputStream fileInputStream;
		File currentDB = new File(DB_PATH);
		fileInputStream = new FileInputStream(currentDB);
		FileChannel src = fileInputStream.getChannel();
		FileChannel dst = fileOutputStream.getChannel();
		dst.transferFrom(src, 0, src.size());
		src.close();
		dst.close();
	}
	public void restoreDatabaseBackup(FileInputStream fileInputStream) throws IOException{
		File currentDB = new File(DB_PATH);
		FileOutputStream fileOutputStream = new FileOutputStream(currentDB);
		FileChannel dst = fileOutputStream.getChannel();
		FileChannel src = fileInputStream.getChannel();
		dst.transferFrom(src, 0, src.size());
		src.close();
		dst.close();
	}
//	public void restoreDatabase(String restoreDB_directory) throws IOException {
//		File currentDB = new File(DB_PATH);
//		File restoreDirectory = new File(restoreDB_directory);
//
//
//		BackupDatabase();
//
//		FileInputStream fileInputStream;
//		FileOutputStream fileOutputStream;
//		fileOutputStream = new FileOutputStream(currentDB);
//		FileChannel dst = fileOutputStream.getChannel();
//		fileInputStream = new FileInputStream(restoreDirectory.getPath());
//		FileChannel src = fileInputStream.getChannel();
//		dst.transferFrom(src, 0, src.size());
//		src.close();
//		dst.close();
//		Toast.makeText(myContext, "Data restoration finished.", Toast.LENGTH_LONG).show();
//	}
//
	/*public SQLiteDatabase getDB_Connection()
	{
		SQLiteDatabase db=null;
		try
		{
			String myPath = DB_PATH + DB_NAME;
			db = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		}
		catch(Exception ex)
		{
			Log.d(APP_TAG, "problem in creating DB connection");
			ex.printStackTrace();
		}		
		return db;
	}*/
	public SQLiteDatabase getDB_Connection()
	{
		SQLiteDatabase db=null;
		db = this.getWritableDatabase();
		return db;
	}
	public Connection connect() {
		// SQLite connection string
		String url = "jdbc:sqlite:"+DB_PATH;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		if (newVersion > oldVersion)
		{
			Log.v("Database Upgrade", "Database version higher than old.");
			db_delete();
		}
	}

	//delete database
	public void db_delete()
	{
		File file = new File(DB_PATH);
		if(file.exists())
		{
			file.delete();
			System.out.println("delete database file.");
		}
	}

	// Add your public helper methods to access and get content from the
	// database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd
	// be easy
	// to you to create adapters for your views.

}