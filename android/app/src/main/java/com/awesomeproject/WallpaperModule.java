package com.awesomeproject;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import java.util.Map;
import java.util.HashMap;

public class WallpaperModule extends ReactContextBaseJavaModule  {
    ReactApplicationContext context;
    WallpaperModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
//        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "WallpaperModule";
    }

    @ReactMethod
    public void setWallpaper(String name) {
        Log.d("WallpaperModule", "Create event called with name: " + name);
        chooseVideo();
    }
    private void chooseVideo() {
        Activity currentActivity = getCurrentActivity();
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        currentActivity.startActivityForResult(intent, 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getPath(final Context context, final Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {

            Log.d("WallpaperModule", "resultCode " + resultCode +" requestCode " + resultCode+" intent " + intent);
            if (resultCode == Activity.RESULT_OK && intent != null) {
                Uri uri = intent.getData();
                Log.d("WallpaperModule", getPath(context, uri));

                Activity currentActivity = getCurrentActivity();
                SharedPreferences sharedPreferences = currentActivity.getSharedPreferences("pref", context.MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("path", getPath(currentActivity, uri));
                myEdit.commit();
                // Log.d("fdfdsfdfdfd", "onActivityResult: "+uri.toString());
                // Log.d("fdfdsfdfdfd", "onActivityResult: "+getPath(this,uri));
                // videoPath =getPath(this,uri);
                final Intent nintent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                nintent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(currentActivity, VideoLiveWallpaper.class));
                currentActivity.startActivityForResult(nintent, 100);
//                currentActivity.startActivity(nintent);
            } else {
//                Toast.makeText(this, "Select a file first", Toast.LENGTH_SHORT).show();
Log.d("WallpaperModule","Select a file first");
            }
        }
    };

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
}
