package com.pagatodoholdings.posandroid;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pagatodo.sigmalib.reportetrx.LocalDBContract;
import com.pagatodoholdings.posandroid.utils.Logger;


public class LocalDbProvider extends ContentProvider {
    private static UriMatcher uriMatcher;
    private LocalDBAdapter localDBAdapter;
    static final int TRANSACCIONES = 100;
    static final String TABLE_TRANSACCIONES = "transacciones";
    static final String TABLE_TRANSACCION_DETALLE = "transaccion_detalle";
    static final String TABLE_CATALOGO_PRODUCTOS = "catalogo_productos";
    static final String ERROR_ROW = "Failed to insert row into ";
    static final String ERROR_URI = "Unknown uri: ";
    private static final SQLiteQueryBuilder PRODUCT_QUERIED_QUERY_BUILDER = new SQLiteQueryBuilder();

    public LocalDbProvider() {
        //Do nothing
    }

    public boolean onCreate() {
        this.localDBAdapter = new LocalDBAdapter(this.getContext());
        return true;
    }

    @Nullable
    public String getType(@NonNull Uri uri) {
        if (getUriMatcher().match(uri) == 100) {

            return LocalDBContract.TransaccionesEntry.CONTENT_TYPE;
        } else {
                throw new UnsupportedOperationException(ERROR_URI + uri);
        }
    }

    @Nullable
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = this.localDBAdapter.getWritableDatabase();
        Cursor retCursor;
        if (getUriMatcher().match(uri) == 100) {
            retCursor = database.query(TABLE_TRANSACCIONES, projection, selection, selectionArgs, (String) null, (String) null, sortOrder);
        } else {
                throw new UnsupportedOperationException(ERROR_URI + uri);
        }

        retCursor.setNotificationUri(this.getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database = this.localDBAdapter.getWritableDatabase();
        Uri returnUri;
        long idTrans;
        if (getUriMatcher().match(uri) == 100) {

            idTrans = database.insert(TABLE_TRANSACCIONES, (String) null, values);
            if (idTrans <= 0L) {
                throw new SQLException(ERROR_ROW + uri);
            }

            returnUri = LocalDBContract.TransaccionesEntry.buildAddressUri(idTrans);
        } else {
                throw new UnsupportedOperationException(ERROR_URI + uri);
        }

        this.getContext().getContentResolver().notifyChange(uri, (ContentObserver)null);
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @Nullable ContentValues[] values) {
        SQLiteDatabase database = this.localDBAdapter.getWritableDatabase();
        int returnCount = 0;

        if (values != null) {
            switch (getUriMatcher().match(uri)) {
                case 100: //TABLE_TRANSACCIONES
                    return doTransaction(uri,values, database, TABLE_TRANSACCIONES);
                case 101: //TABLE_TRANSACCION_DETALLE
                    return doTransaction(uri,values, database, TABLE_TRANSACCION_DETALLE);
                case 200: //TABLE_CATALOGO_PRODUCTOS
                    return doTransaction(uri,values, database, TABLE_CATALOGO_PRODUCTOS);
                default:
                    return super.bulkInsert(uri, values);
            }
        }
        return returnCount;
    }

    private int doTransaction(final Uri uri, final ContentValues[] values, final SQLiteDatabase database, final String table) {

        int returnCount = 0;
        ContentValues[] var5;
        int var6;
        int var7;
        ContentValues value;

        database.beginTransaction();

        try {
            var5 = values;
            var6 = values.length;
            var7 = 0;

            while (true) {
                if (var7 >= var6) {
                    database.setTransactionSuccessful();
                    break;
                }

                value = var5[var7];
                database.insert(table, (String) null, value);
                ++returnCount;
                ++var7;
            }

        }
        catch (Exception exe){
            Logger.LOGGER.throwing("LocalDbProvider", 1, new Throwable("Error"), "Exception: " + exe.getMessage());
        }
        finally {
            database.endTransaction();
        }

        this.getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
        return returnCount;
    }

    public int delete(@NonNull Uri uri, @Nullable String selectionSinValidar, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = this.localDBAdapter.getWritableDatabase();
        String selection;
        if (selectionSinValidar == null) {
            selection = "1";
        } else {
            selection = selectionSinValidar;
        }

        int rowsDeleted;
        if (getUriMatcher().match(uri) == 100) {
            rowsDeleted = database.delete(TABLE_TRANSACCIONES, selection, selectionArgs);
        } else {
                throw new UnsupportedOperationException(ERROR_URI + uri);
        }

        if (rowsDeleted != 0) {
            this.getContext().getContentResolver().notifyChange(uri, (ContentObserver)null);
        }

        return rowsDeleted;
    }

    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = this.localDBAdapter.getWritableDatabase();
        int rowsUpdated;
        if (getUriMatcher().match(uri) == 100) {
            rowsUpdated = database.update(TABLE_TRANSACCIONES, values, selection, selectionArgs);
        } else {
            throw new UnsupportedOperationException(ERROR_URI+ uri);
        }

        if (rowsUpdated != 0) {
            this.getContext().getContentResolver().notifyChange(uri, (ContentObserver)null);
        }

        return rowsUpdated;
    }

    public static UriMatcher getUriMatcher() {
        if (uriMatcher == null) {
            uriMatcher = buildUriMatcher();
        }

        return uriMatcher;
    }

    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(-1);
        matcher.addURI(LocalDBContract.CONTENT_AUTHORITY, TABLE_TRANSACCIONES, 100);
        matcher.addURI(LocalDBContract.CONTENT_AUTHORITY, TABLE_CATALOGO_PRODUCTOS, 200);
        matcher.addURI(LocalDBContract.CONTENT_AUTHORITY, "transacciones_detalle", 101);
        matcher.addURI(LocalDBContract.CONTENT_AUTHORITY, "transacciones_detalle/left_join_catalogo_productos", 102);
        return matcher;
    }

    static {
        PRODUCT_QUERIED_QUERY_BUILDER.setTables("transaccion_detalle as t1 LEFT JOIN catalogo_productos as t2 USING (id_producto)");
    }
}
