package com.omni.negociacaobezerros.utils.document;

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
    private static final String FOLDER = "Negociacao Bezerros";
    private static final String PROVIDER = ".provider";
    private static final String MEDIA_VOLUME = "external";
    private static final String ERR_ENTRY = "Falha ao criar entrada no MediaStore.";
    private static final String ERR_STREAM = "Falha ao abrir stream de saída.";

    private Files() {
        throw new AssertionError("Files é uma classe utilitária e não deve ser instanciada.");
    }

    @NonNull
    public static Uri uri(@NonNull Context context, @NonNull File file) {
        return FileProvider.getUriForFile(context, context.getPackageName() + PROVIDER, file);
    }

    @NonNull
    public static ArrayList<Uri> uris(@NonNull Context context, @NonNull List<File> files) {
        ArrayList<Uri> uris = new ArrayList<>(files.size());
        for (File file : files) uris.add(uri(context, file));
        return uris;
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Uri save(@NonNull Context context, @NonNull File file, @NonNull String mimeType) throws IOException {
        ContentResolver resolver = context.getContentResolver();
        Uri dest = insert(resolver, file, mimeType);
        copy(resolver, file, dest);
        return dest;
    }

    public static void share(@NonNull Activity activity, @NonNull File file, @NonNull String mimeType, @NonNull String title) {
        activity.startActivity(shareIntent(activity, file, mimeType, title));
    }

    public static void shareAll(@NonNull Activity activity, @NonNull List<File> files, @NonNull String mimeType, @NonNull String title) {
        activity.startActivity(shareAllIntent(activity, files, mimeType, title));
    }

    @NonNull
    public static Intent shareIntent(@NonNull Context context, @NonNull File file, @NonNull String mimeType, @NonNull String title) {
        Intent intent = new Intent(Intent.ACTION_SEND).setType(mimeType)
                .putExtra(Intent.EXTRA_STREAM, uri(context, file))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return Intent.createChooser(intent, title);
    }

    @NonNull
    public static Intent shareAllIntent(@NonNull Context context, @NonNull List<File> files, @NonNull String mimeType, @NonNull String title) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE).setType(mimeType)
                .putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris(context, files))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return Intent.createChooser(intent, title);
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static Uri insert(@NonNull ContentResolver resolver, @NonNull File file, @NonNull String mimeType) throws IOException {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/" + FOLDER);
        Uri uri = resolver.insert(MediaStore.Files.getContentUri(MEDIA_VOLUME), values);
        if (uri == null) throw new IOException(ERR_ENTRY);
        return uri;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static void copy(@NonNull ContentResolver resolver, @NonNull File src, @NonNull Uri dest) throws IOException {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = resolver.openOutputStream(dest)) {
            if (out == null) throw new IOException(ERR_STREAM);
            FileUtils.copy(in, out);
        }
    }
}