package com.example.conversormonedainternet;

import static java.lang.Double.parseDouble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.ContentValues;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.Formatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;



public class MainActivity extends AppCompatActivity {


    //accedo a los elementos de la vista
    TextView tMonedaCambioCM;
    Spinner sMonedaCambioCM;
    EditText eValorAConvertirCM;
    Button convertirCM;
    TextView tvConvertidoCM;
    Button bTodasMonedasCM;

    //variable usada para mostrar un toast en caso de que no hay conexion a internet
    boolean toas;

    //concedo permisos de internet
    private static final int REQUEST_EXTERNAL_INTERNET = 1;
    private static String[] PERMISSIONS_INTERNET = {
            Manifest.permission.INTERNET
    };

    //arrays para guardar la informacion que recogo del XML
    public  static String[] inicialesCM;
    public   static String[] ratioCM;
    public  static String[] iniciales_ratioCM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_INTERNET, REQUEST_EXTERNAL_INTERNET);

        //accedo a los elementos del layout
        tMonedaCambioCM = (TextView) findViewById(R.id.tvMonedaCambioCM);
        sMonedaCambioCM = (Spinner) findViewById(R.id.spMonedaCambioCM);
        eValorAConvertirCM = (EditText) findViewById(R.id.etValorAConvertirCM);
        convertirCM = (Button) findViewById(R.id.bConvertirCM);
        bTodasMonedasCM = (Button) findViewById(R.id.bTodasMonedasCM);


        // Creamos el Thread ya que de lo contrario da error la consulta a la URL
        new Thread(new Runnable() {

            //declaro variable de la clase que cree que hereda de SQLiteOpenHelper
            AdminBBDD adminCM;
            Cursor cursorCM;
            SQLiteDatabase bbddCM;
            boolean entro;


            @Override
            public synchronized void run() {
                try {
                    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++comienzo BBDD
                    //inicializo la variable de la clase que cree para la conexion a bbdd, hereda SQLiteOpenHelper
                    adminCM = new AdminBBDD(MainActivity.this, "Administration", null,1);

                    //abro la bbdd
                    bbddCM= adminCM.getReadableDatabase();
                    ConnectivityManager cmCM=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                    //guardo todas las conexiones posibles en un array
                    NetworkInfo[] netInfoCM=cmCM.getAllNetworkInfo();

                    //recorro ese array
                    for(NetworkInfo ni: netInfoCM) {

                        //comprobacion por consola: tipo de conexion y si esta o no conectada
                        String typeName = ni.getTypeName();
                        Boolean conected = ni.isConnected();
                        //System.out.println("***************TIPO DE CONEXION: " + typeName + " -CONECTADO: " + conected+" ******************");

                        //desde el ordenador, tengo conexion con cable: MOBILE, con este if entra en isConnected
                       // if (ni.getTypeName().equalsIgnoreCase("MOBILE"))

                            //desde el ordenador este tipo de conexion me va a dar false(entra en el else), pero desde el movil me va a dar true
                       if(ni.getTypeName().equalsIgnoreCase("WIFI"))
                            {
                            if (ni.isConnected()) {
                                //si cumple con el tipo de conexion que le digo y esta conectado
                               // System.out.println("************USTED ESTA USANDO INTERNET***********");

                                //le digo que entre al codigo que consulta internet y llena los arrays del codigo XML
                                entro=true;


                            } else {
                                //si cumple con el tipo de conexion que le digo y NO esta conectado
                                //  System.out.println("****************ENTRO EN MODO SIN CONEXION****************");
                                //cuento la cantidad de informacion de la tabla monedas, para incializar los arrays
                                final String consultaContador = "select count(*) from monedas";
                                int jo = (int) DatabaseUtils.longForQuery(bbddCM, consultaContador, null);
                                // System.out.println("********************************COUNT: " + jo);
                                //consulto la tabla monedas y la guardo en un cursor
                                String queryCM = "select * from monedas";
                                cursorCM = bbddCM.rawQuery(queryCM, null);
                                //inicializo arrays con ese numero de elementos que me devolvio el count de la ddbb
                                inicialesCM = new String[jo];
                                ratioCM = new String[jo];
                                iniciales_ratioCM= new String[jo];
                                //contador para llenar los arrays
                                int i=0;
                                //mientras haya algo en el cursor
                                while (cursorCM.moveToNext()) {
                                    //recupero lo que hay en currency y cursor
                                    @SuppressLint("Range") String currencyCM = cursorCM.getString(cursorCM.getColumnIndex("currency"));
                                    @SuppressLint("Range") String rateCM = cursorCM.getString(cursorCM.getColumnIndex("ratio"));
                                    // System.out.println("**************Comprobacion consulta de tabla (cursor) modo SIN conexion: Currency " + currencyCM + " Ratio " + rateCM);
                                    //guardo el valor currency y ratio en los arrays correspondientes en la posicion contador
                                        inicialesCM[i] = currencyCM;
                                        ratioCM[i] = rateCM;
                                        iniciales_ratioCM[i]="Currency: "+currencyCM+" Ratio: "+rateCM;

                                        //incremento contador
                                        i++;
                                }
                                //le digo que entro es false, no entrara a pedirle a internet los datos XML
                                entro=false;

                            }
                            }
                    }
                    bbddCM.close();
                    //*********************************************************************fin BBDD

                    if(entro==false){

                       // System.out.println("*********YA LLENE LOS arrays EN EL MODO SIN CONEXION");
                        toas=true;

                    }else
                        {
                       // System.out.println("*********TENGO QUE LLENAR LOS arrays CON CONEXION Y ACTUALIZAR LA  BBDD");
                        toas=false;

                        //Usamos el DOM para consultar el XML devuelto por la URL y lo
                        //guardamos en el doc
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        Document doc = db.parse(new
                                URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml ").openStream());

                        if (doc == null) {
                            Toast notification = Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT);
                            notification.show();
                        }
                        // Buscamos el "tag" "Cube" ya que es donde está la información. El
                        // resultado lo guardamos en nameList
                        NodeList namelist = (NodeList) doc.getElementsByTagName("Cube");

                        //averiguo el numero de elementos de namelist para poner inicializar los arrays
                        int j = 0;
                        for (int i = 0; i < namelist.getLength(); i++) {
                            Node p = namelist.item(i);
                            Element elemento = (Element) p;
                            String varCurrency = (String) elemento.getAttribute("currency");

                            if(varCurrency!=null && !varCurrency.equals("")){
                                j++;
                            }
                        }
                        // System.out.println("*************cantidad de elementos del  NAMELIST "+j);

                            // ********************************inserto en la BBDD
                        //abro la conexion de la bbdd para insertar los nuevos valores del xml
                        bbddCM= adminCM.getReadableDatabase();
                        bbddCM= adminCM.getWritableDatabase();
                        adminCM.onUpgrade(bbddCM,1,2);//metodo de mi clase creada

                        //inicializo arrays con ese numero de elementos que he traido del xml
                        inicialesCM = new String[j];
                        ratioCM = new String[j];
                        iniciales_ratioCM= new String[j];

                        //vuelvo el contador a cero para reutilizarlo
                        j = 0;

                        // Recorremos toda la lista de valores que poseen el "tag" "Cube"
                        for (int i = 0; i < namelist.getLength(); i++) {
                            //recupero el nodo en esa posicion y el elemento
                            Node p = namelist.item(i);
                            Element elemento = (Element) p;

                            // Visualizamos sólo aquellos elementos que poseen el atributo
                            //"currency informado
                            String varCurrencyCM = (String) elemento.getAttribute("currency");
                            String varRateCM = (String) elemento.getAttribute("rate");

                            if (varCurrencyCM!=null && !varCurrencyCM.equals("")) {
                                //imprimo en consola los valores
                              //  System.out.println("********Comprobacion arrays llenados con conexion al XML Currency: " + varCurrencyCM + " Rate: " +
                                       // varRateCM);
                                //guardo el valor currency y ratio en los arrays correspondientes
                                inicialesCM[j] = varCurrencyCM;
                                ratioCM[j] = varRateCM;
                                iniciales_ratioCM[j]="Currency: "+varCurrencyCM+" Ratio: "+varRateCM;

                                ContentValues registro= new ContentValues();
                                registro.put("id", j);
                                registro.put("currency",varCurrencyCM);
                                registro.put("ratio",varRateCM);
                                bbddCM.insert("monedas",null,registro);
                                j++;
                            }
                        }
                    }
                    bbddCM.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        try {
            while (inicialesCM == null) {
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //los arrays pueden estar llenados consultando la bbdd o consultando internet XML
        //meto en el spinner spMonedaCambioCM el contenido del array iniciales
        ArrayAdapter<String> adaptadorCM = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, inicialesCM);
        sMonedaCambioCM.setAdapter(adaptadorCM);
    }


    public void convertirMoneda(View view) {
        if(toas==true){
            Toast.makeText(MainActivity.this,"USTED NO ESTA USANDO INTERNET, los datos podrian no estar actualizados", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(MainActivity.this,"USTED ESTA USANDO INTERNET", Toast.LENGTH_SHORT).show();
        }

        //guardo la posicion del item elegido en los spinner
        sMonedaCambioCM = (Spinner) findViewById(R.id.spMonedaCambioCM);
        int posicionSpinerCM = sMonedaCambioCM.getSelectedItemPosition();

       // System.out.println("*******POSICION SPINNER " + posicionSpinerCM);
            double resultadoCM;
            double valueCM;
            //accedo al textView donde imprimo el resultado
            tvConvertidoCM = (TextView) findViewById(R.id.tvConvertidoCM);

            //si el usuario no introdujo ningun valor, sale un cartel pidiendo que lo haga y borra lo que puede haber en el cuadro de resultado
            if (eValorAConvertirCM.getText().toString().isEmpty()) {

                Toast.makeText(this, "Introducir valor a convertir", Toast.LENGTH_SHORT).show();
                tvConvertidoCM.setText("");

            } else {
                //paso lo que escribio el usuario de String a Double
                double cantidadAcambiarCM = parseDouble(eValorAConvertirCM.getText().toString());

                //compruebo que el usuario no haya puesto 0
                if (cantidadAcambiarCM > 0) {
                    //uso la posicion del spinner elegida en iniciales para consultar el ratio en esa posicion
                    //lo paso a FLoat para poder operar
                    float ratioElegido = Float.parseFloat((ratioCM[posicionSpinerCM]));
                    resultadoCM = cantidadAcambiarCM * ratioElegido;

                  //  System.out.println("*************************RESULTADO " + resultadoCM);

                    //formateo para que me salgan impresos solo dos decimales
                    Formatter form=new Formatter();
                    form.format("%.2f",resultadoCM);
                    tvConvertidoCM.setText(""+ form.toString());

                } else {
                    Toast.makeText(this, "Error al convertir", Toast.LENGTH_SHORT).show();
                }
            }
    }

    public void MostrasTodas(View view){

        Intent intent= new Intent(this, MostrarDivisas.class);
        startActivity(intent);
   }

}