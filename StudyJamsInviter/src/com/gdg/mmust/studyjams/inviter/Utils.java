package com.gdg.mmust.studyjams.inviter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Utils {
	Context context;

	public Utils(Context context) {
		this.context = context;
	}
	
	public void insertMember(ContentValues values){
		Database db = new Database(context);
		SQLiteDatabase sql = db.getWritableDatabase();
		sql.insert("members_table", null, values);
		sql.close();
		db.close();
	}

	public Cursor getCursor() {
		// TODO Auto-generated method stub
		Database db = new Database(context);
		SQLiteDatabase sql = db.getWritableDatabase();
		return sql.query("members_table", null, null, null, null, null, null);
	}
}
