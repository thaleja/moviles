package com.example.appsqilteproducts;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText reference, description, price;
    TextView message;
    Spinner refType;
    ImageButton save, search, delete, list, edit;
    // Array que llenara el spinner
    String[] arrRefType = {"Aseo", "Comestible", "Electrodoméstico"};
    // Instanciar la clase de sqilite
    clsDBSqilite oDB = new clsDBSqilite(this, "dbInventory", null, 1);
    // Generar el objeto de Product
    Product oProduct = new Product();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Refenciar los objetos con los ids del archivo
        reference = findViewById(R.id.etReference);
        description = findViewById(R.id.etDescription);
        price = findViewById(R.id.etPrecio);
        message = findViewById(R.id.tvMessage);
        save = findViewById(R.id.ibSave);
        search = findViewById(R.id.ibSearch);
        edit = findViewById(R.id.ibEdit);
        delete = findViewById(R.id.ibDelete);
        list = findViewById(R.id.ibList);
        refType = findViewById(R.id.spRefType);
        // Generar el arrarAdapter que será llenado con el arrRefType
        ArrayAdapter<String> adpRefType = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, arrRefType);
        // Asignar el anterior adaptador el spinner
        refType.setAdapter(adpRefType);
        // Eventos de cada botón
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String mRef = reference.getText().toString();
                if (!mRef.isEmpty()){
                    if (searchReference(mRef).size() > 0){
                        // Recuperar los datos del objeto oProduct
                        description.setText(oProduct.getDescription());
                        price.setText(String.valueOf(oProduct.getPrice()));
                        switch (oProduct.getReftype()) {
                            case 0:
                                refType.setSelection(0);
                                break;
                            case 1:
                                refType.setSelection(1);
                                break;
                            case 2:
                                refType.setSelection(2);
                                break;
                        }

                    }
                    else {
                        message.setTextColor(Color.RED);
                        message.setText("La referenia NO EXISTE. Intentelo con otra...");
                    }

                }
                else {
                    message.setTextColor(Color.RED);
                    message.setText("Ingrese la referencia a buscar");

                }
            }
        });
        // Guardar
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mRef = reference.getText().toString();
                String mDescription = description.getText().toString();
                String mPrice = price.getText().toString();
                String mRefType = refType.getSelectedItem().toString();
                // Invocar un método para chequear que todos los datos estén diligenciados
                if (checkData(mRef, mDescription, mPrice)){
                    // Buscar la referencia en la tabla Product
                    if (searchReference(reference.getText().toString()).size() == 0){
                        // Guardar el producto
                        // Crear objeto de SQLiteDatabase en modo escritura
                        SQLiteDatabase osldbProd = oDB.getWritableDatabase();
                        // Crear una tabala temporar con ContentValues con los mismos campos de
                        // la tabla producto
                        ContentValues cvProduct = new ContentValues();
                        cvProduct.put("reference", mRef);
                        cvProduct.put("description", mDescription);
                        cvProduct.put("price", Integer.valueOf(mPrice));
                        int mtypeRef = 0;
                        switch (mRefType){
                            case "Aseo":
                               mtypeRef = 0;
                                break;
                            case "Comestible":
                                mtypeRef = 1;
                                break;
                            case "Electrodomestico":
                                mtypeRef = 2;
                                break;
                        }
                        cvProduct.put("reftype", mtypeRef);
                        // Almacenar el nuevo producto
                        osldbProd.insert("product", null, cvProduct);
                        message.setTextColor(Color.GREEN);
                        message.setText("El producto se ha guardado exitosamente...");
                        osldbProd.close();
                    }
                    else {
                        message.setTextColor(Color.RED);
                        message.setText("La referencia EXISTE. Inténtelo con otra...");
                    }
                }
                else {
                   message.setTextColor(Color.RED);
                   message.setText("Debe diligenciar todos los datos del producto");
                }
            }
        });
    }

    private ArrayList<Product> searchReference(String mRef) {
        // Definir el arrayList que se devoverá
        ArrayList<Product> arProd = new ArrayList<Product>();
        // Generar objeto de la clase SQLiteDatabase en modo lectura
        SQLiteDatabase osdbRead = oDB.getReadableDatabase();
        String query = "Select description, price, reftype from product where reference = '" +mRef+"'";
        // Generar una tabla Cursor para almacenar los registros enviados por un query
        Cursor cProduct = osdbRead.rawQuery(query,null);
        // Verificar si la tabla cProduct tiene al menos un registro
        if (cProduct.moveToFirst()){
            // Llenar el objeto de Product
            oProduct.setReference(mRef);
            oProduct.setDescription(cProduct.getString(0));
            oProduct.setPrice(cProduct.getInt(1));
            oProduct.setReftype(cProduct.getInt(2));
            // Agregar el objeto l ArrayList arProd
            arProd.add(oProduct);
        }
        return arProd;
    }

    private boolean checkData(String mRef, String mDescription, String mPrice) {
        return !mRef.isEmpty() && !mDescription.isEmpty() && !mPrice.isEmpty();
    }
}