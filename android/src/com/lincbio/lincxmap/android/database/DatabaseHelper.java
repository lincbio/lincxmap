package com.lincbio.lincxmap.android.database;

import java.util.ArrayList;
import java.util.List;

import com.lincbio.lincxmap.LincXmapException;
import com.lincbio.lincxmap.Version;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.pojo.Catalogue;
import com.lincbio.lincxmap.pojo.Product;
import com.lincbio.lincxmap.pojo.Template;
import com.lincbio.lincxmap.pojo.TemplateItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper implements Constants {

	public DatabaseHelper(Context context) {
		super(context, "lincxmap-" + Version.major + ".db", null, Version.major);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		createTables(db);
	}

	public void execute(String sql, String... args) {
		this.getWritableDatabase().execSQL(sql, args);
	}

	public void execute(SQLiteDatabase db, String sql, String... args) {
		db.execSQL(sql, args);
	}

	public void execute(Transaction trans) throws LincXmapException {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();

		try {
			trans.run(db);
			db.setTransactionSuccessful();
		} catch (Throwable t) {
			throw new LincXmapException(t);
		} finally {
			db.endTransaction();
		}
	}

	public Cursor select(String table, String[] columns, String selection,
			String[] args, String groupBy, String having, String orderBy) {
		return getReadableDatabase().query(table, columns, selection, args,
				groupBy, having, orderBy);
	}

	public Product getProduct(int productId) {
		Cursor c = null;
		String sql = TABLE_COL_ID + "=?";
		String[] args = { String.valueOf(productId) };

		try {
			c = getReadableDatabase().query(TABLE_PRODUCT, TABLE_PRODUCT_COLS,
					sql, args, null, null, null);

			if (c.moveToNext()) {
				return new Product(c.getInt(0), c.getInt(1), c.getString(2));
			}
		} finally {
			close(c);
		}

		return null;

	}

	public TemplateItem getTemplateItem(int itemId) {
		Cursor c = null;
		String sql = TABLE_COL_ID + "=?";
		String[] args = { String.valueOf(itemId) };

		try {
			c = getReadableDatabase().query(TABLE_TEMPLATE_ITEM,
					TABLE_TEMPLATE_ITEM_COLS, sql, args, null, null, null);

			if (c.moveToNext()) {
				return new TemplateItem(c.getInt(0), c.getInt(1), c.getInt(2),
						c.getInt(3), c.getInt(4));
			}
		} finally {
			close(c);
		}

		return null;
	}

	public List<Catalogue> getCatalogues() throws LincXmapException {
		Cursor c = null;
		List<Catalogue> list = new ArrayList<Catalogue>();

		try {
			c = getReadableDatabase().query(TABLE_CATALOGUE,
					TABLE_CATALOGUE_COLS, null, null, null, null, null);

			while (c.moveToNext()) {
				list.add(new Catalogue(c.getInt(0), c.getString(1)));
			}
		} finally {
			close(c);
		}

		return list;
	}
	
	public List<TemplateItem> getTemplateItems(int templateId) {
		Cursor c = null;
		String sql = TABLE_COL_TEMPLATE_ID + "=?";
		String[] args = { String.valueOf(templateId) };
		List<TemplateItem> list = new ArrayList<TemplateItem>();

		try {
			c = getReadableDatabase().query(TABLE_TEMPLATE_ITEM,
					TABLE_TEMPLATE_ITEM_COLS, sql, args, null, null, null);

			while (c.moveToNext()) {
				list.add(new TemplateItem(c.getInt(0), c.getInt(1), c.getInt(2),
						c.getInt(3), c.getInt(4)));
			}
		} finally {
			close(c);
		}

		return list;
	}

	public List<Product> getProducts() {
		Cursor c = null;
		List<Product> list = new ArrayList<Product>();

		try {
			c = getReadableDatabase().query(TABLE_PRODUCT, TABLE_PRODUCT_COLS,
					null, null, null, null, null);

			while (c.moveToNext()) {
				list.add(new Product(c.getInt(0), c.getInt(1), c.getString(2)));
			}
		} finally {
			close(c);
		}

		return list;
	}

	public List<Template> getTemplates() throws LincXmapException {
		Cursor c = null;
		List<Template> list = new ArrayList<Template>();

		try {
			c = getReadableDatabase().query(TABLE_TEMPLATE,
					TABLE_TEMPLATE_COLS, null, null, null, null, null);

			while (c.moveToNext()) {
				Template t = new Template(c.getInt(0), c.getString(1),
						c.getInt(2), c.getInt(3));
				t.getItems().addAll(getTemplateItems(t.getId()));
				list.add(t);
			}
		} finally {
			close(c);
		}

		return list;
	}

	public int newTemplate(Template template) throws LincXmapException {
		Cursor c = null;

		try {
			c = getReadableDatabase().query(TABLE_TEMPLATE,
					TABLE_TEMPLATE_COLS, TABLE_COL_NAME + "=?",
					new String[] { String.valueOf(template.getName()) }, null,
					null, null);

			if (c.moveToNext())
				throw new LincXmapException("Template `" + template.getName()
						+ "' already exists");
		} finally {
			close(c);
		}

		ContentValues values = new ContentValues();
		values.putNull(TABLE_COL_ID);
		values.put(TABLE_COL_NAME, template.getName());
		values.put(TABLE_COL_ROWS, template.getRowCount());
		values.put(TABLE_COL_COLS, template.getColumnCount());
		return (int) getWritableDatabase().insert(TABLE_TEMPLATE, null, values);
	}

	public int updateTempldate(Template template) {
		Cursor c = null;

		try {
			c = getReadableDatabase().query(
					TABLE_TEMPLATE,
					TABLE_TEMPLATE_COLS,
					TABLE_COL_NAME + "=? and " + TABLE_COL_ID + " <> ?",
					new String[] { template.getName(),
							String.valueOf(template.getId()) }, null, null,
					null);

			if (c.moveToNext())
				throw new LincXmapException("Template `" + template.getName()
						+ "' already exists");
		} finally {
			close(c);
		}

		ContentValues values = new ContentValues();
		values.put(TABLE_COL_NAME, template.getName());
		values.put(TABLE_COL_ROWS, template.getRowCount());
		values.put(TABLE_COL_COLS, template.getColumnCount());

		return getWritableDatabase().update(TABLE_TEMPLATE, values,
				TABLE_COL_ID + "=?",
				new String[] { String.valueOf(template.getId()) });
	}

	public void deleteTemplate(final int id) throws LincXmapException {
		try {
			execute(new Transaction() {

				@Override
				public void run(SQLiteDatabase db) throws Exception {
					String[] args = { String.valueOf(id) };

					db.delete(TABLE_TEMPLATE, TABLE_COL_ID + "=?", args);
					db.delete(TABLE_TEMPLATE_ITEM,
							TABLE_COL_TEMPLATE_ID + "=?", args);
				}
			});
		} catch (Throwable t) {
			throw new LincXmapException(t);
		}
	}

	protected static void createTables(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CATALOGUE + "("
				+ TABLE_COL_ID
				+ " integer primary key autoincrement not null, "
				+ TABLE_COL_NAME + " text not null)");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCT + "("
				+ TABLE_COL_ID
				+ " integer primary key autoincrement not null, "
				+ TABLE_COL_CATALOGUE_ID + " integer not null, "
				+ TABLE_COL_NAME + " text not null)");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TEMPLATE + "("
				+ TABLE_COL_ID
				+ " integer primary key autoincrement not null, "
				+ TABLE_COL_NAME + " text not null, " + TABLE_COL_ROWS
				+ " integer not null, " + TABLE_COL_COLS + " integer not null)");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TEMPLATE_ITEM + "("
				+ TABLE_COL_ID
				+ " integer primary key autoincrement not null, "
				+ TABLE_COL_TEMPLATE_ID + " integer not null, "
				+ TABLE_COL_PRODUCT_ID + " integer not null, " + TABLE_COL_X
				+ " integer not null, " + TABLE_COL_Y + " integer not null)");
	}

	protected static void dropTables(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATALOGUE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMPLATE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMPLATE_ITEM);
	}

	public static void close(Cursor c) {
		if (null != c && !c.isClosed()) {
			c.close();
		}
	}

}
