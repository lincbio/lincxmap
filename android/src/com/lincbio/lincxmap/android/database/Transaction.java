package com.lincbio.lincxmap.android.database;

import android.database.sqlite.SQLiteDatabase;

public interface Transaction {

	public void run(SQLiteDatabase db) throws Exception;
	
}
