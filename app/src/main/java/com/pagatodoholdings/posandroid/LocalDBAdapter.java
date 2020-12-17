package com.pagatodoholdings.posandroid;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDBAdapter extends SQLiteOpenHelper {

    public LocalDBAdapter(Context context) {
        super(context, "transacciones.db", null, 16);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE transacciones ( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, fecha INTEGER NOT NULL, codigooper TEXT DEFAULT 'V', procod TEXT, descmenu TEXT, importe TEXT, moneda TEXT, importeorigen TEXT, fee INTEGER, reflocal TEXT, stan INTEGER NOT NULL UNIQUE, refremota TEXT, refcliente TEXT, descproducto TEXT, turno INTEGER NOT NULL, contabilizar INTEGER NOT NULL, zincronizada INTEGER NOT NULL, cuenta INTEGER, dia INTEGER, total TEXT, cashback REAL, impuesto REAL, propina REAL, medio_pago INTEGER, tipo_tarjeta INTEGER);");
        sqLiteDatabase.execSQL("CREATE TABLE transaccion_detalle ( autorizacion_pt TEXT, cantidad TEXT, cantidad_promo TEXT, descuento TEXT, fecha_actualizacion TEXT, id_campana INTEGER, id_producto INTEGER, id_venta INTEGER, id_venta_detalle INTEGER, importe_compra TEXT, importe_venta TEXT, is_premio INTEGER, referencia TEXT, sobre_cargo TEXT);");
        sqLiteDatabase.execSQL("CREATE TABLE catalogo_productos ( descripcion TEXT, desviacion TEXT, existencia TEXT, fecha_sync TEXT, id_categoria INTEGER, id_marca INTEGER, id_producto INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, id_tipo_producto INTEGER, id_unidad INTEGER, minimo INTEGER, precio_compra TEXT, precio_venta TEXT, precio_venta_mayoreo TEXT, promedio TEXT, sku TEXT, sugerido TEXT, sync INTEGER, techo TEXT, total_ventas TEXT, db_state INTEGER, image_preview TEXT, logo TEXT, custom INTEGER);");
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int contador, int contador1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS transacciones");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS transaccion_detalle");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS catalogo_productos");
        this.onCreate(sqLiteDatabase);
    }
}
