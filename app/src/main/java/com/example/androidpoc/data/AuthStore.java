package com.example.androidpoc.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * 認証情報の永続化。メールアドレスは平文 {@link SharedPreferences}、
 * userID は Keystore 裏付けの {@link EncryptedSharedPreferences} に保存する
 * （iOS 版の UserDefaults / Keychain の使い分けに相当）。
 */
public class AuthStore {
    private static final String EMAIL_PREFS = "auth_prefs";
    private static final String EMAIL_KEY = "login_email";
    private static final String SECURE_PREFS = "auth_secure_prefs";
    private static final String USER_ID_KEY = "user_id";

    private final SharedPreferences emailPrefs;
    private final SharedPreferences securePrefs;

    public AuthStore(Context context) {
        Context appContext = context.getApplicationContext();
        emailPrefs = appContext.getSharedPreferences(EMAIL_PREFS, Context.MODE_PRIVATE);
        securePrefs = createSecurePrefs(appContext);
    }

    private static SharedPreferences createSecurePrefs(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    context,
                    SECURE_PREFS,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to create secure storage", e);
        }
    }

    public void saveEmail(String email) {
        emailPrefs.edit().putString(EMAIL_KEY, email).apply();
    }

    public String loadEmail() {
        return emailPrefs.getString(EMAIL_KEY, null);
    }

    public void saveUserId(String userId) {
        securePrefs.edit().putString(USER_ID_KEY, userId).apply();
    }

    public String loadUserId() {
        return securePrefs.getString(USER_ID_KEY, null);
    }
}
