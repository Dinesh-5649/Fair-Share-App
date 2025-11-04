package com.example.demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyDatabase extends SQLiteOpenHelper {

    Context context;
    public static final String DATABASE_NAME = "my_database.db";
    public static final int DATABASE_VERSION = 9; //

    // TABLE: Users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // TABLE: Groups
    public static final String TABLE_GROUPS = "group_table";
    public static final String COLUMN_GROUP_ID = "group_id";
    public static final String COLUMN_GROUP_NAME = "group_name";

    // TABLE: Members
    public static final String TABLE_MEMBERS = "members";
    public static final String COLUMN_MEMBER_ID = "member_id";
    public static final String COLUMN_MEMBER_NAME = "member_name";
    public static final String COLUMN_GROUP_REF_ID_FOR_MEMBERS = "group_ref_id"; // foreign key

    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_MESSAGE_ID = "message_id";
    public static final String COLUMN_GROUP_REF_ID_FOR_MESSAGE = "group_id";
    public static final String COLUMN_SENDER_ID = "sender_id";
    public static final String COLUMN_MESSAGE_TEXT = "message_text";
    public static final String COLUMN_TIMESTAMP = "timestamp";


    public MyDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL);";

        // Create Groups Table
        String createGroupsTable = "CREATE TABLE " + TABLE_GROUPS + " (" +
                COLUMN_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GROUP_NAME + " TEXT UNIQUE NOT NULL);";

        // Create Members Table
        String createMembersTable = "CREATE TABLE " + TABLE_MEMBERS + " (" +
                COLUMN_MEMBER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GROUP_REF_ID_FOR_MEMBERS + " INTEGER, " +
                COLUMN_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_GROUP_REF_ID_FOR_MEMBERS + ") REFERENCES " + TABLE_GROUPS + "(" + COLUMN_GROUP_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE);";

        // Create Message Table
        String createMessagesTable = "CREATE TABLE " + TABLE_MESSAGES + " (" +
                COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GROUP_REF_ID_FOR_MESSAGE + " INTEGER, " +
                COLUMN_SENDER_ID + " INTEGER, " +
                COLUMN_MESSAGE_TEXT + " TEXT NOT NULL, " +
                COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COLUMN_GROUP_REF_ID_FOR_MESSAGE + ") REFERENCES " + TABLE_GROUPS + "(" + COLUMN_GROUP_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + COLUMN_SENDER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE);";


        db.execSQL(createUsersTable);
        db.execSQL(createGroupsTable);
        db.execSQL(createMembersTable);
        db.execSQL(createMessagesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    // ============================
    //  USERS - LOGIN / SIGNUP
    // ============================

    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERNAME, username);
        cv.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(context, "User registered successfully", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        boolean loginSuccess = cursor.getCount() > 0;
        cursor.close();
        db.close();

        if (loginSuccess) {
            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }

        return loginSuccess;
    }
    // ============================
    //  Create group and automatically adds the user who created it
    // ============================
    public long addGroup(String groupName, String creatorUsername) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check for the duplicate group name
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_GROUPS + " WHERE " + COLUMN_GROUP_NAME + "=?",
                new String[]{groupName}
        );
        if (cursor.getCount() > 0) {
            Toast.makeText(context, "Group name already exists!", Toast.LENGTH_SHORT).show();
            cursor.close();
            return -1;
        }
        cursor.close();
        // Create group
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_GROUP_NAME, groupName);

        long groupId = db.insert(TABLE_GROUPS, null, cv);

        if (groupId == -1) {
            Toast.makeText(context, "Failed to add group", Toast.LENGTH_SHORT).show();
            return -1;
        } else {
            Toast.makeText(context, "Group created successfully", Toast.LENGTH_SHORT).show();

            //  Add creator as member of this group
            Integer userId = getUserIdByUsername(creatorUsername);
            if (userId != null) {
                ContentValues memberCv = new ContentValues();
                memberCv.put(COLUMN_GROUP_REF_ID_FOR_MEMBERS, groupId);
                memberCv.put(COLUMN_USER_ID, userId);
                db.insert(TABLE_MEMBERS, null, memberCv);
            }
            return groupId;
        }
    }

    // ============================
    //  MEMBERS
    // ============================

    public void addMember(String username, int groupId) {
        SQLiteDatabase db = this.getWritableDatabase();


          // Get user ID from username
          Integer userId = getUserIdByUsername(username);
          if (userId == null) {
              Toast.makeText(context, "User not found. Please register first.", Toast.LENGTH_SHORT).show();
              return;
          }

          // Check if already a member
          String checkQuery = "SELECT * FROM " + TABLE_MEMBERS +
                  " WHERE " + COLUMN_GROUP_REF_ID_FOR_MEMBERS + "=? AND " + COLUMN_USER_ID + "=?";
          Cursor cursor = db.rawQuery(checkQuery, new String[]{String.valueOf(groupId), String.valueOf(userId)});

          if (cursor.getCount() > 0) {
              cursor.close();
              Toast.makeText(context, "User is already a member of this group.", Toast.LENGTH_SHORT).show();
              return;
          }

          cursor.close();

          // Add user to group
          ContentValues cv = new ContentValues();
          cv.put(COLUMN_GROUP_REF_ID_FOR_MEMBERS, groupId);
          cv.put(COLUMN_USER_ID, userId);

          long result = db.insert(TABLE_MEMBERS, null, cv);
          if (result == -1)
              Toast.makeText(context, "Failed to add member", Toast.LENGTH_SHORT).show();
          else
              Toast.makeText(context, "Member added successfully", Toast.LENGTH_SHORT).show();

    }

    //  Get all the Group Members

    public ArrayList<String> getMembersByGroup(int groupId) {
        ArrayList<String> members = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT u." + COLUMN_USERNAME +
                " FROM " + TABLE_MEMBERS + " m" +
                " JOIN " + TABLE_USERS + " u ON m." + COLUMN_USER_ID + " = u." + COLUMN_USER_ID +
                " WHERE m." + COLUMN_GROUP_REF_ID_FOR_MEMBERS + "=?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(groupId)});

        if (cursor.moveToFirst()) {
            do {
                members.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return members;
    }


    //Show all users

    // Get all the group name
    public ArrayList<String> getAllUserNames() {
        ArrayList<String> usersList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USERS;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                usersList.add(username);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return usersList;
    }



    //  Helper method to find UserID by UserName    //

    public Integer getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?",
                new String[]{username}
        );

        Integer userId = null;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
        }

        cursor.close();

        return userId;
    }
    // get the group names in which the user is present
    public ArrayList<String> getGroupsForUser(String username) {
        ArrayList<String> groupList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Integer userId = getUserIdByUsername(username);
        if (userId == null) {
            Toast.makeText(context, "User not found!", Toast.LENGTH_SHORT).show();
            return groupList;
        }

        String query = "SELECT g." + COLUMN_GROUP_NAME +
                " FROM " + TABLE_GROUPS + " g" +
                " JOIN " + TABLE_MEMBERS + " m ON g." + COLUMN_GROUP_ID + " = m." + COLUMN_GROUP_REF_ID_FOR_MEMBERS +
                " WHERE m." + COLUMN_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                String groupName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_NAME));
                groupList.add(groupName);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return groupList;
    }

    // Get the group Id by Group name
    public int getGroupIdByGroupName(String groupName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_GROUP_ID + " FROM " + TABLE_GROUPS + " WHERE " + COLUMN_GROUP_NAME + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{groupName});
        int groupId = 0;
        if (cursor.moveToFirst()) {
            groupId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GROUP_ID));
        }
        db.close();
        cursor.close();
        return groupId;

    }
    /// ////////////////////////
    // Send a Message

    public void addMessage(int groupId, int senderId, String messageText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_REF_ID_FOR_MESSAGE, groupId);
        values.put(COLUMN_SENDER_ID, senderId);
        values.put(COLUMN_MESSAGE_TEXT, messageText);

        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }

    /// ///  ///
    // Show all group messages
    public ArrayList<String> getMessagesByGroup(int groupId) {
        ArrayList<String> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT u." + COLUMN_USERNAME + ", m." + COLUMN_MESSAGE_TEXT +
                " FROM " + TABLE_MESSAGES + " m " +
                " JOIN " + TABLE_USERS + " u ON m." + COLUMN_SENDER_ID + " = u." + COLUMN_USER_ID +
                " WHERE m." + COLUMN_GROUP_REF_ID_FOR_MESSAGE + " = ?" +
                " ORDER BY m." + COLUMN_TIMESTAMP + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(groupId)});

        if (cursor.moveToFirst()) {
            do {
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                String text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_TEXT));
                messages.add(sender + ": " + text);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return messages;
    }




}
