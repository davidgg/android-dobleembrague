package com.davidgg.dobleembrague.rss;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * RSS Item Activity
 *
 * @author DavidGG
 */
public class RSSItemDisplayer extends Activity {
    final String url = "http://dobleembrague.wordpress.com";

    private void share() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        RSSItem selectedRssItem = News.selectedRssItem;
        String contenido = selectedRssItem.getTitle();

        if (contenido != null) {
            contenido = "\"" + selectedRssItem.getTitle() + "\"";
        } else {
            return;
        }

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,
                this.getBaseContext().getString(R.string.noticiasCompartir));
        intent.putExtra(Intent.EXTRA_TEXT, contenido + " "
                + this.getBaseContext().getString(R.string.noticiasCompartirb)
                + " " + selectedRssItem.getLink());
        startActivity(Intent.createChooser(intent,
                getString(R.string.menu_compartir)));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rss_item_displayer);

        RSSItem selectedRssItem = News.selectedRssItem;
        TextView textTitulo = (TextView) findViewById(R.id.titleTextView);
        TextView textDesc = (TextView) findViewById(R.id.contentTextView);
        TextView textFechaAutor = (TextView) findViewById(R.id.TextViewFechaAutor);

        if (textTitulo != null) {
            textTitulo.setText(selectedRssItem.getTitle());
        }
        if (textFechaAutor != null) {
            String fecha = "";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy '- 'HH:MM");
            fecha = "" + sdf.format(selectedRssItem.getPubDate());
            textFechaAutor.setText("\n"
                    + this.getBaseContext().getString(R.string.adapterCreador)
                    + " " + selectedRssItem.getCreator() + " - " + fecha
                    + "\n\n");
        }
        if (textDesc != null) {
            String desc = selectedRssItem.getDescription();

            textDesc.setText(desc
                    + "[...]\n\n"
                    + this.getBaseContext()
                    .getString(R.string.itemDisplayerMas)
                    + selectedRssItem.getLink());
        }
        getWindow().setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem item = menu.add(0, 0, 0, R.string.menu_compartir);
        item.setIcon(android.R.drawable.ic_menu_share);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                share();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}