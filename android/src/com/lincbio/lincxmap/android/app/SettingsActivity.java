package com.lincbio.lincxmap.android.app;

import java.util.regex.Matcher;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.utils.Toasts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;

public class SettingsActivity extends PreferenceActivity implements Constants {
	private static final KeyListener digitsKeyListener = DigitsKeyListener
			.getInstance(false, false);

	private EditTextPreference ssgPref;
	private EditTextPreference sssPref;
	private ListPreference dpPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.addPreferencesFromResource(R.xml.settings);

		this.dpPref = (ListPreference) findPreference(KEY_DETECTION_POLICY);
		this.ssgPref = (EditTextPreference) findPreference(KEY_SAMPLE_SELECTOR_GAP);
		this.sssPref = (EditTextPreference) findPreference(KEY_SAMPLE_SELECTOR_SIZE);

		this.ssgPref.getEditText().setKeyListener(digitsKeyListener);
		this.sssPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			private final Context context = SettingsActivity.this;

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				SharedPreferences sp = getSharedPreferences();
				String defType = getString(R.string.default_sample_selector_type);
				String type = sp.getString(KEY_SAMPLE_SELECTOR_TYPE, defType);

				if (!defType.equals(type)) {
					Matcher matcher = PATTERN_SIZE.matcher(String.valueOf(newValue));

					if (!matcher.matches()) {
						Toasts.show(context, R.string.msg_unsupported_value);
						return false;
					}
				} else {
					try {
						Integer.parseInt(String.valueOf(newValue));
					} catch (NumberFormatException e) {
						Toasts.show(context, R.string.msg_unsupported_value);
						return false;
					}
				}
				
				return true;
			}
		});
		this.dpPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			private final Context context = SettingsActivity.this;

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				String auto = getString(R.string.detection_policy_auto);

				if (newValue.equals(auto)) {
					Toasts.show(context, R.string.msg_unsupported_value);
					return false;
				}

				return true;
			}
		});
	}

	private SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(this);
	}

}
