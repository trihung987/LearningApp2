package me.trihung.learningapp2.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Database extends SQLiteOpenHelper {

    private static final String DB_NAME = "LearningEnglish.db";
    private final Context context;
    private final String dbPath;

    public Database(Context context) {
        super(context, context.getFilesDir().getPath() + "/" + DB_NAME, null, 1);
        this.context = context;
        this.dbPath = context.getFilesDir().getPath() + "/" + DB_NAME;
        copyDatabaseIfNeeded();
    }

    private void copyDatabaseIfNeeded() {
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            try {
                InputStream is = context.getAssets().open(DB_NAME);
                OutputStream os = new FileOutputStream(dbFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

                os.flush();
                os.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void query_noresult(String sql){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }

    public Cursor query_hasresult(String sql){
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Không cần nếu có sẵn .db
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}