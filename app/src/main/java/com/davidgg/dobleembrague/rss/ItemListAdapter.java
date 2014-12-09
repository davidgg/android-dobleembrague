package com.davidgg.dobleembrague.rss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Custom Adapter
 * list_item.xml
 *
 * @author DavidGG
 */
public class ItemListAdapter extends ArrayAdapter<RSSItem> {

    private ArrayList<RSSItem> items;

    public ItemListAdapter(Context context, int textViewResourceId,
                           ArrayList<RSSItem> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
        }
        RSSItem item = items.get(position);
        if (item != null) {
            TextView textTitulo = (TextView) v
                    .findViewById(R.id.TextViewGeneralTitulo);
            TextView textCreadorFecha = (TextView) v
                    .findViewById(R.id.TextViewGeneralAutorFecha);
            if (textTitulo != null) {
                textTitulo.setText(item.getTitle());
            }
            if (textCreadorFecha != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
                String fecha = sdf.format(item.getPubDate());
                textCreadorFecha.setText(this.getContext().getString(
                        R.string.adapterCreador)
                        + " " + item.getCreator() + " " + fecha + "\n");
            }
        }
        return v;
    }
}