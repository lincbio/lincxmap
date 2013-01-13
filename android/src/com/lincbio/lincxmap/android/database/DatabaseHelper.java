package com.lincbio.lincxmap.android.database;

import java.util.ArrayList;
import java.util.List;

import com.lincbio.lincxmap.LincXmapException;
import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.Version;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.pojo.Catalog;
import com.lincbio.lincxmap.pojo.History;
import com.lincbio.lincxmap.pojo.Product;
import com.lincbio.lincxmap.pojo.Profile;
import com.lincbio.lincxmap.pojo.Result;
import com.lincbio.lincxmap.pojo.Sample;
import com.lincbio.lincxmap.pojo.Template;
import com.lincbio.lincxmap.pojo.TemplateItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper implements Constants {
	private final Context context;

	public DatabaseHelper(Context context) {
		super(context, "lincxmap-" + Version.major + ".db", null, Version.major);
		this.context = context;
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
		SQLiteDatabase db = getWritableDatabase();

		try {
			db.execSQL(sql, args);
		} finally {
			close(db);
		}
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
			close(db);
		}
	}

	public Cursor select(String table, String[] columns, String selection,
			String[] args, String groupBy, String having, String orderBy) {
		SQLiteDatabase db = getReadableDatabase();

		try {
			return db.query(table, columns, selection, args, groupBy, having,
					orderBy);
		} finally {
			close(db);
		}
	}

	public Catalog getCatalogue(long catalogueId) {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		String sql = TABLE_COL_ID + "=?";
		String[] args = { String.valueOf(catalogueId) };

		try {
			c = db.query(TABLE_CATALOG, TABLE_CATALOG_COLS, sql, args, null,
					null, null);

			if (c.moveToNext()) {
				return new Catalog(c.getLong(0), c.getString(1));
			}
		} finally {
			close(c);
			close(db);
		}

		return null;
	}

	public Product getProduct(long productId) {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		String sql = TABLE_COL_ID + "=?";
		String[] args = { String.valueOf(productId) };

		try {
			c = db.query(TABLE_PRODUCT, TABLE_PRODUCT_COLS, sql, args, null,
					null, null);

			if (c.moveToNext()) {
				return new Product(c.getLong(0), c.getLong(1), c.getString(2));
			}
		} finally {
			close(c);
			close(db);
		}

		return null;
	}

	public TemplateItem getTemplateItem(long itemId) {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		String sql = TABLE_COL_ID + "=?";
		String[] args = { String.valueOf(itemId) };

		try {
			c = db.query(TABLE_TEMPLATE_ITEM, TABLE_TEMPLATE_ITEM_COLS, sql,
					args, null, null, null);

			if (c.moveToNext()) {
				return new TemplateItem(c.getLong(0), c.getLong(1),
						c.getLong(2), c.getInt(3), c.getInt(4));
			}
		} finally {
			close(c);
			close(db);
		}

		return null;
	}

	public List<Catalog> getCatalogs() throws LincXmapException {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		List<Catalog> list = new ArrayList<Catalog>();

		try {
			c = db.query(TABLE_CATALOG, TABLE_CATALOG_COLS, null, null, null,
					null, null);

			while (c.moveToNext()) {
				list.add(new Catalog(c.getLong(0), c.getString(1)));
			}
		} finally {
			close(c);
			close(db);
		}

		return list;
	}

	public History getHistory(long historyId) {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		String sql = TABLE_COL_ID + "=?";
		String[] args = { String.valueOf(historyId) };

		try {
			c = db.query(TABLE_HISTORY, TABLE_HISTORY_COLS, sql, args, null,
					null, null);

			if (c.moveToNext()) {
				return new History(c.getLong(0), c.getLong(1), c.getString(2),
						c.getString(3), c.getString(4));
			}
		} finally {
			close(c);
			close(db);
		}

		return null;
	}

	public List<History> getHistories() {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		List<History> list = new ArrayList<History>();

		try {
			c = db.query(TABLE_HISTORY, TABLE_HISTORY_COLS, null, null, null,
					null, TABLE_COL_ID + " desc");

			while (c.moveToNext()) {
				list.add(new History(c.getLong(0), c.getLong(1),
						c.getString(2), c.getString(3), c.getString(4)));
			}
		} finally {
			close(c);
			close(db);
		}

		return list;
	}

	public List<History> getHistories(Profile profile) {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		List<History> list = new ArrayList<History>();
		String sql = TABLE_COL_PROFILE_ID + "=?";
		String[] args = new String[] { String.valueOf(profile.getId()) };

		try {
			c = db.query(TABLE_HISTORY, TABLE_HISTORY_COLS, sql, args, null,
					null, TABLE_COL_ID + " desc");

			while (c.moveToNext()) {
				list.add(new History(c.getLong(0), c.getLong(1),
						c.getString(2), c.getString(3), c.getString(4)));
			}
		} finally {
			close(c);
			close(db);
		}

		return list;
	}

	public Profile getProfile(long profileId) {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		String sql = TABLE_COL_ID + "=?";
		String[] args = { String.valueOf(profileId) };

		try {
			c = db.query(TABLE_PROFILE, TABLE_PROFILE_COLS, sql, args, null,
					null, null);

			if (c.moveToNext()) {
				return new Profile(c.getLong(0), c.getString(1), c.getString(2));
			}
		} finally {
			close(c);
			close(db);
		}

		return null;
	}

	public List<Profile> getProfiles() {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		List<Profile> list = new ArrayList<Profile>();

		try {
			c = db.query(TABLE_PROFILE, TABLE_PROFILE_COLS, null, null, null,
					null, null);

			while (c.moveToNext()) {
				list.add(new Profile(c.getLong(0), c.getString(1), c
						.getString(2)));
			}
		} finally {
			close(c);
			close(db);
		}

		return list;
	}

	public List<Profile> getProfiles(String key) {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		List<Profile> list = new ArrayList<Profile>();
		String sql = TABLE_COL_NAME + " like ? or " + TABLE_COL_SERIAL_NUMBER
				+ " like ?";

		try {
			if (!key.startsWith("%"))
				key = "%" + key;

			if (!key.endsWith("%"))
				key = key + "%";

			c = db.query(TABLE_PROFILE, TABLE_PROFILE_COLS, sql, new String[] {
					key, key }, null, null, null);

			while (c.moveToNext()) {
				list.add(new Profile(c.getLong(0), c.getString(1), c
						.getString(2)));
			}
		} finally {
			close(c);
			close(db);
		}

		return list;
	}

	public List<TemplateItem> getTemplateItems(long templateId) {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		String sql = TABLE_COL_TEMPLATE_ID + "=?";
		String[] args = { String.valueOf(templateId) };
		List<TemplateItem> list = new ArrayList<TemplateItem>();

		try {
			c = db.query(TABLE_TEMPLATE_ITEM, TABLE_TEMPLATE_ITEM_COLS, sql,
					args, null, null, null);

			while (c.moveToNext()) {
				list.add(new TemplateItem(c.getLong(0), c.getLong(1), c
						.getLong(2), c.getInt(3), c.getInt(4)));
			}
		} finally {
			close(c);
			close(db);
		}

		return list;
	}

	public List<Product> getProducts() {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		List<Product> list = new ArrayList<Product>();

		try {
			c = db.query(TABLE_PRODUCT, TABLE_PRODUCT_COLS, null, null, null,
					null, null);

			while (c.moveToNext()) {
				list.add(new Product(c.getLong(0), c.getLong(1), c.getString(2)));
			}
		} finally {
			close(c);
			close(db);
		}

		return list;
	}

	public List<Result> getResults(long historyId) {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		String sql = TABLE_COL_HISTORY_ID + "=?";
		String[] args = { String.valueOf(historyId) };
		List<Result> list = new ArrayList<Result>();

		try {
			c = db.query(TABLE_RESULT, TABLE_RESULT_COLS, sql, args, null,
					null, null);

			while (c.moveToNext()) {
				list.add(new Result(c.getLong(0), c.getLong(1), c.getString(2),
						c.getDouble(3), c.getDouble(4), c.getDouble(5), c
								.getDouble(6), c.getString(7)));
			}
		} finally {
			close(c);
			close(db);
		}

		return list;
	}

	public List<Template> getTemplates() throws LincXmapException {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();
		List<Template> list = new ArrayList<Template>();

		try {
			c = db.query(TABLE_TEMPLATE, TABLE_TEMPLATE_COLS, null, null, null,
					null, null);

			while (c.moveToNext()) {
				Template t = new Template(c.getLong(0), c.getString(1),
						c.getInt(2), c.getInt(3));
				t.getItems().addAll(getTemplateItems(t.getId()));
				list.add(t);
			}
		} finally {
			close(c);
			close(db);
		}

		return list;
	}

	public void addHistory(final History history, final List<Sample> samples) {
		final int n = samples.size();
		final List<Result> results = new ArrayList<Result>(n);

		for (int i = 0; i < n; i++) {
			Sample sample = samples.get(i);
			Result result = new Result(0, sample.getName(),
					sample.getBrightness(), sample.getConcentration());
			result.setMinValue(0);
			result.setMaxValue(1);
			result.setFlag(this.context.getString(R.string.normal));
			results.add(result);
		}

		this.execute(new Transaction() {

			@Override
			public void run(SQLiteDatabase db) throws Exception {
				long historyId;
				ContentValues values;

				values = new ContentValues();
				values.putNull(TABLE_COL_ID);
				values.put(TABLE_COL_PROFILE_ID, history.getProfileId());
				values.put(TABLE_COL_OWNER, history.getOwner());
				values.put(TABLE_COL_LABEL, history.getLabel());
				values.put(TABLE_COL_TIME, history.getTime());
				historyId = db.insert(TABLE_HISTORY, null, values);
				history.setId(historyId);

				for (Result result : results) {
					values = new ContentValues();
					values.putNull(TABLE_COL_ID);
					values.put(TABLE_COL_HISTORY_ID, historyId);
					values.put(TABLE_COL_NAME, result.getSampleName());
					values.put(TABLE_COL_BRIGHTNESS, result.getBrightness());
					values.put(TABLE_COL_CONCENTRATION,
							result.getConcentration());
					values.put(TABLE_COL_MIN, result.getMinValue());
					values.put(TABLE_COL_MAX, result.getMaxValue());
					values.put(TABLE_COL_FLAG, result.getFlag());
					db.insert(TABLE_RESULT, null, values);
				}
			}

		});
	}

	public long addProfile(Profile profile) {
		SQLiteDatabase db = getWritableDatabase();

		try {
			ContentValues values = new ContentValues();
			values.putNull(TABLE_COL_ID);
			values.put(TABLE_COL_NAME, profile.getName());
			values.put(TABLE_COL_SERIAL_NUMBER, profile.getSerialNumber());
			profile.setId(db.insert(TABLE_PROFILE, null, values));
		} finally {
			close(db);
		}

		return profile.getId();
	}

	public long addTemplate(final Template template,
			final List<TemplateItem> items) throws LincXmapException {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();

		try {
			c = db.query(TABLE_TEMPLATE, TABLE_TEMPLATE_COLS, TABLE_COL_NAME
					+ "=?",
					new String[] { String.valueOf(template.getName()) }, null,
					null, null);

			if (c.moveToNext())
				throw new LincXmapException("Template '" + template.getName()
						+ "' already exists");
		} finally {
			close(c);
			close(db);
		}

		this.execute(new Transaction() {

			@Override
			public void run(SQLiteDatabase db) throws Exception {
				ContentValues tplValues = new ContentValues();
				tplValues.putNull(TABLE_COL_ID);
				tplValues.put(TABLE_COL_NAME, template.getName());
				tplValues.put(TABLE_COL_ROWS, template.getRowCount());
				tplValues.put(TABLE_COL_COLS, template.getColumnCount());
				template.setId(db.insert(TABLE_TEMPLATE, null, tplValues));

				for (TemplateItem item : items) {
					ContentValues itemValues = new ContentValues();
					itemValues.putNull(TABLE_COL_ID);
					itemValues.put(TABLE_COL_TEMPLATE_ID, template.getId());
					itemValues.put(TABLE_COL_PRODUCT_ID, item.getProductId());
					itemValues.put(TABLE_COL_X, item.getX());
					itemValues.put(TABLE_COL_Y, item.getY());
					item.setId(db.insert(TABLE_TEMPLATE_ITEM, null, itemValues));
				}
			}

		});

		return template.getId();
	}

	public int updateTempldate(Template template) {
		Cursor c = null;
		SQLiteDatabase db = getReadableDatabase();

		try {
			c = db.query(TABLE_TEMPLATE, TABLE_TEMPLATE_COLS, TABLE_COL_NAME
					+ "=? and " + TABLE_COL_ID + " <> ?", new String[] {
					template.getName(), String.valueOf(template.getId()) },
					null, null, null);

			if (c.moveToNext())
				throw new LincXmapException("Template '" + template.getName()
						+ "' already exists");
		} finally {
			close(c);
			close(db);
		}

		try {
			db = getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(TABLE_COL_NAME, template.getName());
			values.put(TABLE_COL_ROWS, template.getRowCount());
			values.put(TABLE_COL_COLS, template.getColumnCount());

			return db.update(TABLE_TEMPLATE, values, TABLE_COL_ID + "=?",
					new String[] { String.valueOf(template.getId()) });
		} finally {
			close(db);
		}
	}

	public void deleteHistory(final History history) {
		try {
			this.execute(new Transaction() {

				@Override
				public void run(SQLiteDatabase db) throws Exception {
					String[] args = { String.valueOf(history.getId()) };

					db.delete(TABLE_HISTORY, TABLE_COL_ID + "=?", args);
					db.delete(TABLE_RESULT, TABLE_COL_HISTORY_ID + "=?", args);
				}

			});
		} catch (Throwable t) {
			throw new LincXmapException(t);
		}
	}

	public void deleteProfile(final Profile profile) {
		try {
			this.execute(new Transaction() {

				@Override
				public void run(SQLiteDatabase db) throws Exception {
					String[] args = { String.valueOf(profile.getId()) };

					db.delete(TABLE_PROFILE, TABLE_COL_ID + "=?", args);
					db.delete(TABLE_HISTORY, TABLE_COL_PROFILE_ID + "=?", args);
					// TODO delete results
				}

			});
		} catch (Throwable t) {
			throw new LincXmapException(t);
		}
	}

	public void deleteTemplate(final long id) throws LincXmapException {
		try {
			this.execute(new Transaction() {

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

	public int deleteAllHistory() {
		SQLiteDatabase db = getWritableDatabase();

		try {
			return db.delete(TABLE_HISTORY, null, null);
		} finally {
			close(db);
		}
	}

	protected static void createTables(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CATALOG + "("
				+ TABLE_COL_ID
				+ " integer primary key autoincrement not null, "
				+ TABLE_COL_NAME + " text not null)");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_HISTORY + "("
				+ TABLE_COL_ID
				+ " integer primary key autoincrement not null, "
				+ TABLE_COL_PROFILE_ID + " integer not null, "
				+ TABLE_COL_OWNER + " text not null, " + TABLE_COL_LABEL
				+ " text not null, " + TABLE_COL_TIME + " text not null)");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCT + "("
				+ TABLE_COL_ID
				+ " integer primary key autoincrement not null, "
				+ TABLE_COL_CATALOGUE_ID + " integer not null, "
				+ TABLE_COL_NAME + " text not null)");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PROFILE + "("
				+ TABLE_COL_ID
				+ " integer primary key autoincrement not null, "
				+ TABLE_COL_NAME + " text not null, " + TABLE_COL_SERIAL_NUMBER
				+ " text not null)");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RESULT + "("
				+ TABLE_COL_ID
				+ " integer primary key autoincrement not null, "
				+ TABLE_COL_HISTORY_ID + " integer not null, " + TABLE_COL_NAME
				+ " text not null, " + TABLE_COL_BRIGHTNESS
				+ " numeric not null, " + TABLE_COL_CONCENTRATION
				+ " numeric not null, " + TABLE_COL_MIN + " numeric not null, "
				+ TABLE_COL_MAX + " numeric not null, " + TABLE_COL_FLAG
				+ " text not null)");
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
		for (int i = 0; i < ALL_TABLES.length; i++) {
			db.execSQL("DROP TABLE IF EXISTS " + ALL_TABLES[i]);
		}
	}

	public static void close(SQLiteDatabase db) {
		if (null != db && db.isOpen()) {
			db.close();
		}
	}

	public static void close(Cursor c) {
		if (null != c && !c.isClosed()) {
			c.close();
		}
	}

}
