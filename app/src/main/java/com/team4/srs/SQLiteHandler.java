package com.team4.srs;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final String REQUESTS_TABLE = "Requests";

    public SQLiteHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public void initializeDB() {
        SharedPreferences dbPrefs = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);

        if (!dbPrefs.getBoolean("isDBSetup", false)) {
            insertGuest("guestUserID", "Guest Account");
            insertTestVendors();
        }

        SharedPreferences.Editor editor = dbPrefs.edit();
        editor.putBoolean("isDBSetup", true);
        editor.apply();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db, USERS_TABLE, "userID TEXT PRIMARY KEY", new String[]{"password TEXT", "name TEXT", "email TEXT", "phone TEXT", "address TEXT"});
        createTable(db, CUSTOMER_TABLE, "userID TEXT PRIMARY KEY", new String[]{"points TEXT", "discounts TEXT", "FOREIGN KEY (userID) REFERENCES Users (userID)"});
        createTable(db, VENDORS_TABLE, "userID TEXT PRIMARY KEY", new String[]{"name TEXT", "email TEXT", "phone TEXT", "address TEXT", "services TEXT", "rates TEXT", "FOREIGN KEY (userID) REFERENCES Users (userID)"});
        createTable(db, REQUESTS_TABLE, "orderID INTEGER PRIMARY KEY AUTOINCREMENT", new String[]{"userID TEXT", "service TEXT", "description TEXT", "time TEXT", "date TEXT", "other TEXT", "cost TEXT", "status TEXT", "FOREIGN KEY (userID) REFERENCES Users (userID)"});
    }

    public void createTable(SQLiteDatabase db, String tableName, String primaryKey, String[] columns) {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (" + primaryKey + ", ");
        for (int i = 0; i < columns.length; i++)
        {
            query.append(columns[i]);
            if (i != columns.length -1) query.append(", ");
        }
        query.append(")");
        db.execSQL(query.toString());
    }

    public void insertGuest(String userID, String name) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("userID", userID);
            values.put("password", (String) null);
            values.put("name", name);
            values.put("email", (String) null);
            values.put("phone", (String) null);
            values.put("address", (String) null);

            db.insert(USERS_TABLE, null, values);
            db.close();
        }catch (SQLException e) {
            Log.e("SQLException", "insertGuest: " + e.getMessage());
        }
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

    public boolean insertRequests(String userID, String service, String desc, String time, String date, String other, String cost, String status) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("userID", userID);
            values.put("service", service);
            values.put("description", desc);
            values.put("time", time);
            values.put("date", date);
            values.put("other", other);
            values.put("cost", cost);
            values.put("status", status);

            db.insert(REQUESTS_TABLE, null, values);
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "insertRequests: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> getVendorsByService(String service) {
        try
        {
            List<String[]> list = new ArrayList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT name, email, phone, address, services, rates FROM " + VENDORS_TABLE + " WHERE services LIKE '%" + service + "%';";
            Cursor cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()) {
                String[] rowData = new String[cursor.getColumnCount()];
                for (int i = 0; i < rowData.length; i++)
                {
                    rowData[i] = cursor.getString(i);
                }
                list.add(rowData);
            }
            Log.i("SQLData", "getVendorsByService: " + list);
            cursor.close();
            db.close();
            return list;
        }catch (SQLException e) {
            Log.e("SQLException", "getVendorsByService: " + e.getMessage());
            return null;
        }
    }

    private void insertTestVendors() {
        //Appliance vendors
        insertUsers("user1001", "Passw1rd!", "John Doe", "johndoe@gmail.com", "8178888888", "123 Bob St, Burleson, TX, 76028");
        insertCustomers("user1001", "0", null);
        insertVendors("user1001", "Appliance Needs", "applianceneeds@gmail.com", "8178881234", "123 Test St, Burleson, TX, 76028", "Appliances", "25");

        insertUsers("user1002", "Passw1rd!", "Michael Adams", "michaeladams@gmail.com", "8177778888", "321 Elm St, Arlington, TX, 76001");
        insertCustomers("user1002", "0", null);
        insertVendors("user1002", "Elite Appliances", "eliteappliances@gmail.com", "8177771234", "321 Test St, Arlington, TX, 76001", "Appliances", "30");

        insertUsers("user1003", "Passw1rd!", "Sarah Johnson", "sarahjohnson@gmail.com", "8176667777", "456 Oak St, Fort Worth, TX, 76102");
        insertCustomers("user1003", "0", null);
        insertVendors("user1003", "Quick Fix Appliances", "quickfixappliances@gmail.com", "8176661234", "456 Test St, Fort Worth, TX, 76102", "Appliances", "20");

        //Electrical vendors
        insertUsers("user1004", "Passw1rd!", "Jane Smith", "janesmith@gmail.com", "8177777777", "456 Oak St, Arlington, TX, 76001");
        insertCustomers("user1004", "0", null);
        insertVendors("user1004", "Power Solutions", "powersolutions@gmail.com", "8177771234", "456 Test St, Arlington, TX, 76001", "Electrical", "30");

        insertUsers("user1005", "Passw1rd!", "Ryan Wilson", "ryanwilson@gmail.com", "8175556666", "789 Pine St, Dallas, TX, 75201");
        insertCustomers("user1005", "0", null);
        insertVendors("user1005", "Wired Solutions", "wiredsolutions@gmail.com", "8175551234", "789 Test St, Dallas, TX, 75201", "Electrical", "35");

        insertUsers("user1006", "Passw1rd!", "Jessica Martinez", "jessicamartinez@gmail.com", "8174445555", "852 Cedar St, Plano, TX, 75023");
        insertCustomers("user1006", "0", null);
        insertVendors("user1006", "Eco Electric", "ecoelectric@gmail.com", "8174441234", "852 Test St, Plano, TX, 75023", "Electrical", "25");

        //Plumbing vendors
        insertUsers("user1007", "Passw1rd!", "Mike Johnson", "mikejohnson@gmail.com", "8176666666", "789 Elm St, Fort Worth, TX, 76102");
        insertCustomers("user1007", "0", null);
        insertVendors("user1007", "Plumbing Experts", "plumbingexperts@gmail.com", "8176661234", "789 Test St, Fort Worth, TX, 76102", "Plumbing", "40");

        insertUsers("user1008", "Passw1rd!", "Daniel Brown", "danielbrown@gmail.com", "8173334444", "987 Walnut St, Irving, TX, 75038");
        insertCustomers("user1008", "0", null);
        insertVendors("user1008", "Reliable Plumbing", "reliableplumbing@gmail.com", "8173331234", "987 Test St, Irving, TX, 75038", "Plumbing", "45");

        insertUsers("user1009", "Passw1rd!", "Laura Taylor", "laurataylor@gmail.com", "8172223333", "654 Ash St, Garland, TX, 75040");
        insertCustomers("user1009", "0", null);
        insertVendors("user1009", "Aqua Plumbing", "aquaplumbing@gmail.com", "8172221234", "654 Test St, Garland, TX, 75040", "Plumbing", "55");

        //Home cleaning vendors
        insertUsers("user1010", "Passw1rd!", "Emily Brown", "emilybrown@gmail.com", "8175555555", "987 Maple St, Dallas, TX, 75201");
        insertCustomers("user1010", "0", null);
        insertVendors("user1010", "Sparkle Cleaners", "sparklecleaners@gmail.com", "8175551234", "987 Test St, Dallas, TX, 75201", "Home Cleaning", "35");

        insertUsers("user1011", "Passw1rd!", "Mark Hernandez", "markhernandez@gmail.com", "8171112222", "321 Birch St, Richardson, TX, 75080");
        insertCustomers("user1011", "0", null);
        insertVendors("user1011", "Fresh Start Cleaning", "freshstartcleaning@gmail.com", "8171111234", "321 Test St, Richardson, TX, 75080", "Home Cleaning", "40");

        insertUsers("user1012", "Passw1rd!", "Emma Clark", "emmaclark@gmail.com", "8179990000", "753 Cedar St, McKinney, TX, 75071");
        insertCustomers("user1012", "0", null);
        insertVendors("user1012", "Spotless Cleaning", "spotlesscleaning@gmail.com", "8179991234", "753 Test St, McKinney, TX, 75071", "Home Cleaning", "50");

        //Tutoring vendors
        insertUsers("user1013", "Passw1rd!", "David Lee", "davidlee@gmail.com", "8174444444", "654 Pine St, Plano, TX, 75023");
        insertCustomers("user1013", "0", null);
        insertVendors("user1013", "LearnWell Tutoring", "learnwelltutoring@gmail.com", "8174441234", "654 Test St, Plano, TX, 75023", "Tutoring", "50");

        insertUsers("user1014", "Passw1rd!", "Brian Scott", "brianscott@gmail.com", "8178889999", "159 Bob St, Burleson, TX, 76028");
        insertCustomers("user1014", "0", null);
        insertVendors("user1014", "Smart Learning", "smartlearning@gmail.com", "8178881234", "159 Test St, Burleson, TX, 76028", "Tutoring", "60");

        insertUsers("user1015", "Passw1rd!", "Kimberly Nguyen", "kimberlynguyen@gmail.com", "8177778888", "456 Oak St, Arlington, TX, 76001");
        insertCustomers("user1015", "0", null);
        insertVendors("user1015", "Knowledge Builders", "knowledgebuilders@gmail.com", "8177771234", "456 Test St, Arlington, TX, 76001", "Tutoring", "70");

        //Packaging and moving vendors
        insertUsers("user1016", "Passw1rd!", "Sarah Kim", "sarahkim@gmail.com", "8173333333", "321 Cedar St, Irving, TX, 75038");
        insertCustomers("user1016", "0", null);
        insertVendors("user1016", "Swift Movers", "swiftmovers@gmail.com", "8173331234", "321 Test St, Irving, TX, 75038", "Packaging & Moving", "60");

        insertUsers("user1017", "Passw1rd!", "Christopher Lopez", "christopherlopez@gmail.com", "8176667777", "789 Elm St, Fort Worth, TX, 76102");
        insertCustomers("user1017", "0", null);
        insertVendors("user1017", "Move Masters", "movemasters@gmail.com", "8176661234", "789 Test St, Fort Worth, TX, 76102", "Packaging & Moving", "65");

        insertUsers("user1018", "Passw1rd!", "Amanda Hall", "amandahall@gmail.com", "8175556666", "987 Pine St, Dallas, TX, 75201");
        insertCustomers("user1018", "0", null);
        insertVendors("user1018", "Speedy Movers", "speedymovers@gmail.com", "8175551234", "987 Test St, Dallas, TX, 75201", "Packaging & Moving", "55");

        //Computer repair vendors
        insertUsers("user1019", "Passw1rd!", "Kevin Garcia", "kevingarcia@gmail.com", "8172222222", "159 Walnut St, Garland, TX, 75040");
        insertCustomers("user1019", "0", null);
        insertVendors("user1019", "PC Wizards", "pcwizards@gmail.com", "8172221234", "159 Test St, Garland, TX, 75040", "Computer Repair", "45");

        insertUsers("user1020", "Passw1rd!", "Jason White", "jasonwhite@gmail.com", "8174445555", "654 Walnut St, Plano, TX, 75023");
        insertCustomers("user1020", "0", null);
        insertVendors("user1020", "Geek Squad", "geeksquad@gmail.com", "8174441234", "654 Test St, Plano, TX, 75023", "Computer Repair", "50");

        insertUsers("user1021", "Passw1rd!", "Stephanie Garcia", "stephaniegarcia@gmail.com", "8173334444", "852 Ash St, Irving, TX, 75038");
        insertCustomers("user1021", "0", null);
        insertVendors("user1021", "Tech Geniuses", "techgeniuses@gmail.com", "8173331234", "852 Test St, Irving, TX, 75038", "Computer Repair", "45");

        //Home repair and painting vendors
        insertUsers("user1022", "Passw1rd!", "Rachel Martinez", "rachelmartinez@gmail.com", "8171111111", "852 Ash St, Richardson, TX, 75080");
        insertCustomers("user1022", "0", null);
        insertVendors("user1022", "FixIt Pros", "fixitpros@gmail.com", "8171111234", "852 Test St, Richardson, TX, 75080", "Home Repair & Painting", "55");

        insertUsers("user1023", "Passw1rd!", "Brandon Martinez", "brandonmartinez@gmail.com", "8172223333", "321 Cedar St, Garland, TX, 75040");
        insertCustomers("user1023", "0", null);
        insertVendors("user1023", "Handy Hands", "handyhands@gmail.com", "8172221234", "321 Test St, Garland, TX, 75040", "Home Repair & Painting", "60");

        insertUsers("user1024", "Passw1rd!", "Nicole Thompson", "nicolethompson@gmail.com", "8171112222", "753 Pine St, Richardson, TX, 75080");
        insertCustomers("user1024", "0", null);
        insertVendors("user1024", "Master Craftsmen", "mastercraftsmen@gmail.com", "8171111234", "753 Test St, Richardson, TX, 75080", "Home Repair & Painting", "70");

        //Pest control vendors
        insertUsers("user1025", "Passw1rd!", "Chris Thompson", "christhompson@gmail.com", "8179999999", "753 Birch St, McKinney, TX, 75071");
        insertCustomers("user1025", "0", null);
        insertVendors("user1025", "Bug Busters", "bugbusters@gmail.com", "8179991234", "753 Test St, McKinney, TX, 75071", "Pest Control", "70");

        insertUsers("user1026", "Passw1rd!", "Adam Wilson", "adamwilson@gmail.com", "8179990000", "159 Bob St, McKinney, TX, 75071");
        insertCustomers("user1026", "0", null);
        insertVendors("user1026", "Critter Control", "crittercontrol@gmail.com", "8179991234", "159 Test St, McKinney, TX, 75071", "Pest Control", "75");

        insertUsers("user1027", "Passw1rd!", "Taylor Harris", "taylorharris@gmail.com", "8178889999", "456 Oak St, Burleson, TX, 76028");
        insertCustomers("user1027", "0", null);
        insertVendors("user1027", "Bug Off", "bugoff@gmail.com", "8178881234", "456 Test St, Burleson, TX, 76028", "Pest Control", "65");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onCreate(db);
    }
}
