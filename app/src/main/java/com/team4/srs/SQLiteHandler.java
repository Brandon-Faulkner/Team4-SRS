package com.team4.srs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class SQLiteHandler extends SQLiteOpenHelper
{
    //Main database information
    private static final String DB_NAME = "ServiceSystem.db";
    private static final int DB_VERSION = 1;

    //Users Table
    public static final String USERS_TABLE = "Users";
    public static final String CUSTOMER_TABLE = "Customers";
    public static final String VENDORS_TABLE = "Vendors";

    public SQLiteHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        db.close();
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

    public boolean checkUserIDExists(String userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USERS_TABLE + " WHERE userID = " + userID;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }

    public void insertUsers(String userID, String password, String name, String email, String phone, String address) {
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
    }

    public void insertCustomers(String userID, String points, String discounts) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("userID", userID);
        values.put("points", points);
        values.put("discounts", discounts);

        db.insert(CUSTOMER_TABLE, null, values);
        db.close();
    }

    public void insertVendors(String userID, String name, String email, String phone, String address, String services, String rates) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("userID", userID);
        values.put("name", name);
        values.put("email", email);
        values.put("phone", phone);
        values.put("address", address);
        values.put("services", services);
        values.put("rates", rates);

        db.insert(USERS_TABLE, null, values);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onCreate(db);
    }
}
