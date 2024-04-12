package com.team4.srs;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHandler extends SQLiteOpenHelper
{
    private Context context;

    //Main database information
    private static final String DB_NAME = "ServiceSystem.db";
    private static final int DB_VERSION = 1;

    //All table names
    public static final String USERS_TABLE = "Users";
    public static final String CUSTOMER_TABLE = "Customers";
    public static final String VENDORS_TABLE = "Vendors";

    public SQLiteHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public void initializeDB() {
        SharedPreferences dbPrefs = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);

        if (!dbPrefs.getBoolean("isDBSetup", false)) {
            insertUsers("user1234", "Passw1rd!", "John Doe", "johndoe@gmail.com", "8188888888", "123 Bob St, Burleson, TX, 76028");
            insertCustomers("user1234", "0", null);
        }

        SharedPreferences.Editor editor = dbPrefs.edit();
        editor.putBoolean("isDBSetup", true);
        editor.apply();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        createTable(db, USERS_TABLE, "userID TEXT", new String[]{"password TEXT", "name TEXT", "email TEXT", "phone TEXT", "address TEXT"});
        createTable(db, CUSTOMER_TABLE, "userID TEXT", new String[]{"points TEXT", "discounts TEXT", "FOREIGN KEY (userID) REFERENCES Users (userID)"});
        createTable(db, VENDORS_TABLE, "userID TEXT", new String[]{"name TEXT", "email TEXT", "phone TEXT", "address TEXT", "services TEXT", "rates TEXT", "FOREIGN KEY (userID) REFERENCES Users (userID)"});
    }

    public void createTable(SQLiteDatabase db, String tableName, String primaryKey, String[] columns) {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (" + primaryKey + " PRIMARY KEY, ");
        for (int i = 0; i < columns.length; i++)
        {
            query.append(columns[i]);
            if (i != columns.length -1) query.append(", ");
        }
        query.append(")");
        db.execSQL(query.toString());
    }

    public boolean insertUsers(String userID, String password, String name, String email, String phone, String address) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("userID", userID);
            values.put("password", password);
            values.put("name", name);
            values.put("email", email);
            values.put("phone", phone);
            values.put("address", address);

            db.insert(USERS_TABLE, null, values);
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "insertUsers: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(String userID, boolean isCustomer, boolean isVendor) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            if (isCustomer) db.delete(CUSTOMER_TABLE, "userID=?", new String[]{userID});
            if (isVendor) db.delete(VENDORS_TABLE, "userID=?", new String[]{userID});
            db.delete(USERS_TABLE, "userID=?", new String[]{userID} );
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "deleteUser: " + e.getMessage());
            return false;
        }
    }

    public boolean checkUserIDExists(String userID) {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + USERS_TABLE + " WHERE userID = '" + userID + "' LIMIT 1;";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                db.close();
                return false;
            }
            cursor.close();
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "checkUserIDExists: " + e.getMessage());
            return false;
        }
    }

    public boolean checkLoginUser(String userID, String password) {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + USERS_TABLE + " WHERE userID = '" + userID + "' AND password = '" + password + "' LIMIT 1;";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                db.close();
                return false;
            }
            cursor.close();
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "checkLoginUser: " + e.getMessage());
            return false;
        }
    }

    public boolean checkForgotPassUser(String userID, String email, String phone) {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + USERS_TABLE + " WHERE userID = '" + userID + "' AND email = '" + email + "' AND phone = '" + phone + "' LIMIT 1;";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                db.close();
                return false;
            }
            cursor.close();
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "checkForgotPassUser: " + e.getMessage());
            return false;
        }
    }

    public boolean changePasswordUser(String userID, String newPassword) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("password", newPassword);

            db.update(USERS_TABLE, values, "userID=?", new String[]{userID});
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "changePasswordUser: " + e.getMessage());
            return false;
        }
    }

    public boolean insertCustomers(String userID, String points, String discounts) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("userID", userID);
            values.put("points", points);
            values.put("discounts", discounts);

            db.insert(CUSTOMER_TABLE, null, values);
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "insertCustomers: " + e.getMessage());
            return false;
        }
    }

    public boolean insertVendors(String userID, String name, String email, String phone, String address, String services, String rates) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("userID", userID);
            values.put("name", name);
            values.put("email", email);
            values.put("phone", phone);
            values.put("address", address);
            values.put("services", services);
            values.put("rates", rates);

            db.insert(VENDORS_TABLE, null, values);
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "insertVendors: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onCreate(db);
    }
}
