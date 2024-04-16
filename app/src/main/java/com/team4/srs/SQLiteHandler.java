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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

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
    public static final String CUSTOMER_REVIEWS_TABLE = "Customer_Reviews";
    public static final String VENDOR_REVIEWS_TABLE = "Vendor_Reviews";
    public static final String VENDOR_DATES_TABLE = "Vendor_Dates";
    public static final String VENDOR_SERVICES_TABLE = "Vendor_Services";

    public SQLiteHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public void initializeDB() {
        SharedPreferences dbPrefs = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);

        if (!dbPrefs.getBoolean("isDBSetup", false)) {
            insertGuest("guestUserID", "Guest Account");
            insertTestVendors();
            insertTestVendorDates();
            insertTestCustomers();
            insertTestRequests();
            insertTestReviews();
        }

        SharedPreferences.Editor editor = dbPrefs.edit();
        editor.putBoolean("isDBSetup", true);
        editor.apply();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db, USERS_TABLE, "userID TEXT PRIMARY KEY", new String[]{"password TEXT", "name TEXT", "email TEXT", "phone TEXT", "address TEXT"});
        createTable(db, CUSTOMER_TABLE, "customerID TEXT PRIMARY KEY", new String[]{"points TEXT", "discounts TEXT", "FOREIGN KEY (customerID) REFERENCES Users (userID)"});
        createTable(db, VENDORS_TABLE, "vendorID TEXT PRIMARY KEY", new String[]{"name TEXT", "email TEXT", "phone TEXT", "address TEXT", "FOREIGN KEY (vendorID) REFERENCES Users (userID)"});
        createTable(db, REQUESTS_TABLE, "orderID INTEGER PRIMARY KEY AUTOINCREMENT", new String[]{"vendorID TEXT", "customerID TEXT", "service TEXT", "description TEXT", "time TEXT", "date TEXT", "other TEXT", "cost TEXT", "status TEXT", "FOREIGN KEY (vendorID) REFERENCES Users (userID)", "FOREIGN KEY (customerID) REFERENCES Users (userID)"});
        createTable(db, CUSTOMER_REVIEWS_TABLE, "reviewID INTEGER PRIMARY KEY AUTOINCREMENT", new String[]{"vendorID TEXT", "customerID TEXT", "rating TEXT", "comment TEXT", "FOREIGN KEY (vendorID) REFERENCES Users (userID)", "FOREIGN KEY (customerID) REFERENCES Users (userID)"});
        createTable(db, VENDOR_REVIEWS_TABLE, "vendorID TEXT PRIMARY KEY", new String[]{"num_ratings INTEGER", "avg_rating TEXT", "FOREIGN KEY (vendorID) REFERENCES Users (userID)"});
        createTable(db, VENDOR_DATES_TABLE, "vendorID TEXT", new String[]{"avail_date TEXT", "FOREIGN KEY (vendorID) REFERENCES Users (userID)"});
        createTable(db, VENDOR_SERVICES_TABLE, "vendorID TEXT", new String[]{"service TEXT", "rate TEXT", "FOREIGN KEY (vendorID) REFERENCES Users (userID)"});
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

    public String getUsersName(String userID) {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT name FROM " + USERS_TABLE + " WHERE userID = '" + userID + "' LIMIT 1;";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                db.close();
                return userID;
            }
            cursor.moveToNext();
            String name = cursor.getString(0);
            cursor.close();
            db.close();
            return name;
        }catch (SQLException e) {
            Log.e("SQLException", "getUsersName: " + e.getMessage());
            return userID;
        }
    }

    public String isCustomerOrVendor(String userID) {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String customerQuery = "SELECT * FROM " + CUSTOMER_TABLE + " WHERE customerID = '" + userID + "' LIMIT 1;";
            Cursor customerCursor = db.rawQuery(customerQuery, null);

            if (customerCursor.getCount() == 1) {
                customerCursor.close();
                db.close();
                return "customer";
            }

            String vendorQuery = "SELECT * FROM " + VENDORS_TABLE + " WHERE vendorID = '" + userID + "' LIMIT 1;";
            Cursor vendorCursor = db.rawQuery(vendorQuery, null);

            if (vendorCursor.getCount() == 1) {
                vendorCursor.close();
                db.close();
                return "vendor";
            }

            customerCursor.close();
            vendorCursor.close();
            db.close();
            return "guest";
        }catch (SQLException e) {
            Log.e("SQLException", "isCustomerOrVendor: " + e.getMessage());
            return "guest";
        }
    }

    public boolean deleteUser(String userID, boolean isCustomer, boolean isVendor) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            if (isCustomer) db.delete(CUSTOMER_TABLE, "customerID=?", new String[]{userID});
            if (isVendor) db.delete(VENDORS_TABLE, "vendorID=?", new String[]{userID});
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

    public boolean insertCustomers(String customerID, String points, String discounts) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("customerID", customerID);
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

    public String[] getCustomerRewards(String customerID) {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT points, discounts FROM " + CUSTOMER_TABLE + " WHERE customerID = '" + customerID + "' LIMIT 1;";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.getCount() <= 0) {
                cursor.close();
                db.close();
                return new String[]{"N/A", "N/A"};
            }

            cursor.moveToNext();
            String[] results = {cursor.getString(0), cursor.getString(1)};

            cursor.close();
            db.close();
            return results;
        }catch (SQLException e) {
            Log.e("SQLException", "getCustomerPoints: " + e.getMessage());
            return new String[]{"N/A", "N/A"};
        }
    }

    public List<String[]> getCustomerOrders(String customerID, Boolean isPaid) {
        try
        {
            List<String[]> list = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + REQUESTS_TABLE + " WHERE customerID = '" + customerID + "'";
            if (isPaid) query += " AND status LIKE 'Paid'";
            else query += " AND status NOT LIKE 'Paid'";

            Cursor cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()) {
                String[] rowData = new String[cursor.getColumnCount()];
                for (int i = 0; i < rowData.length; i++)
                {
                    rowData[i] = cursor.getString(i);
                }
                list.add(rowData);
            }
            cursor.close();
            db.close();
            return list;
        }catch (SQLException e) {
            Log.e("SQLException", "getCustomerOrders: " + e.getMessage());
            return null;
        }
    }

    public boolean insertCustomerReview(String vendorID, String customerID, String rating, String comment) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("vendorID", vendorID);
            values.put("customerID", customerID);
            values.put("rating", rating);
            values.put("comment", comment);

            db.insert(CUSTOMER_REVIEWS_TABLE, null, values);
            db.close();

            return updateVendorReviews(vendorID, rating);
        }catch (SQLException e) {
            Log.e("SQLException", "insertCustomerReview: " + e.getMessage());
            return false;
        }
    }

    public boolean insertVendors(String vendorID, String name, String email, String phone, String address, String services, String rates) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();

            //Initial insertion of vendor to vendors table
            ContentValues values = new ContentValues();
            values.put("vendorID", vendorID);
            values.put("name", name);
            values.put("email", email);
            values.put("phone", phone);
            values.put("address", address);
            //values.put("services", services);
            //values.put("rates", rates);
            db.insert(VENDORS_TABLE, null, values);

            //Insert services and rates into vendor services table
            String[] serviceParts = services.split(",");
            String[] rateParts = rates.split(",");
            for (int i = 0; i < serviceParts.length; i++)
            {
                ContentValues serviceValues = new ContentValues();
                serviceValues.put("vendorID", vendorID);
                serviceValues.put("service", serviceParts[i].trim());
                serviceValues.put("rate", rateParts[i].trim());
                db.insert(VENDOR_SERVICES_TABLE, null, serviceValues);
            }

            //Now initially insert into vendor reviews table
            ContentValues reviewValues = new ContentValues();
            reviewValues.put("vendorID", vendorID);
            reviewValues.put("num_ratings", 0);
            reviewValues.put("avg_rating", "0");
            db.insert(VENDOR_REVIEWS_TABLE, null, reviewValues);

            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "insertVendors: " + e.getMessage());
            return false;
        }
    }

    public boolean insertRequests(String customerID, String service, String desc, String time, String date, String other, String cost, String status) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("vendorID", "");
            values.put("customerID", customerID);
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

    public boolean insertVendorDate(String vendorID, String date) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("vendorID", vendorID);
            values.put("avail_date", date);

            db.insert(VENDOR_DATES_TABLE, null, values);
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "insertVendorDate: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> getVendorRequests(String vendorID, boolean isVendorSpecific) {
        try
        {
            List<String[]> list = new ArrayList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            String query;
            if (isVendorSpecific) {
                query = "SELECT * FROM " + REQUESTS_TABLE + " WHERE vendorID LIKE '%" +vendorID + "%'";
            } else {
                query = "SELECT r.orderID, r.vendorID, r.customerID, r.service, r.description, r.time, r.date, r.other, r.cost, r.status FROM " + REQUESTS_TABLE + " r";
                query += " LEFT JOIN " + VENDOR_SERVICES_TABLE + " s ON r.service = s.service WHERE s.vendorID = '" + vendorID + "'";
            }

            Cursor cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()) {
                String[] rowData = new String[cursor.getColumnCount()];
                for (int i = 0; i < rowData.length; i++)
                {
                    rowData[i] = cursor.getString(i);
                }
                list.add(rowData);
            }
            cursor.close();
            db.close();
            return list;
        }catch (SQLException e) {
            Log.e("SQLException", "getVendorRequests: " + e.getMessage());
            return null;
        }
    }

    public boolean updateVendorReviews(String vendorID, String newRating) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "UPDATE " + VENDOR_REVIEWS_TABLE + " SET avg_rating = printf('%.1f', (avg_rating * num_ratings + " + newRating + ") / (num_ratings + 1)), " +
                    "num_ratings = num_ratings + 1 WHERE vendorID = '" + vendorID + "';";
            Cursor cursor = db.rawQuery(query,null);
            cursor.moveToFirst();
            cursor.close();
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "updateVendorReviews: " + e.getMessage());
            return false;
        }
    }

    public String getVendorOverallRating(String vendorID) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT avg_rating FROM " + VENDOR_REVIEWS_TABLE + " WHERE vendorID = '" + vendorID + "'";
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            String result = cursor.getString(0);
            cursor.close();
            db.close();
            return result;
        }catch (SQLException e) {
            Log.e("SQLException", "getVendorOverallRating: " + e.getMessage());
            return null;
        }
    }

    public List<String[]> getVendorRatings(String vendorID) {
        try
        {
            List<String[]> list = new ArrayList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT * FROM " + CUSTOMER_REVIEWS_TABLE + " WHERE vendorID = '" + vendorID + "'";
            Cursor cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()) {
                String[] rowData = new String[cursor.getColumnCount()];
                for (int i = 0; i < rowData.length; i++)
                {
                    rowData[i] = cursor.getString(i);
                }
                list.add(rowData);
            }
            cursor.close();
            db.close();
            return list;
        }catch (SQLException e) {
            Log.e("SQLException", "getVendorRatings: " + e.getMessage());
            return null;
        }
    }

    public List<String[]> getVendorsByService(String service) {
        try
        {
            List<String[]> list = new ArrayList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT v.name, v.email, v.phone, v.address, s.service, s.rate FROM " + VENDORS_TABLE + " v" +
                    " LEFT JOIN " + VENDOR_SERVICES_TABLE + " s ON v.vendorID = s.vendorID " + "WHERE s.service LIKE '%" + service + "%';";
            Cursor cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()) {
                String[] rowData = new String[cursor.getColumnCount()];
                for (int i = 0; i < rowData.length; i++)
                {
                    rowData[i] = cursor.getString(i);
                }
                list.add(rowData);
            }
            cursor.close();
            db.close();
            return list;
        }catch (SQLException e) {
            Log.e("SQLException", "getVendorsByService: " + e.getMessage());
            return null;
        }
    }

    public List<String[]> getVendorsBySearch(String service, String rating, String date, String price) {
        try
        {
            List<String[]> list = new ArrayList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            StringBuilder query = new StringBuilder();
            query.append("SELECT v.name, v.email, v.phone, v.address, s.service, s.rate, r.avg_rating, d.avail_date FROM ").append(VENDORS_TABLE).append(" v");
            query.append(" LEFT JOIN ").append(VENDOR_SERVICES_TABLE).append(" s ON v.vendorID = s.vendorID");
            query.append(" LEFT JOIN ").append(VENDOR_REVIEWS_TABLE).append(" r ON v.vendorID = r.vendorID");
            query.append(" LEFT JOIN ").append(VENDOR_DATES_TABLE).append(" d ON v.vendorID = d.vendorID");

            boolean hasWhereClause = false;

            if (!service.isEmpty() || !rating.isEmpty() || !date.isEmpty() || !price.isEmpty()) {
                query.append(" WHERE ");
                if (!service.isEmpty()) {
                    query.append("s.service LIKE '%").append(service).append("%'");
                    hasWhereClause = true;
                }
                if (!rating.isEmpty()) {
                    if (hasWhereClause) {
                        query.append(" AND ");
                    }
                    //Parse the rating parameter into lower and upper bounds
                    String[] ratingRange = rating.split("-");
                    query.append("CAST(r.avg_rating AS REAL) BETWEEN ").append(ratingRange[0]).append(" AND ").append(ratingRange[1]);
                    hasWhereClause = true;
                }
                if (!date.isEmpty()) {
                    if (hasWhereClause) {
                        query.append(" AND ");
                    }
                    query.append("d.avail_date = '").append(date).append("'");
                    hasWhereClause = true;
                }
                if (!price.isEmpty()) {
                    if (hasWhereClause) {
                        query.append(" AND ");
                    }
                    String[] priceRange = price.split("[-+]");
                    if (priceRange.length == 2) {
                        query.append("CAST(s.rate AS REAL) BETWEEN ").append(priceRange[0]).append(" AND ").append(priceRange[1]);
                    } else {
                        query.append("CAST(s.rate AS REAL) >= ").append(priceRange[0]);
                    }
                    hasWhereClause = true;
                }
            }

            Cursor cursor = db.rawQuery(query.toString(), null);
            while(cursor.moveToNext()) {
                String[] rowData = new String[cursor.getColumnCount()];
                for (int i = 0; i < rowData.length; i++)
                {
                    rowData[i] = cursor.getString(i);
                }
                list.add(rowData);
            }
            cursor.close();
            db.close();
            return list;
        }catch (SQLException e) {
            Log.e("SQLException", "getVendorsBySearch: " + e.getMessage());
            return null;
        }
    }

    private void insertTestVendors() {
        //Appliance vendors
        insertUsers("user1001", "Passw1rd!", "John Doe", "johndoe@gmail.com", "8178888888", "123 Bob St, Burleson, TX, 76028");
        insertVendors("user1001", "Appliance Needs", "applianceneeds@gmail.com", "8178881234", "123 Test St, Burleson, TX, 76028", "Appliances", "25");

        insertUsers("user1002", "Passw1rd!", "Michael Adams", "michaeladams@gmail.com", "8177778888", "321 Elm St, Arlington, TX, 76001");
        insertVendors("user1002", "Elite Appliances", "eliteappliances@gmail.com", "8177771234", "321 Test St, Arlington, TX, 76001", "Appliances", "30");

        insertUsers("user1003", "Passw1rd!", "Sarah Johnson", "sarahjohnson@gmail.com", "8176667777", "456 Oak St, Fort Worth, TX, 76102");
        insertVendors("user1003", "Quick Fix Appliances", "quickfixappliances@gmail.com", "8176661234", "456 Test St, Fort Worth, TX, 76102", "Appliances", "20");

        //Electrical vendors
        insertUsers("user1004", "Passw1rd!", "Jane Smith", "janesmith@gmail.com", "8177777777", "456 Oak St, Arlington, TX, 76001");
        insertVendors("user1004", "Power Solutions", "powersolutions@gmail.com", "8177771234", "456 Test St, Arlington, TX, 76001", "Electrical", "30");

        insertUsers("user1005", "Passw1rd!", "Ryan Wilson", "ryanwilson@gmail.com", "8175556666", "789 Pine St, Dallas, TX, 75201");
        insertVendors("user1005", "Wired Solutions", "wiredsolutions@gmail.com", "8175551234", "789 Test St, Dallas, TX, 75201", "Electrical", "35");

        insertUsers("user1006", "Passw1rd!", "Jessica Martinez", "jessicamartinez@gmail.com", "8174445555", "852 Cedar St, Plano, TX, 75023");
        insertVendors("user1006", "Eco Electric", "ecoelectric@gmail.com", "8174441234", "852 Test St, Plano, TX, 75023", "Electrical", "25");

        //Plumbing vendors
        insertUsers("user1007", "Passw1rd!", "Mike Johnson", "mikejohnson@gmail.com", "8176666666", "789 Elm St, Fort Worth, TX, 76102");
        insertVendors("user1007", "Plumbing Experts", "plumbingexperts@gmail.com", "8176661234", "789 Test St, Fort Worth, TX, 76102", "Plumbing", "40");

        insertUsers("user1008", "Passw1rd!", "Daniel Brown", "danielbrown@gmail.com", "8173334444", "987 Walnut St, Irving, TX, 75038");
        insertVendors("user1008", "Reliable Plumbing", "reliableplumbing@gmail.com", "8173331234", "987 Test St, Irving, TX, 75038", "Plumbing", "45");

        insertUsers("user1009", "Passw1rd!", "Laura Taylor", "laurataylor@gmail.com", "8172223333", "654 Ash St, Garland, TX, 75040");
        insertVendors("user1009", "Aqua Plumbing", "aquaplumbing@gmail.com", "8172221234", "654 Test St, Garland, TX, 75040", "Plumbing", "55");

        //Home cleaning vendors
        insertUsers("user1010", "Passw1rd!", "Emily Brown", "emilybrown@gmail.com", "8175555555", "987 Maple St, Dallas, TX, 75201");
        insertVendors("user1010", "Sparkle Cleaners", "sparklecleaners@gmail.com", "8175551234", "987 Test St, Dallas, TX, 75201", "Home Cleaning", "35");

        insertUsers("user1011", "Passw1rd!", "Mark Hernandez", "markhernandez@gmail.com", "8171112222", "321 Birch St, Richardson, TX, 75080");
        insertVendors("user1011", "Fresh Start Cleaning", "freshstartcleaning@gmail.com", "8171111234", "321 Test St, Richardson, TX, 75080", "Home Cleaning", "40");

        insertUsers("user1012", "Passw1rd!", "Emma Clark", "emmaclark@gmail.com", "8179990000", "753 Cedar St, McKinney, TX, 75071");
        insertVendors("user1012", "Spotless Cleaning", "spotlesscleaning@gmail.com", "8179991234", "753 Test St, McKinney, TX, 75071", "Home Cleaning", "50");

        //Tutoring vendors
        insertUsers("user1013", "Passw1rd!", "David Lee", "davidlee@gmail.com", "8174444444", "654 Pine St, Plano, TX, 75023");
        insertVendors("user1013", "LearnWell Tutoring", "learnwelltutoring@gmail.com", "8174441234", "654 Test St, Plano, TX, 75023", "Tutoring", "50");

        insertUsers("user1014", "Passw1rd!", "Brian Scott", "brianscott@gmail.com", "8178889999", "159 Bob St, Burleson, TX, 76028");
        insertVendors("user1014", "Smart Learning", "smartlearning@gmail.com", "8178881234", "159 Test St, Burleson, TX, 76028", "Tutoring", "60");

        insertUsers("user1015", "Passw1rd!", "Kimberly Nguyen", "kimberlynguyen@gmail.com", "8177778888", "456 Oak St, Arlington, TX, 76001");
        insertVendors("user1015", "Knowledge Builders", "knowledgebuilders@gmail.com", "8177771234", "456 Test St, Arlington, TX, 76001", "Tutoring", "70");

        //Packaging and moving vendors
        insertUsers("user1016", "Passw1rd!", "Sarah Kim", "sarahkim@gmail.com", "8173333333", "321 Cedar St, Irving, TX, 75038");
        insertVendors("user1016", "Swift Movers", "swiftmovers@gmail.com", "8173331234", "321 Test St, Irving, TX, 75038", "Packaging & Moving", "60");

        insertUsers("user1017", "Passw1rd!", "Christopher Lopez", "christopherlopez@gmail.com", "8176667777", "789 Elm St, Fort Worth, TX, 76102");
        insertVendors("user1017", "Move Masters", "movemasters@gmail.com", "8176661234", "789 Test St, Fort Worth, TX, 76102", "Packaging & Moving", "65");

        insertUsers("user1018", "Passw1rd!", "Amanda Hall", "amandahall@gmail.com", "8175556666", "987 Pine St, Dallas, TX, 75201");
        insertVendors("user1018", "Speedy Movers", "speedymovers@gmail.com", "8175551234", "987 Test St, Dallas, TX, 75201", "Packaging & Moving", "55");

        //Computer repair vendors
        insertUsers("user1019", "Passw1rd!", "Kevin Garcia", "kevingarcia@gmail.com", "8172222222", "159 Walnut St, Garland, TX, 75040");
        insertVendors("user1019", "PC Wizards", "pcwizards@gmail.com", "8172221234", "159 Test St, Garland, TX, 75040", "Computer Repair", "45");

        insertUsers("user1020", "Passw1rd!", "Jason White", "jasonwhite@gmail.com", "8174445555", "654 Walnut St, Plano, TX, 75023");
        insertVendors("user1020", "Geek Squad", "geeksquad@gmail.com", "8174441234", "654 Test St, Plano, TX, 75023", "Computer Repair", "50");

        insertUsers("user1021", "Passw1rd!", "Stephanie Garcia", "stephaniegarcia@gmail.com", "8173334444", "852 Ash St, Irving, TX, 75038");
        insertVendors("user1021", "Tech Geniuses", "techgeniuses@gmail.com", "8173331234", "852 Test St, Irving, TX, 75038", "Computer Repair", "45");

        //Home repair and painting vendors
        insertUsers("user1022", "Passw1rd!", "Rachel Martinez", "rachelmartinez@gmail.com", "8171111111", "852 Ash St, Richardson, TX, 75080");
        insertVendors("user1022", "FixIt Pros", "fixitpros@gmail.com", "8171111234", "852 Test St, Richardson, TX, 75080", "Home Repair & Painting", "55");

        insertUsers("user1023", "Passw1rd!", "Brandon Martinez", "brandonmartinez@gmail.com", "8172223333", "321 Cedar St, Garland, TX, 75040");
        insertVendors("user1023", "Handy Hands", "handyhands@gmail.com", "8172221234", "321 Test St, Garland, TX, 75040", "Home Repair & Painting", "60");

        insertUsers("user1024", "Passw1rd!", "Nicole Thompson", "nicolethompson@gmail.com", "8171112222", "753 Pine St, Richardson, TX, 75080");
        insertVendors("user1024", "Master Craftsmen", "mastercraftsmen@gmail.com", "8171111234", "753 Test St, Richardson, TX, 75080", "Home Repair & Painting", "70");

        //Pest control vendors
        insertUsers("user1025", "Passw1rd!", "Chris Thompson", "christhompson@gmail.com", "8179999999", "753 Birch St, McKinney, TX, 75071");
        insertVendors("user1025", "Bug Busters", "bugbusters@gmail.com", "8179991234", "753 Test St, McKinney, TX, 75071", "Pest Control", "70");

        insertUsers("user1026", "Passw1rd!", "Adam Wilson", "adamwilson@gmail.com", "8179990000", "159 Bob St, McKinney, TX, 75071");
        insertVendors("user1026", "Critter Control", "crittercontrol@gmail.com", "8179991234", "159 Test St, McKinney, TX, 75071", "Pest Control", "75");

        insertUsers("user1027", "Passw1rd!", "Taylor Harris", "taylorharris@gmail.com", "8178889999", "456 Oak St, Burleson, TX, 76028");
        insertVendors("user1027", "Bug Off", "bugoff@gmail.com", "8178881234", "456 Test St, Burleson, TX, 76028", "Pest Control", "65");
    }

    private void insertTestVendorDates() {
        insertVendorDate("user1001", "5/1/2024"); insertVendorDate("user1001", "5/2/2024");
        insertVendorDate("user1002", "5/2/2024"); insertVendorDate("user1002", "5/3/2024");
        insertVendorDate("user1003", "5/3/2024"); insertVendorDate("user1003", "5/4/2024");
        insertVendorDate("user1004", "5/4/2024"); insertVendorDate("user1004", "5/5/2024");
        insertVendorDate("user1005", "5/5/2024"); insertVendorDate("user1005", "5/6/2024");
        insertVendorDate("user1006", "5/6/2024"); insertVendorDate("user1006", "5/7/2024");
        insertVendorDate("user1007", "5/7/2024"); insertVendorDate("user1007", "5/8/2024");
        insertVendorDate("user1008", "5/8/2024"); insertVendorDate("user1008", "5/9/2024");
        insertVendorDate("user1009", "5/9/2024"); insertVendorDate("user1009", "5/10/2024");
        insertVendorDate("user1010", "5/10/2024"); insertVendorDate("user1010", "5/11/2024");
        insertVendorDate("user1011", "5/11/2024"); insertVendorDate("user1011", "5/12/2024");
        insertVendorDate("user1012", "5/12/2024"); insertVendorDate("user1012", "5/13/2024");
        insertVendorDate("user1013", "5/13/2024"); insertVendorDate("user1013", "5/14/2024");
        insertVendorDate("user1014", "5/14/2024"); insertVendorDate("user1014", "5/15/2024");
        insertVendorDate("user1015", "5/15/2024"); insertVendorDate("user1015", "5/16/2024");
        insertVendorDate("user1016", "5/16/2024"); insertVendorDate("user1016", "5/17/2024");
        insertVendorDate("user1017", "5/17/2024"); insertVendorDate("user1017", "5/18/2024");
        insertVendorDate("user1018", "5/18/2024"); insertVendorDate("user1018", "5/19/2024");
        insertVendorDate("user1019", "5/19/2024"); insertVendorDate("user1019", "5/20/2024");
        insertVendorDate("user1020", "5/20/2024"); insertVendorDate("user1020", "5/21/2024");
        insertVendorDate("user1021", "5/21/2024"); insertVendorDate("user1021", "5/22/2024");
        insertVendorDate("user1022", "5/22/2024"); insertVendorDate("user1022", "5/23/2024");
        insertVendorDate("user1023", "5/23/2024"); insertVendorDate("user1023", "5/24/2024");
        insertVendorDate("user1024", "5/24/2024"); insertVendorDate("user1024", "5/25/2024");
        insertVendorDate("user1025", "5/25/2024"); insertVendorDate("user1025", "5/26/2024");
        insertVendorDate("user1026", "5/26/2024"); insertVendorDate("user1026", "5/27/2024");
        insertVendorDate("user1027", "5/27/2024"); insertVendorDate("user1027", "5/28/2024");
    }

    private void insertTestCustomers() {
        insertUsers("user1028", "Passw1rd!", "Alice Smith", "alice.smith@example.com", "8171234567", "456 Main St, Arlington, TX, 76010");
        insertCustomers("user1028", "0", "None");

        insertUsers("user1029", "P@ssw0rd!", "Bob Johnson", "bob.johnson@example.com", "8172345678", "789 Elm St, Fort Worth, TX, 76102");
        insertCustomers("user1029", "0", "None");

        insertUsers("user1030", "SecurePwd!", "Emily Davis", "emily.davis@example.com", "8173456789", "101 Oak St, Mansfield, TX, 76063");
        insertCustomers("user1030", "0", "None");

        insertUsers("user1031", "Secret123", "David Brown", "david.brown@example.com", "8174567890", "202 Pine St, Grapevine, TX, 76051");
        insertCustomers("user1031", "0", "None");

        insertUsers("user1032", "Pa$$w0rd!", "Sarah Wilson", "sarah.wilson@example.com", "8175678901", "303 Maple St, Irving, TX, 75038");
        insertCustomers("user1032", "0", "None");

        insertUsers("user1033", "MyPwd123", "Michael Martinez", "michael.martinez@example.com", "8176789012", "404 Cedar St, Keller, TX, 76248");
        insertCustomers("user1033", "0", "None");

        insertUsers("user1034", "StrongPwd!", "Jessica Lee", "jessica.lee@example.com", "8177890123", "505 Walnut St, Southlake, TX, 76092");
        insertCustomers("user1034", "0", "None");

        insertUsers("user1035", "Passw0rd!", "Daniel Harris", "daniel.harris@example.com", "8178901234", "606 Cherry St, Colleyville, TX, 76034");
        insertCustomers("user1035", "0", "None");

        insertUsers("user1036", "Password!", "Laura Clark", "laura.clark@example.com", "8179012345", "707 Pine St, Euless, TX, 76039");
        insertCustomers("user1036", "0", "None");

        insertUsers("user1037", "Pwd12345!", "Ryan Nguyen", "ryan.nguyen@example.com", "8170123456", "808 Oak St, Bedford, TX, 76021");
        insertCustomers("user1037", "0", "None");
    }

    private void insertTestRequests() {
        insertRequests("user1028", "Appliances", "I need my microwave installed.", "10:00 AM", "5/27/2024", "", "N/A", "Waiting for Bid");
        insertRequests("user1029", "Electrical", "I need an outlet fixed.", "11:00 AM", "5/26/2024", "", "N/A", "Waiting for Bid");
        insertRequests("user1030", "Plumbing", "I need my toilet fixed.", "9:00 AM", "5/25/2024", "", "N/A", "Waiting for Bid");
        insertRequests("user1031", "Home Cleaning", "I need my bedroom cleaned.", "8:00 AM", "5/24/2024", "", "N/A", "Waiting for Bid");
        insertRequests("user1032", "Tutoring", "I need help with math.", "12:00 PM", "5/23/2024", "", "N/A", "Waiting for Bid");
        insertRequests("user1033", "Packaging & Moving", "I need to move houses.", "1:00 PM", "5/22/2024", "", "N/A", "Waiting for Bid");
        insertRequests("user1034", "Computer Repair", "I need my computer fixed.", "2:00 PM", "5/21/2024", "", "N/A", "Waiting for Bid");
        insertRequests("user1035", "Home Repair & Painting", "I need my walls painted.", "3:00 PM", "5/20/2024", "", "N/A", "Waiting for Bid");
        insertRequests("user1036", "Pest Control", "I need bugs in my shed gone.", "4:00 PM", "5/19/2024", "", "N/A", "Waiting for Bid");
        insertRequests("user1037", "Appliances", "I need my washer and dryer installed.", "5:00 PM", "5/18/2024", "", "N/A", "Waiting for Bid");
    }

    private void insertTestReviews() {
        insertCustomerReview("user1001", "user1028", "4", "Good Job");
        insertCustomerReview("user1002", "user1028", "3", "Okay Job");
        insertCustomerReview("user1003", "user1028", "5", "Great Job!");

        insertCustomerReview("user1004", "user1029", "4", "Good Job");
        insertCustomerReview("user1005", "user1029", "3", "Okay Job");
        insertCustomerReview("user1006", "user1029", "5", "Great Job!");

        insertCustomerReview("user1007", "user1030", "4", "Good Job");
        insertCustomerReview("user1008", "user1030", "3", "Okay Job");
        insertCustomerReview("user1009", "user1030", "5", "Great Job!");

        insertCustomerReview("user1010", "user1031", "4", "Good Job");
        insertCustomerReview("user1011", "user1031", "3", "Okay Job");
        insertCustomerReview("user1012", "user1031", "5", "Great Job!");

        insertCustomerReview("user1013", "user1032", "4", "Good Job");
        insertCustomerReview("user1014", "user1032", "3", "Okay Job");
        insertCustomerReview("user1015", "user1032", "5", "Great Job!");

        insertCustomerReview("user1016", "user1033", "4", "Good Job");
        insertCustomerReview("user1017", "user1033", "3", "Okay Job");
        insertCustomerReview("user1018", "user1033", "5", "Great Job!");

        insertCustomerReview("user1019", "user1034", "4", "Good Job");
        insertCustomerReview("user1020", "user1034", "3", "Okay Job");
        insertCustomerReview("user1021", "user1034", "5", "Great Job!");

        insertCustomerReview("user1022", "user1035", "4", "Good Job");
        insertCustomerReview("user1023", "user1035", "3", "Okay Job");
        insertCustomerReview("user1024", "user1035", "5", "Great Job!");

        insertCustomerReview("user1025", "user1036", "4", "Good Job");
        insertCustomerReview("user1026", "user1036", "3", "Okay Job");
        insertCustomerReview("user1027", "user1036", "5", "Great Job!");

        insertCustomerReview("user1001", "user1037", "2", "Alright Job");
        insertCustomerReview("user1002", "user1037", "5", "Great Job!");
        insertCustomerReview("user1003", "user1037", "3", "Okay Job");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onCreate(db);
    }
}
