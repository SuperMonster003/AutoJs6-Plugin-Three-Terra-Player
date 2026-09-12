package io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** Test APK process: pure Java because Kotlin runtime classes live in the target APK. */
public class PlaylistFixtureProvider extends ContentProvider {
    @Override public boolean onCreate() { return true; }

    @Override public String getType(Uri uri) {
        return DocumentsContract.getDocumentId(uri).endsWith(".m3u") ? "audio/x-mpegurl" : "audio/mpeg";
    }

    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] args, String order) {
        String id = DocumentsContract.getDocumentId(uri);
        String[][] documents;
        if ("children".equals(uri.getLastPathSegment())) {
            if ("root".equals(id)) {
                documents = new String[][] {
                    {"song1", "song1.mp3", "audio/mpeg"},
                    {"song2", "song2.mp3", "audio/mpeg"},
                    {"album-id", "album", DocumentsContract.Document.MIME_TYPE_DIR},
                };
            } else if ("album-id".equals(id)) {
                documents = new String[][] {{"sub-song", "sub.mp3", "audio/mpeg"}};
            } else {
                documents = new String[0][];
            }
        } else {
            documents = new String[][] {{id, id, getType(uri)}};
        }
        String[] columns = projection != null ? projection : new String[] {OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE};
        MatrixCursor cursor = new MatrixCursor(columns);
        for (String[] document : documents) {
            Object[] row = new Object[columns.length];
            for (int index = 0; index < columns.length; index++) {
                switch (columns[index]) {
                    case DocumentsContract.Document.COLUMN_DOCUMENT_ID: row[index] = document[0]; break;
                    case OpenableColumns.DISPLAY_NAME: row[index] = document[1]; break;
                    case DocumentsContract.Document.COLUMN_MIME_TYPE: row[index] = document[2]; break;
                    case OpenableColumns.SIZE: row[index] = 100L; break;
                }
            }
            cursor.addRow(row);
        }
        return cursor;
    }

    @Override public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (!"r".equals(mode)) throw new SecurityException("Read only");
        String id = DocumentsContract.getDocumentId(uri);
        File file = new File(getContext().getCacheDir(), "playlist-fixture-" + id.replace('/', '_'));
        String content = "list.m3u".equals(id)
            ? "#EXTM3U\n#EXTINF:-1,Second\nsong2.mp3\n#EXTINF:-1,First\nsong1.mp3\n#EXTINF:-1,Again\nsong2.mp3\nalbum/sub.mp3\nmissing.mp3\n../secret.mp3\nhttps://example.invalid/media.mp3\n"
            : "fixture";
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException error) {
            throw new FileNotFoundException(error.getMessage());
        }
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override public Uri insert(Uri uri, ContentValues values) { throw new UnsupportedOperationException(); }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] args) { throw new UnsupportedOperationException(); }
    @Override public int delete(Uri uri, String selection, String[] args) { throw new UnsupportedOperationException(); }
}
