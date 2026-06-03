package com.omni.negociacaobezerros.utils.pdf;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public final class Files {
    private static final String DOCUMENTS_SUBFOLDER = "Negociacao Bezerros";

    private Files() {
        throw new AssertionError("FileHelper is a utility class and must not be instantiated.");
    }

    @NonNull
    public static Uri uri(@NonNull Context context, @NonNull File file) {
        return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
    }

    @NonNull
    public static ArrayList<Uri> uris(@NonNull Context context, @NonNull List<File> files) {
        ArrayList<Uri> list = new ArrayList<>(files.size());
        for (File file : files) list.add(uri(context, file));
        return list;
    }


    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Uri save(
            @NonNull Context context,
            @NonNull File file,
            @NonNull String mimeType) throws IOException {

        Uri uri = insertEntry(context.getContentResolver(), file, mimeType);
        copyTo(context.getContentResolver(), file, uri);
        return uri;
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static Uri insertEntry(
            @NonNull ContentResolver resolver,
            @NonNull File file,
            @NonNull String mimeType) throws IOException {

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOCUMENTS + "/" + DOCUMENTS_SUBFOLDER
        );

        Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), values);
        if (uri == null) throw new IOException("Failed to create MediaStore entry.");
        return uri;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static void copyTo(
            @NonNull ContentResolver resolver,
            @NonNull File source,
            @NonNull Uri dest) throws IOException {

        try (InputStream in = new FileInputStream(source);
             OutputStream out = resolver.openOutputStream(dest)) {
            if (out == null) throw new IOException("Failed to open output stream.");
            FileUtils.copy(in, out);
        }
    }

    public static void share(
            @NonNull Activity activity,
            @NonNull File file,
            @NonNull String mimeType,
            @NonNull String title) {

        activity.startActivity(shareIntent(activity, file, mimeType, title));
    }

    public static void shareAll(
            @NonNull Activity activity,
            @NonNull List<File> files,
            @NonNull String mimeType,
            @NonNull String title) {
        activity.startActivity(shareAllIntent(activity, files, mimeType, title));
    }

    @NonNull
    public static Intent shareIntent(
            @NonNull Context context,
            @NonNull File file,
            @NonNull String mimeType,
            @NonNull String title) {

        Intent intent = new Intent(Intent.ACTION_SEND)
                .setType(mimeType)
                .putExtra(Intent.EXTRA_STREAM, uri(context, file))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return Intent.createChooser(intent, title);
    }

    @NonNull
    public static Intent shareAllIntent(
            @NonNull Context context,
            @NonNull List<File> files,
            @NonNull String mimeType,
            @NonNull String title) {

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE)
                .setType(mimeType)
                .putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris(context, files))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return Intent.createChooser(intent, title);
    }
}