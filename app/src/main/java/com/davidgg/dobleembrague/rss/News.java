package com.davidgg.dobleembrague.rss;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * News Activity
 *
 * @author DavidGG
 */
public class News extends Activity implements
        OnSharedPreferenceChangeListener {

    private class UpdateItemList extends
            AsyncTask<Void, Void, ArrayList<RSSItem>> {
        @Override
        protected ArrayList<RSSItem> doInBackground(Void... voids) {
            String section = "";
            switch (currentView) {
                case VISTA_ULTIMAS:
                    break;
                case VISTA_COCHES:
                    section += "/category/coches";
                    break;
                case VISTA_CLASICOS:
                    section += "/category/clasicos";
                    break;
                case VISTA_COMPETICION:
                    section += "/category/competicion";
                    break;
                case VISTA_VIDEOJUEGOS:
                    section += "/category/videojuegos";
                    break;
                case VISTA_MOTOS:
                    section += "/category/motos";
                    break;
                case VISTA_OTROS:
                    section += "/category/otros";
                    break;
                case VISTA_INFORMACIONTECNICA:
                    section += "/category/informacion-tecnica";
                    break;
            }
            return (RSSItem.getRssItems(url + section + "/feed/"));
        }

        @Override
        protected void onPostExecute(ArrayList<RSSItem> newItems) {
            dialog.dismiss();
            if (newItems == null) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(
                        myActivity);
                alertbox.setMessage(getBaseContext().getString(
                        R.string.noticiasConex)
                        + "\n"
                        + getBaseContext().getString(R.string.noticiasConexb));
                alertbox.setNeutralButton("Ok", null);
                alertbox.show();
                return;
            }

            TextView tVMsg = (TextView) findViewById(R.id.TextViewMsg);
            tVMsg.setText("");
            tVMsg.invalidate();

            rssItems.clear();
            rssItems.addAll(newItems);
            iLA.notifyDataSetChanged();
            rssListView.setSelection(0);
            Toast.makeText(getApplicationContext(),
                    getBaseContext().getString(R.string.noticiasRefresh),
                    Toast.LENGTH_LONG).show();
            super.onPostExecute(newItems);
        }

    }

    private final String url = "http://dobleembrague.wordpress.com";

    public static final String PREFS_NAME = "Prefs";

    private ArrayList<RSSItem> rssItems = new ArrayList<RSSItem>();

    private static int currentView = 0;
    private static final int VISTA_ULTIMAS = 0;
    private static final int VISTA_COCHES = 1;
    private static final int VISTA_CLASICOS = 2;
    private static final int VISTA_COMPETICION = 3;
    private static final int VISTA_VIDEOJUEGOS = 4;
    private static final int VISTA_MOTOS = 5;
    private static final int VISTA_OTROS = 6;
    private static final int VISTA_INFORMACIONTECNICA = 7;

    private Activity myActivity = this;
    private ItemListAdapter iLA = null;
    private ProgressDialog dialog = null;
    private Dialog dialogAbout = null;
    private boolean prefActualizarInicio = true;

    private static ScrollView sVSecciones;
    private static TextView tVCoches;
    private static TextView tVClasicos;
    private static TextView tVCompeticion;
    private static TextView tVVideojuegos;
    private static TextView tVMotos;
    private static TextView tVOtros;
    private static TextView tVInformacionTecnica;
    private ListView rssListView;

    static RSSItem selectedRssItem = null;

    private void updateView() {

        switch (currentView) {
            case VISTA_ULTIMAS:
                sVSecciones.setVisibility(View.GONE);
                return;
            case VISTA_COCHES:
                updateViewStateON(tVCoches);
                break;
            case VISTA_CLASICOS:
                updateViewStateON(tVClasicos);
                break;
            case VISTA_VIDEOJUEGOS:
                updateViewStateON(tVVideojuegos);
                break;
            case VISTA_MOTOS:
                updateViewStateON(tVMotos);
                break;
            case VISTA_OTROS:
                updateViewStateON(tVOtros);
                break;
            case VISTA_INFORMACIONTECNICA:
                updateViewStateON(tVInformacionTecnica);
                break;
        }
        sVSecciones.setVisibility(View.VISIBLE);
    }

    private void updateViewStateOFF(int VISTA) {
        switch (VISTA) {
            case VISTA_COCHES:
                tVCoches.setBackgroundColor(Color.TRANSPARENT);
                tVCoches.setTextColor(Color.WHITE);
                break;
            case VISTA_CLASICOS:
                tVClasicos.setBackgroundColor(Color.TRANSPARENT);
                tVClasicos.setTextColor(Color.WHITE);
                break;
            case VISTA_COMPETICION:
                tVCompeticion.setBackgroundColor(Color.TRANSPARENT);
                tVCompeticion.setTextColor(Color.WHITE);
                break;
            case VISTA_VIDEOJUEGOS:
                tVVideojuegos.setBackgroundColor(Color.TRANSPARENT);
                tVVideojuegos.setTextColor(Color.WHITE);
                break;
            case VISTA_MOTOS:
                tVMotos.setBackgroundColor(Color.TRANSPARENT);
                tVMotos.setTextColor(Color.WHITE);
                break;
            case VISTA_OTROS:
                tVOtros.setBackgroundColor(Color.TRANSPARENT);
                tVOtros.setTextColor(Color.WHITE);
                break;
            case VISTA_INFORMACIONTECNICA:
                tVInformacionTecnica.setBackgroundColor(Color.TRANSPARENT);
                tVInformacionTecnica.setTextColor(Color.WHITE);
                break;
        }
    }

    private void updateViewStateON(TextView tV) {
        tV.setBackgroundColor(Color.WHITE);
        tV.setTextColor(Color.BLACK);
    }

    private void loadConf() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        prefActualizarInicio = settings.getBoolean("checkboxActualizar", true);
        currentView = Integer
                .parseInt(settings.getString("listCategoria", "0"));
    }

    private void loadRefs() {
        sVSecciones = (ScrollView) findViewById(R.id.ScrollViewSecciones);
        rssListView = (ListView) findViewById(R.id.rssListView);

        tVCoches = (TextView) findViewById(R.id.TextViewCoches);
        tVClasicos = (TextView) findViewById(R.id.TextViewClasicos);
        tVCompeticion = (TextView) findViewById(R.id.TextViewCompeticion);
        tVVideojuegos = (TextView) findViewById(R.id.TextViewVideojuegos);
        tVMotos = (TextView) findViewById(R.id.TextViewMotos);
        tVOtros = (TextView) findViewById(R.id.TextViewOtros);
        tVInformacionTecnica = (TextView) findViewById(R.id.TextViewInformacionTecnica);
    }

    private void share(RSSItem item) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        String contenido = item.getTitle();

        if (contenido != null) {
            contenido = "\"" + item.getTitle() + "\"";
        } else {
            return;
        }

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,
                this.getBaseContext().getString(R.string.noticiasCompartir));
        intent.putExtra(Intent.EXTRA_TEXT, contenido + " "
                + this.getBaseContext().getString(R.string.noticiasCompartirb)
                + " " + item.getLink());
        startActivity(Intent.createChooser(intent,
                getString(R.string.menu_compartir)));
    }

    private void sendSuggestion() {
        String[] address = new String[]{"sugerencias.davidgg@gmail.com", ""};
        String subject = getBaseContext()
                .getString(R.string.noticiasSugerencia);
        String emailtext = getBaseContext().getString(
                R.string.noticiasSugerenciab);

        final Intent emailIntent = new Intent(
                android.content.Intent.ACTION_SEND);

        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, address);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, emailtext);

        startActivity(Intent.createChooser(emailIntent, getBaseContext()
                .getString(R.string.noticiasSugerenciac)));
    }

    public void showAbout() {
        final String urlMarket = "market://search?q=pub:\"DavidGG\"";
        // Configuración del dialogo
        dialogAbout = new Dialog(News.this);
        dialogAbout.setContentView(R.layout.acerca);
        dialogAbout.setTitle(getBaseContext().getString(R.string.menu_acerca));
        dialogAbout.setCancelable(true);

        Button botonOK = (Button) dialogAbout.findViewById(R.id.ButtonOK);
        Button botonMasApps = (Button) dialogAbout
                .findViewById(R.id.ButtonMoreApps);

        // Evento que se lanza para cerrar el dialogo
        botonOK.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                dialogAbout.cancel();
            }
        });

        // Evento que se lanza al hacer clic en el botón del dialogo para ver el
        // market del autor
        botonMasApps.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urlMarket));
                startActivity(intent);
            }
        });
        dialogAbout.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        selectedRssItem = (RSSItem) rssListView
                .getItemAtPosition(info.position);
        switch (item.getItemId()) {
            case R.id.item_visitar_articulo:
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(selectedRssItem.getLink())));
                break;
            case R.id.item_compartir_item:
                share(selectedRssItem);
                break;
        }
        return true;
    }

     @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        loadConf();
        loadRefs();

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        iLA = new ItemListAdapter(this, R.layout.list_item, rssItems);
        rssListView.setAdapter(iLA);

        rssListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View view, int index,
                                    long arg3) {
                selectedRssItem = rssItems.get(index);
                startActivity(new Intent(
                        "com.davidgg.dobleembrague.rss.displayRssItem"));
            }
        });

        updateView();
        if (prefActualizarInicio)
            refressRssList();
        getWindow().setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.rssListView) {
            menu.setHeaderTitle(getBaseContext().getString(
                    R.string.noticiasOpcionesArticulo));
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_item, menu);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String urlYoutube = "http://www.youtube.com/user/DobleEmbrague";
        final String urlFacebook = "http://www.facebook.com/pages/Doble-Embrague/219404697245";
        final String urlTwitter = "http://twitter.com/dobleembrague";

        switch (item.getItemId()) {
            case R.id.item_actualizar:
                refressRssList();
                break;
            case R.id.item_preferencias:
                startActivity(new Intent(
                        "com.davidgg.dobleembrague.rss.preferencias"));
                break;
            case R.id.item_Web:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                break;
            case R.id.item_Facebook:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlFacebook)));
                break;
            case R.id.item_Twitter:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlTwitter)));
                break;
            case R.id.item_Youtube:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlYoutube)));
                break;
            case R.id.item_sugerencias:
                sendSuggestion();
                break;
            case R.id.item_acerca:
                showAbout();
                break;
            case R.id.item_salir:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        if (key.equals("checkboxActualizar")) {
            boolean actualizarInicio = prefs.getBoolean("checkboxActualizar",
                    true);
            editor.putBoolean("checkboxActualizar", actualizarInicio);
        }
        if (key.equals("listCategoria")) {
            String categoriaDefecto = prefs.getString("listCategoria", "0");
            editor.putString("listCategoria", categoriaDefecto);
        }

        if (key.equals("listCategoriaWidget")) {
            String categoriaDefectoWidget = prefs.getString(
                    "listCategoriaWidget", "0");
            editor.putString("listCategoriaWidget", categoriaDefectoWidget);
        }

        if (key.equals("listFrecuenciaWidget")) {
            String actualizacion = prefs.getString("listFrecuenciaWidget",
                    "10800000");
            editor.putString("listFrecuenciaWidget", actualizacion);
        }
        editor.commit();

        if (key.equals("listCategoriaWidget")
                || key.equals("listFrecuenciaWidget")) {
            Intent updateIntent = new Intent(this, WidgetLastNews.class);
            updateIntent.setAction("PreferencesUpdated");
            sendBroadcast(updateIntent);
        }

    }

    private void refressRssList() {
        dialog = ProgressDialog
                .show(this,
                        "",
                        getBaseContext().getString(
                                R.string.noticiasActualizando), true);
        new UpdateItemList().execute();
    }

    public void SeleccionSeccion(View v) {
        updateViewStateOFF(currentView);

        switch (v.getId()) {
            case R.id.TextViewUltimas:
                sVSecciones.setVisibility(View.GONE);
                if (currentView != VISTA_ULTIMAS)
                    currentView = VISTA_ULTIMAS;
                else
                    return;
                break;
            case R.id.TextViewCategorias:
                sVSecciones.setVisibility(View.VISIBLE);
                return;
            case R.id.TextViewCoches:
                updateViewStateON(tVCoches);
                if (currentView != VISTA_COCHES)
                    currentView = VISTA_COCHES;
                else
                    return;
                break;
            case R.id.TextViewClasicos:
                updateViewStateON(tVClasicos);
                if (currentView != VISTA_CLASICOS)
                    currentView = VISTA_CLASICOS;
                else
                    return;
                break;
            case R.id.TextViewCompeticion:
                updateViewStateON(tVCompeticion);
                if (currentView != VISTA_COMPETICION)
                    currentView = VISTA_COMPETICION;
                else
                    return;
                break;
            case R.id.TextViewVideojuegos:
                updateViewStateON(tVVideojuegos);
                if (currentView != VISTA_VIDEOJUEGOS)
                    currentView = VISTA_VIDEOJUEGOS;
                else
                    return;
                break;
            case R.id.TextViewMotos:
                updateViewStateON(tVMotos);
                if (currentView != VISTA_MOTOS)
                    currentView = VISTA_MOTOS;
                else
                    return;
                break;
            case R.id.TextViewOtros:
                updateViewStateON(tVOtros);
                if (currentView != VISTA_OTROS)
                    currentView = VISTA_OTROS;
                else
                    return;
                break;
            case R.id.TextViewInformacionTecnica:
                updateViewStateON(tVInformacionTecnica);
                if (currentView != VISTA_INFORMACIONTECNICA)
                    currentView = VISTA_INFORMACIONTECNICA;
                else
                    return;
                break;
        }
        refressRssList();
    }
}