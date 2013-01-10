package com.lincbio.lincxmap.android;

public interface Constants {

	public static final String CATEGORY_TOOLBAR = "lincxmap.intent.category.toolbar";

	public static final String KEY_DETECTION_POLICY = "key_detection_policy";

	public static final String VALUE_DETECTION_POLICY_AUTO = "0";

	public static final String VALUE_DETECTION_POLICY_MANUAL = "1";

	public static final String KEY_SAMPLE_SELECTOR_GAP = "key_sample_selector_gap";

	public static final String KEY_SAMPLE_SELECTOR_SIZE = "key_sample_selector_size";

	public static final String KEY_SAMPLE_IDENTIFIER = "key_sample_identifier";

	public static final String KEY_SCAN_FOR_DEVICES = "scan_for_devices";

	public static final String PARAM_CATALOG_ID = "catalog-id";

	public static final String PARAM_IMAGE_SOURCE = "image-source";

	public static final String PARAM_HISTORY_ID = "history-id";
	
	public static final String PARAM_PRODUCT_ID = "product-id";

	public static final String PARAM_PROFILE_OBJECT = "profile-object";

	public static final String PARAM_TEMPLATE_OBJECT = "template-object";

	public static final String PARAM_TEMPLATE_ID = "template-id";

	public static final String PARAM_TEMPLATE_ITEM_ID = "template-item-id";

	public static final String PARAM_SAMPLE_LIST = "sample-list";

	public static final String TABLE_CATALOG = "catalog";

	public static final String TABLE_HISTORY = "history";

	public static final String TABLE_PRODUCT = "product";

	public static final String TABLE_PROFILE = "profile";

	public static final String TABLE_RESULT = "result";

	public static final String TABLE_TEMPLATE = "template";

	public static final String TABLE_TEMPLATE_ITEM = "template_item";

	public static final String TABLE_COL_BRIGHTNESS = "brightness";

	public static final String TABLE_COL_CONCENTRATION = "concentration";

	public static final String TABLE_COL_ID = "id";

	public static final String TABLE_COL_NAME = "name";

	public static final String TABLE_COL_ROWS = "rows";

	public static final String TABLE_COL_COLS = "cols";

	public static final String TABLE_COL_CATALOGUE_ID = "catalog_id";

	public static final String TABLE_COL_HISTORY_ID = "history_id";

	public static final String TABLE_COL_LABEL = "label";

	public static final String TABLE_COL_OWNER = "owner";

	public static final String TABLE_COL_PROFILE_ID = "profile_id";

	public static final String TABLE_COL_PRODUCT_ID = "product_id";

	public static final String TABLE_COL_SERIAL_NUMBER = "serial_number";

	public static final String TABLE_COL_TEMPLATE_ID = "template_id";

	public static final String TABLE_COL_TIME = "time";

	public static final String TABLE_COL_X = "x";

	public static final String TABLE_COL_Y = "y";

	public static final String[] TABLE_CATALOG_COLS = { TABLE_COL_ID,
			TABLE_COL_NAME };

	public static final String[] TABLE_HISTORY_COLS = { TABLE_COL_ID,
			TABLE_COL_PROFILE_ID, TABLE_COL_OWNER, TABLE_COL_LABEL,
			TABLE_COL_TIME };

	public static final String[] TABLE_PRODUCT_COLS = { TABLE_COL_ID,
			TABLE_COL_CATALOGUE_ID, TABLE_COL_NAME };

	public static final String[] TABLE_PROFILE_COLS = { TABLE_COL_ID,
			TABLE_COL_NAME, TABLE_COL_SERIAL_NUMBER };

	public static final String[] TABLE_RESULT_COLS = { TABLE_COL_ID,
			TABLE_COL_HISTORY_ID, TABLE_COL_NAME, TABLE_COL_BRIGHTNESS,
			TABLE_COL_CONCENTRATION };

	public static final String[] TABLE_TEMPLATE_COLS = { TABLE_COL_ID,
			TABLE_COL_NAME, TABLE_COL_ROWS, TABLE_COL_COLS };

	public static final String[] TABLE_TEMPLATE_ITEM_COLS = { TABLE_COL_ID,
			TABLE_COL_TEMPLATE_ID, TABLE_COL_PRODUCT_ID, TABLE_COL_X,
			TABLE_COL_Y };

	public static final String[] ALL_TABLES = { TABLE_CATALOG, TABLE_HISTORY,
			TABLE_PRODUCT, TABLE_PROFILE, TABLE_RESULT, TABLE_TEMPLATE,
			TABLE_TEMPLATE_ITEM };

	public static final String XML_TAG_CATALOG = "catalog";

	public static final String XML_TAG_PRODUCT = "product";

	public static final String XML_ATTR_NAME = "name";

}
