package com.awesomeproject;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends ReactActivity {

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "AwesomeProject";
  }

  /**
   * Returns the instance of the {@link ReactActivityDelegate}. Here we use a util class {@link
   * DefaultReactActivityDelegate} which allows you to easily enable Fabric and Concurrent React
   * (aka React 18) with two boolean flags.
   */
  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new DefaultReactActivityDelegate(
        this,
        getMainComponentName(),
        // If you opted-in for the New Architecture, we enable the Fabric Renderer.
        DefaultNewArchitectureEntryPoint.getFabricEnabled(), // fabricEnabled
        // If you opted-in for the New Architecture, we enable Concurrent React (i.e. React 18).
        DefaultNewArchitectureEntryPoint.getConcurrentReactEnabled() // concurrentRootEnabled
        );
  }





  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_main);
    String permission;
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
      permission = android.Manifest.permission.READ_MEDIA_VIDEO;
    } else {
      permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    }
    Dexter.withContext(this)
            .withPermission(permission).withListener(new PermissionListener() {
              public void onPermissionDenied(PermissionDeniedResponse param2PermissionDeniedResponse) {
                if (param2PermissionDeniedResponse.isPermanentlyDenied())
                  finish();

              }

              public void onPermissionGranted(PermissionGrantedResponse param2PermissionGrantedResponse) {

//
              }

              public void onPermissionRationaleShouldBeShown(PermissionRequest param2PermissionRequest,
                                                             PermissionToken param2PermissionToken) {
                param2PermissionToken.continuePermissionRequest();
              }
            }).check();
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

  public boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  public boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  public boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      Uri uri = data.getData();

      SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
      SharedPreferences.Editor myEdit = sharedPreferences.edit();
      myEdit.putString("path", getPath(this, uri));
      myEdit.commit();
      // Log.d("fdfdsfdfdfd", "onActivityResult: "+uri.toString());
      // Log.d("fdfdsfdfdfd", "onActivityResult: "+getPath(this,uri));
      // videoPath =getPath(this,uri);
      final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
      intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
              new ComponentName(this, VideoLiveWallpaper.class));
      startActivityForResult(intent, 100);
      // startActivity(intent);
    } else {
      Toast.makeText(this, "Select a file first", Toast.LENGTH_SHORT).show();

    }

  }
}
