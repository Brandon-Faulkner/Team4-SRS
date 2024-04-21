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
import java.util.List;

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
    public static final String VENDOR_BIDS_TABLE = "Vendor_Bids";

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
        createTable(db, CUSTOMER_REVIEWS_TABLE, "reviewID INTEGER PRIMARY KEY AUTOINCREMENT", new String[]{"orderID INTEGER", "vendorID TEXT", "customerID TEXT", "rating TEXT", "comment TEXT", "FOREIGN KEY (orderID) REFERENCES Requests (orderID)", "FOREIGN KEY (vendorID) REFERENCES Users (userID)", "FOREIGN KEY (customerID) REFERENCES Users (userID)"});
        createTable(db, VENDOR_REVIEWS_TABLE, "vendorID TEXT PRIMARY KEY", new String[]{"num_ratings INTEGER", "avg_rating TEXT", "FOREIGN KEY (vendorID) REFERENCES Users (userID)"});
        createTable(db, VENDOR_DATES_TABLE, "vendorID TEXT", new String[]{"avail_date TEXT", "FOREIGN KEY (vendorID) REFERENCES Users (userID)"});
        createTable(db, VENDOR_SERVICES_TABLE, "vendorID TEXT", new String[]{"service TEXT", "rate TEXT", "FOREIGN KEY (vendorID) REFERENCES Users (userID)"});
        createTable(db, VENDOR_BIDS_TABLE, "bidID INTEGER PRIMARY KEY AUTOINCREMENT", new String[]{"orderID INTEGER", "vendorID TEXT", "bid TEXT", "FOREIGN KEY (orderID) REFERENCES Requests (orderID)", "FOREIGN KEY (vendorID) REFERENCES Users (userID)"});
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

    public String getUsersName(String userID, String userType) {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String query;
            if (userType.equals("vendor")) {
                query = "SELECT name FROM " + VENDORS_TABLE + " WHERE vendorID = '" + userID + "' LIMIT 1;";
            } else {
                query = "SELECT name FROM " + USERS_TABLE + " WHERE userID = '" + userID + "' LIMIT 1;";
            }
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

    public String getUsersAddress(String userID) {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT address FROM " + USERS_TABLE + " WHERE userID = '" + userID + "' LIMIT 1";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                db.close();
                return null;
            }
            cursor.moveToNext();
            String address = cursor.getString(0);
            cursor.close();
            db.close();
            return address;
        }catch (SQLException e) {
            Log.e("SQLException", "getUsersAddress: " + e.getMessage());
            return null;
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

    public boolean updateCustomerRewards(String customerID, String discount, String points, boolean removeOldPoints) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String query;
            if (removeOldPoints) {
                query = "UPDATE " + CUSTOMER_TABLE + " SET points = '" + points + "', discounts = '" + discount + "' WHERE customerID = '" + customerID + "'";
            } else {
                query = "UPDATE " + CUSTOMER_TABLE + " SET points = (CAST(points AS INTEGER) + " + Integer.valueOf(points) + "), discounts = '" + discount + "' WHERE customerID = '" + customerID + "'";
            }
            Cursor cursor = db.rawQuery(query,null);
            cursor.moveToFirst();
            cursor.close();
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "updateCustomerRewards: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> getCustomerOrders(String customerID, boolean isPaid, boolean showBids, boolean isAccepted, boolean makePayment) {
        try
        {
            List<String[]> list = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            String query;

            if (makePayment) {
                query = "SELECT r.*, (SELECT name FROM " + VENDORS_TABLE + " WHERE vendorID = r.vendorID) as vendor FROM " + REQUESTS_TABLE +
                        " r WHERE customerID = '" + customerID + "' AND status LIKE 'Waiting for Payment'";
            } else {
                if (!showBids)
                {
                    query = "SELECT r.orderID, b.vendorID, r.customerID, r.service, r.description, r.time, r.date, r.other, b.bid, r.status, (SELECT name FROM " +
                            VENDORS_TABLE + " WHERE vendorID = b.vendorID) as vendor " +
                            "FROM " + REQUESTS_TABLE + " r LEFT JOIN " + VENDOR_BIDS_TABLE + " b ON r.orderID = b.orderID WHERE r.customerID = '" + customerID + "'" +
                            " AND (status LIKE 'Waiting for Bid' OR status LIKE 'Bids Placed')";
                } else
                {
                    query = "SELECT r.*, (SELECT name FROM " + VENDORS_TABLE + " WHERE vendorID = r.vendorID) as vendor FROM " + REQUESTS_TABLE + " r WHERE customerID = '" + customerID + "'";
                    if (isPaid)
                    {
                        query += " AND status LIKE 'Paid'";
                    } else if (isAccepted)
                    {
                        query += " AND (status LIKE 'Accepted' OR status LIKE 'In Progress' OR status LIKE 'Waiting for Payment')";
                    }
                }
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
            Log.e("SQLException", "getCustomerOrders: " + e.getMessage());
            return null;
        }
    }

    public boolean acceptCustomerRequestBid(String orderID, String vendorID, String customerID, String cost) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "UPDATE " + REQUESTS_TABLE + " SET status = 'Accepted', cost = '" + cost + "', vendorID = '" +
                    vendorID +  "' WHERE orderID = '" + orderID + "' AND customerID = '" + customerID + "';";
            Cursor cursor = db.rawQuery(query,null);
            cursor.moveToFirst();
            cursor.close();
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "acceptCustomerRequestBid: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> getRequestsForReview(String customerID, boolean showOldReviews) {
        try
        {
            List<String[]> list = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            String query;
            if (!showOldReviews) {
                query = "SELECT r.*, (SELECT name FROM " + VENDORS_TABLE + " WHERE vendorID = r.vendorID) as vendor FROM " +
                        REQUESTS_TABLE + " r LEFT JOIN " + CUSTOMER_REVIEWS_TABLE + " c ON r.orderID = c.orderID WHERE r.customerID = '" + customerID + "' AND c.orderID IS NULL AND r.status = 'Paid'";
            } else {
                query = "SELECT r.*, (SELECT name FROM " + VENDORS_TABLE + " WHERE vendorID = r.vendorID) as vendor FROM " + CUSTOMER_REVIEWS_TABLE + " r WHERE customerID = '" + customerID + "'";
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
            Log.e("SQLException", "getRequestsForReview: " + e.getMessage());
            return null;
        }
    }

    public boolean insertCustomerReview(String orderID, String vendorID, String customerID, String rating, String comment) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("orderID", orderID);
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

    public boolean removeVendorDate(String vendorID, String date) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(VENDOR_DATES_TABLE, "vendorID=? AND avail_date=?", new String[]{vendorID, date});
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "removeVendorDate: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<String> getVendorDates(String vendorID) {
        try
        {
            ArrayList<String> list = new ArrayList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT avail_date FROM " + VENDOR_DATES_TABLE + " WHERE vendorID = '" + vendorID + "'";
            Cursor cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()) {
                list.add(cursor.getString(0));
            }
            cursor.close();
            db.close();
            return list;
        }catch (SQLException e) {
            Log.e("SQLException", "getVendorDates: " + e.getMessage());
            return null;
        }
    }

    public List<String[]> getVendorRequests(String vendorID, boolean isVendorSpecific, String status) {
        try
        {
            List<String[]> list = new ArrayList<>();
            SQLiteDatabase db = this.getWritableDatabase();
            String query;
            if (isVendorSpecific) {
                query = "SELECT * FROM " + REQUESTS_TABLE + " WHERE vendorID = '" + vendorID + "' " + status;
            } else {
                query = "SELECT r.orderID, r.vendorID, r.customerID, r.service, r.description, r.time, r.date, r.other, " +
                        "(SELECT bid FROM " + VENDOR_BIDS_TABLE + " WHERE orderID = r.orderID AND vendorID = '" + vendorID + "') AS bid, r.status FROM " + REQUESTS_TABLE + " r";
                query += " LEFT JOIN " + VENDOR_SERVICES_TABLE + " s ON r.service = s.service WHERE s.vendorID = '" + vendorID + "' AND (status LIKE 'Waiting for Bid' OR status LIKE 'Bids Placed')";
            }

            Log.i("QUERY", "getVendorRequests: " + query);

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

    public boolean insertVendorBid(String orderID, String vendorID, String cost) {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String checkBidID = "SELECT * FROM " + VENDOR_BIDS_TABLE + " WHERE orderID = '" + orderID + "' AND vendorID = '" + vendorID +"' LIMIT 1";
            Cursor bidCursor = db.rawQuery(checkBidID, null);
            if (bidCursor.getCount() == 1) {
                //We can update instead of inserting
                String query = "UPDATE " + VENDOR_BIDS_TABLE + " SET bid = '" + cost + "' WHERE orderID = '" + orderID + "' AND vendorID = '" + vendorID + "'";
                bidCursor = db.rawQuery(query, null);
                bidCursor.moveToFirst();
            } else {
                //We need to insert a new one
                ContentValues values = new ContentValues();
                values.put("orderID", orderID);
                values.put("vendorID", vendorID);
                values.put("bid", cost);
                db.insert(VENDOR_BIDS_TABLE, null, values);
            }
            bidCursor.close();
            db.close();
            return updateRequestStatus(orderID, "Bids Placed");
        }catch (SQLException e) {
            Log.e("SQLException", "insertVendorBid: " + e.getMessage());
            return false;
        }
    }

    public boolean removeOldVendorBids(String orderID, String vendorID) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(VENDOR_BIDS_TABLE, "orderID='" + orderID + "' AND vendorID = '" + vendorID + "'", null);
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "removeOldVendorBids: " + e.getMessage());
            return false;
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

    public boolean updateRequestStatus(String orderID, String status) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "UPDATE " + REQUESTS_TABLE + " SET status = '" + status + "' WHERE orderID = '" + orderID + "'";
            Cursor cursor = db.rawQuery(query,null);
            cursor.moveToFirst();
            cursor.close();
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "updateRequestStatus: " + e.getMessage());
            return false;
        }
    }

    public boolean updateRequestDate(String orderID, String date) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "UPDATE " + REQUESTS_TABLE + " SET date = '" + date + "' WHERE orderID = '" + orderID + "'";
            Cursor cursor = db.rawQuery(query,null);
            cursor.moveToFirst();
            cursor.close();
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "updateRequestDate: " + e.getMessage());
            return false;
        }
    }

    public String getVendorsAddress(String vendorID) {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT address FROM " + VENDORS_TABLE + " WHERE vendorID = '" + vendorID + "' LIMIT 1";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                db.close();
                return null;
            }
            cursor.moveToNext();
            String address = cursor.getString(0);
            cursor.close();
            db.close();
            return address;
        }catch (SQLException e) {
            Log.e("SQLException", "getVendorsAddress: " + e.getMessage());
            return null;
        }
    }

    public boolean cancelCustomerRequest(String orderID) {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(VENDOR_BIDS_TABLE, "orderID=?", new String[]{orderID});
            db.delete(REQUESTS_TABLE, "orderID=?", new String[]{orderID} );
            db.close();
            return true;
        }catch (SQLException e) {
            Log.e("SQLException", "cancelCustomerRequest: " + e.getMessage());
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
            String query = "SELECT r.*, (SELECT name FROM " + USERS_TABLE + " WHERE userID = r.customerID) as custName FROM " + CUSTOMER_REVIEWS_TABLE + " r WHERE vendorID = '" + vendorID + "'";
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
            query.append("SELECT v.name, v.email, v.phone, v.address, s.service, s.rate, r.avg_rating, GROUP_CONCAT(d.avail_date, ', ') AS avail_dates FROM ").append(VENDORS_TABLE).append(" v");
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

            query.append(" GROUP BY v.name, v.email, v.phone, v.address, s.service, s.rate, r.avg_rating");

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
        insertUsers("user1001", "Passw1rd!", "John Doe", "johndoe@gmail.com", "8178888888", "123 S Wisteria St, Mansfield, TX, 76063");
        insertVendors("user1001", "Appliance Needs", "applianceneeds@gmail.com", "8178881234", "123 E Debbie Ln, Mansfield, TX, 76063", "Appliances", "25");

        insertUsers("user1002", "Passw1rd!", "Michael Adams", "michaeladams@gmail.com", "8177778888", "1600 Pennsylvania Ave, Fort Worth, TX, 76104");
        insertVendors("user1002", "Elite Appliances", "eliteappliances@gmail.com", "8177771234", "121 S Riverside Dr, Fort Worth, TX, 76104", "Appliances", "30");

        insertUsers("user1003", "Passw1rd!", "Sarah Johnson", "sarahjohnson@gmail.com", "8176667777", "777 Taylor St, Fort Worth, TX, 76102");
        insertVendors("user1003", "Quick Fix Appliances", "quickfixappliances@gmail.com", "8176661234", "500 Throckmorton St, Fort Worth, TX, 76102", "Appliances", "20");

        //Electrical vendors
        insertUsers("user1004", "Passw1rd!", "Jane Smith", "janesmith@gmail.com", "8177777777", "456 Oak St, Arlington, TX, 76001");
        insertVendors("user1004", "Power Solutions", "powersolutions@gmail.com", "8177771234", "123 W Main St, Arlington, TX, 76001", "Electrical", "30");

        insertUsers("user1005", "Passw1rd!", "Ryan Wilson", "ryanwilson@gmail.com", "8175556666", "321 N Central Expy, Richardson, TX, 75080");
        insertVendors("user1005", "Wired Solutions", "wiredsolutions@gmail.com", "8175551234", "1379 W Belt Line Rd, Richardson, TX, 75080", "Electrical", "35");

        insertUsers("user1006", "Passw1rd!", "Jessica Martinez", "jessicamartinez@gmail.com", "8174445555", "190 E Stacy Rd, Allen, TX, 75002");
        insertVendors("user1006", "Eco Electric", "ecoelectric@gmail.com", "8174441234", "2150 E Lamar Blvd, Arlington, TX, 76006", "Electrical", "25");

        //Plumbing vendors
        insertUsers("user1007", "Passw1rd!", "Mike Johnson", "mikejohnson@gmail.com", "8176666666", "9500 Ray White Rd, Fort Worth, TX, 76133");
        insertVendors("user1007", "Plumbing Experts", "plumbingexperts@gmail.com", "8176661234", "931 Foch St, Fort Worth, TX, 76107", "Plumbing", "40");

        insertUsers("user1008", "Passw1rd!", "Daniel Brown", "danielbrown@gmail.com", "8173334444", "4425 W Airport Fwy, Irving, TX, 75062");
        insertVendors("user1008", "Reliable Plumbing", "reliableplumbing@gmail.com", "8173331234", "501 E Corporate Dr, Lewisville, TX, 75057", "Plumbing", "45");

        insertUsers("user1009", "Passw1rd!", "Laura Taylor", "laurataylor@gmail.com", "8172223333", "1002 Avenue B, Garland, TX, 75040");
        insertVendors("user1009", "Aqua Plumbing", "aquaplumbing@gmail.com", "8172221234", "109 W Avenue B, Garland, TX, 75040", "Plumbing", "55");

        //Home cleaning vendors
        insertUsers("user1010", "Passw1rd!", "Emily Brown", "emilybrown@gmail.com", "8175555555", "12345 E Main St, Richardson, TX, 75081");
        insertVendors("user1010", "Sparkle Cleaners", "sparklecleaners@gmail.com", "8175551234", "1511 E Main St, Allen, TX, 75002", "Home Cleaning", "35");

        insertUsers("user1011", "Passw1rd!", "Mark Hernandez", "markhernandez@gmail.com", "8171112222", "1600 Pennsylvania Ave, Fort Worth, TX, 76104");
        insertVendors("user1011", "Fresh Start Cleaning", "freshstartcleaning@gmail.com", "8171111234", "111 S Main St, Grapevine, TX, 76051", "Home Cleaning", "40");

        insertUsers("user1012", "Passw1rd!", "Emma Clark", "emmaclark@gmail.com", "8179990000", "122 W Lamar St, McKinney, TX, 75069");
        insertVendors("user1012", "Spotless Cleaning", "spotlesscleaning@gmail.com", "8179991234", "206 N Kentucky St, McKinney, TX, 75069", "Home Cleaning", "50");

        //Tutoring vendors
        insertUsers("user1013", "Passw1rd!", "David Lee", "davidlee@gmail.com", "8174444444", "304 Preston Rd, Plano, TX, 75093");
        insertVendors("user1013", "LearnWell Tutoring", "learnwelltutoring@gmail.com", "8174441234", "2000 W Parker Rd, Plano, TX, 75023", "Tutoring", "50");

        insertUsers("user1014", "Passw1rd!", "Brian Scott", "brianscott@gmail.com", "8178889999", "123 S Wisteria St, Mansfield, TX, 76063");
        insertVendors("user1014", "Smart Learning", "smartlearning@gmail.com", "8178881234", "7001 US-287 Frontage Rd, Arlington, TX, 76001", "Tutoring", "60");

        insertUsers("user1015", "Passw1rd!", "Kimberly Nguyen", "kimberlynguyen@gmail.com", "8177778888", "456 Oak St, Arlington, TX, 76001");
        insertVendors("user1015", "Knowledge Builders", "knowledgebuilders@gmail.com", "8177771234", "2000 E Lamar Blvd, Arlington, TX, 76006", "Tutoring", "70");

        //Packaging and moving vendors
        insertUsers("user1016", "Passw1rd!", "Sarah Kim", "sarahkim@gmail.com", "8173333333", "4460 McEwen Rd, Dallas, TX, 75244");
        insertVendors("user1016", "Swift Movers", "swiftmovers@gmail.com", "8173331234", "2720 Royal Ln, Dallas, TX, 75229", "Packaging & Moving", "60");

        insertUsers("user1017", "Passw1rd!", "Christopher Lopez", "christopherlopez@gmail.com", "8176667777", "800 W Magnolia Ave, Fort Worth, TX, 76104");
        insertVendors("user1017", "Move Masters", "movemasters@gmail.com", "8176661234", "2201 N Collins St, Arlington, TX, 76011", "Packaging & Moving", "65");

        insertUsers("user1018", "Passw1rd!", "Amanda Hall", "amandahall@gmail.com", "8175556666", "2200 N Stemmons Fwy, Dallas, TX, 75207");
        insertVendors("user1018", "Speedy Movers", "speedymovers@gmail.com", "8175551234", "9850 N Central Expy, Dallas, TX, 75231", "Packaging & Moving", "55");

        //Computer Repaid vendors
        insertUsers("user1019", "Passw1rd!", "Kevin Garcia", "kevingarcia@gmail.com", "8172222222", "3046 Lavon Dr, Garland, TX, 75040");
        insertVendors("user1019", "PC Wizards", "pcwizards@gmail.com", "8172221234", "3415 W Buckingham Rd, Garland, TX, 75042", "Computer Repair", "45");

        insertUsers("user1020", "Passw1rd!", "Jason White", "jasonwhite@gmail.com", "8174445555", "4701 W Park Blvd, Plano, TX, 75093");
        insertVendors("user1020", "Geek Squad", "geeksquad@gmail.com", "8174441234", "2201 Preston Rd, Plano, TX, 75093", "Computer Repair", "50");

        insertUsers("user1021", "Passw1rd!", "Stephanie Garcia", "stephaniegarcia@gmail.com", "8173334444", "8701 Cypress Waters Blvd, Irving, TX, 75063");
        insertVendors("user1021", "Tech Geniuses", "techgeniuses@gmail.com", "8173331234", "975 W Bethel Rd, Coppell, TX, 75019", "Computer Repair", "45");

        //Home repair and painting vendors
        insertUsers("user1022", "Passw1rd!", "Rachel Martinez", "rachelmartinez@gmail.com", "8171111111", "1101 W Arapaho Rd, Richardson, TX, 75080");
        insertVendors("user1022", "FixIt Pros", "fixitpros@gmail.com", "8171111234", "2100 N Greenville Ave, Richardson, TX, 75080", "Home Repair & Painting", "55");

        insertUsers("user1023", "Passw1rd!", "Brandon Martinez", "brandonmartinez@gmail.com", "8172223333", "3939 S Garland Ave, Garland, TX, 75041");
        insertVendors("user1023", "Handy Hands", "handyhands@gmail.com", "8172221234", "635 N Garland Ave, Garland, TX, 75040", "Home Repair & Painting", "60");

        insertUsers("user1024", "Passw1rd!", "Nicole Thompson", "nicolethompson@gmail.com", "8171112222", "101 S Coit Rd, Richardson, TX, 75080");
        insertVendors("user1024", "Master Craftsmen", "mastercraftsmen@gmail.com", "8171111234", "2100 N Collins Blvd, Richardson, TX, 75080", "Home Repair & Painting", "70");

        //Pest control vendors
        insertUsers("user1025", "Passw1rd!", "Chris Thompson", "christhompson@gmail.com", "8179999999", "4550 Eldorado Pkwy, McKinney, TX, 75070");
        insertVendors("user1025", "Bug Busters", "bugbusters@gmail.com", "8179991234", "2001 Central Cir, McKinney, TX, 75070", "Pest Control", "70");

        insertUsers("user1026", "Passw1rd!", "Adam Wilson", "adamwilson@gmail.com", "8179990000", "4991 W University Dr, McKinney, TX, 75071");
        insertVendors("user1026", "Critter Control", "crittercontrol@gmail.com", "8179991234", "6500 W Virginia Pkwy, McKinney, TX, 75071", "Pest Control", "75");

        insertUsers("user1027", "Passw1rd!", "Taylor Harris", "taylorharris@gmail.com", "8178889999", "123 S Wisteria St, Mansfield, TX, 76063");
        insertVendors("user1027", "Bug Off", "bugoff@gmail.com", "8178881234", "1532 E Debbie Ln, Mansfield, TX, 76063", "Pest Control", "65");
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
        insertUsers("user1028", "Passw1rd!", "Alice Smith", "alice.smith@example.com", "8171234567", "456 W Main St, Arlington, TX, 76010");
        insertCustomers("user1028", "200", ".05");

        insertUsers("user1029", "Passw1rd!", "Bob Johnson", "bob.johnson@example.com", "8172345678", "789 Elm St, Fort Worth, TX, 76102");
        insertCustomers("user1029", "0", "0");

        insertUsers("user1030", "Passw1rd!", "Emily Davis", "emily.davis@example.com", "8173456789", "101 W Oak St, Mansfield, TX, 76063");
        insertCustomers("user1030", "0", "0");

        insertUsers("user1031", "Passw1rd!", "David Brown", "david.brown@example.com", "8174567890", "202 S Pine St, Grapevine, TX, 76051");
        insertCustomers("user1031", "0", "0");

        insertUsers("user1032", "Passw1rd!", "Sarah Wilson", "sarah.wilson@example.com", "8175678901", "303 Maple St, Irving, TX, 75038");
        insertCustomers("user1032", "0", "0");

        insertUsers("user1033", "Passw1rd!", "Michael Martinez", "michael.martinez@example.com", "8176789012", "404 Cedar Glen St, Keller, TX, 76248");
        insertCustomers("user1033", "0", "0");

        insertUsers("user1034", "Passw1rd!", "Jessica Lee", "jessica.lee@example.com", "8177890123", "505 Walnut Dr, Southlake, TX, 76092");
        insertCustomers("user1034", "0", "0");

        insertUsers("user1035", "Passw1rd!", "Daniel Harris", "daniel.harris@example.com", "8178901234", "606 Cherry Lane, Colleyville, TX, 76034");
        insertCustomers("user1035", "0", "0");

        insertUsers("user1036", "Passw1rd!", "Laura Clark", "laura.clark@example.com", "8179012345", "Pine St, Fort Worth, TX, 76102");
        insertCustomers("user1036", "0", "0");

        insertUsers("user1037", "Passw1rd!", "Ryan Nguyen", "ryan.nguyen@example.com", "8170123456", "808 Oak St, Bedford, TX, 76021");
        insertCustomers("user1037", "0", "0");
    }

    private void insertTestRequests() {
        insertRequests("user1028", "Appliances", "I need my microwave installed.", "10:00 AM", "5/27/2024", "", null, "Waiting for Bid");
        acceptCustomerRequestBid("1", "user1001", "user1028", "200");
        updateRequestStatus("1", "Paid");

        insertRequests("user1029", "Electrical", "I need an outlet fixed.", "11:00 AM", "5/26/2024", "", null, "Waiting for Bid");
        acceptCustomerRequestBid("2", "user1004", "user1029", "75");
        updateRequestStatus("2", "Paid");

        insertRequests("user1030", "Plumbing", "I need my toilet fixed.", "9:00 AM", "5/25/2024", "", null, "Waiting for Bid");
        acceptCustomerRequestBid("3", "user1007", "user1030", "150");
        updateRequestStatus("3", "Paid");

        insertRequests("user1031", "Home Cleaning", "I need my bedroom cleaned.", "8:00 AM", "5/24/2024", "", null, "Waiting for Bid");
        acceptCustomerRequestBid("4", "user1010", "user1031", "300");
        updateRequestStatus("4", "Paid");

        insertRequests("user1032", "Tutoring", "I need help with math.", "12:00 PM", "5/23/2024", "", null, "Waiting for Bid");
        acceptCustomerRequestBid("5", "user1013", "user1032", "45");
        updateRequestStatus("5", "Paid");

        insertRequests("user1033", "Packaging & Moving", "I need to move houses.", "1:00 PM", "5/22/2024", "", null, "Waiting for Bid");
        acceptCustomerRequestBid("6", "user1016", "user1033", "400");
        updateRequestStatus("6", "Paid");

        insertRequests("user1034", "Computer Repair", "I need my computer fixed.", "2:00 PM", "5/21/2024", "", null, "Waiting for Bid");
        acceptCustomerRequestBid("7", "user1019", "user1034", "175");
        updateRequestStatus("7", "Paid");

        insertRequests("user1035", "Home Repair & Painting", "I need my walls painted.", "3:00 PM", "5/20/2024", "", null, "Waiting for Bid");
        acceptCustomerRequestBid("8", "user1022", "user1035", "100");
        updateRequestStatus("8", "Paid");

        insertRequests("user1036", "Pest Control", "I need bugs in my shed gone.", "4:00 PM", "5/19/2024", "", null, "Waiting for Bid");
        acceptCustomerRequestBid("9", "user1025", "user1036", "125");
        updateRequestStatus("9", "Paid");

        insertRequests("user1037", "Appliances", "I need my washer and dryer installed.", "5:00 PM", "5/18/2024", "", null, "Waiting for Bid");
        acceptCustomerRequestBid("10", "user1002", "user1037", "350");
        updateRequestStatus("10", "Paid");
    }

    private void insertTestReviews() {
        insertCustomerReview("1","user1001", "user1028", "5", "Great Job!");

        insertCustomerReview("2","user1004", "user1029", "2", "Poor Job");

        insertCustomerReview("3","user1007", "user1030", "3", "Okay Job");

        insertCustomerReview("4","user1010", "user1031", "3.5", "Alright Job");

        insertCustomerReview("5","user1013", "user1032", "5", "Great Job!");

        insertCustomerReview("6","user1016", "user1033", "4", "Good Job");

        insertCustomerReview("7","user1019", "user1034", "3", "Okay Job");

        insertCustomerReview("8","user1022", "user1035", "4.5", "Nice Job!");

        insertCustomerReview("9","user1025", "user1036", "4", "Good Job");

        insertCustomerReview("10","user1002", "user1028", "5", "Great Job!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onCreate(db);
    }
}
