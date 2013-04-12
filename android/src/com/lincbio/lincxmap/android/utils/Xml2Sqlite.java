package com.lincbio.lincxmap.android.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;

import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.database.Transaction;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;

public final class Xml2Sqlite implements Constants {
	private static int argidx = 0;
	private static long ncatalog = 0;
	private static long nproduct = 0;

	private Context context;
	private DatabaseHelper dbhelper;
	private final Stack<XmlTag> tagStack = new Stack<XmlTag>();

	public Xml2Sqlite(Context context, DatabaseHelper dbhelper) {
		this.context = context;
		this.dbhelper = dbhelper;
	}

	public void parse(final int resId) {
		this.dbhelper.execute(new Transaction() {

			@Override
			public void run(SQLiteDatabase db) throws Exception {
				int nattr = 0;
				int eventType = 0;
				XmlTag tag = null;
				String tagName = null;
				Map<String, String> attrs = null;
				XmlResourceParser parser = null;

				parser = context.getResources().getXml(resId);
				eventType = parser.getEventType();

				while (XmlPullParser.END_DOCUMENT != eventType) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						tagName = parser.getName();
						nattr = parser.getAttributeCount();
						attrs = new HashMap<String, String>(nattr);

						for (int i = 0; i < nattr; ++i) {
							attrs.put(parser.getAttributeName(i),
									parser.getAttributeValue(i));
						}

						tag = new XmlTag(tagName, attrs);

						if (XML_TAG_CATALOG.equals(tagName)) {
							String name = tag.getAttribute(TABLE_COL_NAME);
							ContentValues cv = new ContentValues();
							cv.putNull(TABLE_COL_ID);
							cv.put(TABLE_COL_NAME, name);
							ncatalog = db.insert(TABLE_CATALOG, null, cv);
						} else if (XML_TAG_PRODUCT.equals(tagName)) {
							String name = tag.getAttribute(TABLE_COL_NAME);
							String model = tag.getAttribute(TABLE_COL_MODEL);
							ContentValues cv = new ContentValues();
							cv.putNull(TABLE_COL_ID);
							cv.put(TABLE_COL_CATALOGUE_ID, ncatalog);
							cv.put(TABLE_COL_NAME, name);
							cv.put(TABLE_COL_MODEL, model);
							nproduct = db.insert(TABLE_PRODUCT, null, cv);
							argidx = 0;
						} else if (XML_TAG_ARG.equals(tagName)) {
							String value = tag.getAttribute(TABLE_COL_VALUE);
							ContentValues cv = new ContentValues();
							cv.putNull(TABLE_COL_ID);
							cv.put(TABLE_COL_PRODUCT_ID, nproduct);
							cv.put(TABLE_COL_INDEX, ++argidx);
							cv.put(TABLE_COL_VALUE, value);
							db.insert(TABLE_PRODUCT_ARG, null, cv);
						}

						tagStack.add(tag);
						break;
					case XmlPullParser.TEXT:
						tag = tagStack.peek();
						tagName = tag.getName();

						// TODO parse tag content

						break;
					case XmlPullParser.END_TAG:
						tag = tagStack.peek();
						tagName = parser.getName();

						if (tagName.equals(tag.getName())) {
							tagStack.pop();
						}
						break;
					default:
						break;
					}

					eventType = parser.next();
				}

			}
		});
	}

	static class XmlTag {
		private final String name;
		private final Map<String, String> attrs = new HashMap<String, String>();

		public XmlTag(String name) {
			this.name = name;
		}

		public XmlTag(String name, Map<String, String> attrs) {
			this(name);
			this.attrs.putAll(attrs);
		}

		public String getName() {
			return this.name;
		}

		public String getAttribute(String attrName) {
			return this.attrs.get(attrName);
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

}
