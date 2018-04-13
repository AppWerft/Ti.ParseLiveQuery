/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package de.appwerft.parselivequery;

import java.net.URI;
import java.net.URISyntaxException;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;

import android.content.Context;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseUser;

@Kroll.module(name = "Parselivequery", id = "de.appwerft.parselivequery")
public class ParselivequeryModule extends KrollModule {

	// Standard Debugging variables
	private static final String LCAT = "PLQ";
	public static final String QUERY = "query";
	ParseLiveQueryClient client;
	Context ctx;

	// You can define constants with @Kroll.constant, for example:
	// @Kroll.constant public static final String EXTERNAL_NAME = value;

	public ParselivequeryModule() {
		super();
		ctx = TiApplication.getInstance().getBaseContext();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app) {
		Log.d(LCAT, "inside onAppCreate");
		// put module init code that needs to run when the application is
		// created
	}

	@Kroll.method
	public boolean setEndpoint(@Kroll.argument(optional = true) KrollDict opts) {
		String applicationId = null;
		String clientKey = null;
		if (opts != null) {
			if (opts.containsKeyAndNotNull("applicationId")) {
				applicationId = opts.getString("applicationId");
			}
			if (opts.containsKeyAndNotNull("clientKey")) {
				clientKey = opts.getString("clientKey");
			}
			if (opts.containsKeyAndNotNull(TiC.PROPERTY_URI)) {
				try {
					@SuppressWarnings("unused")
					URI dummy = new URI(opts.getString(TiC.PROPERTY_URI));
					// first we need Parse client
					Parse.initialize(new Parse.Configuration.Builder(ctx)
							.applicationId(applicationId).clientKey(clientKey)
							.server(opts.getString(TiC.PROPERTY_URI)).build());
					// and a parseLiveQueryClient
					client = ParseLiveQueryClient.Factory.getClient();
					return true;
				} catch (URISyntaxException e) {
					e.printStackTrace();
					return false;
				}
			}

		} else
			client = ParseLiveQueryClient.Factory.getClient();
		return true;
	}

	@Kroll.method
	void logout() {
		ParseUser.logOut();
	}

	@Kroll.method
	void login(KrollDict opts) {
		if (client == null)
			return;
		final KrollCallbacks kcb = new KrollCallbacks(opts);
		String email = null, password = null;
		if (opts.containsKeyAndNotNull("email")) {
			email = opts.getString("email");
		}
		if (opts.containsKeyAndNotNull("password")) {
			password = opts.getString("password");
		}
		ParseUser.logInInBackground(email, password, new LogInCallback() {
			@Override
			public void done(ParseUser parseUser, com.parse.ParseException e) {
				KrollDict kd = new KrollDict();
				if (parseUser != null)
					if (kcb.onSuccess != null) {
						kcb.onSuccess.call(getKrollObject(), kd);
					} else {
						if (kcb.onError != null) {
							kcb.onError.call(getKrollObject(), kd);
						}
					}
			}
		});
	}

	@Kroll.method
	void loginAnonymous(KrollDict opts) {
		if (client == null)
			return;
		final KrollCallbacks kcb = new KrollCallbacks(opts);
		ParseAnonymousUtils.logIn(new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				KrollDict res = new KrollDict();
				if (e != null) {
					res.put("user", user.toString());
					kcb.onSuccess.call(getKrollObject(), res);
				} else {
					kcb.onError.call(getKrollObject(), res);
				}
			}
		});
	}
}
