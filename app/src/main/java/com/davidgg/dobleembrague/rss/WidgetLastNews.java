package com.davidgg.dobleembrague.rss;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.widget.RemoteViews;

/**
 * Service Widget
 *
 * @author DavidGG
 */
public class WidgetLastNews extends AppWidgetProvider {

    public static class WidgetAlarm extends BroadcastReceiver {

        private String url = "http://dobleembrague.wordpress.com";
        private String feed;

        private void actualizarWidget(Context context) {
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget_ultima_noticia);
            SharedPreferences settings = context.getSharedPreferences(
                    PREFS_NAME, 0);
            int categoria = Integer.parseInt(settings.getString(
                    "listCategoriaWidget", "0"));

            feed = url;

            switch (categoria) {
                case 0:
                    break;
                case 1:
                    feed += "/category/coches";
                    break;
                case 2:
                    feed += "/category/clasicos";
                    break;
                case 3:
                    feed += "/category/competicion";
                    break;
                case 4:
                    feed += "/category/videojuegos";
                    break;
                case 5:
                    feed += "/category/motos";
                    break;
                case 6:
                    feed += "/category/otros";
                    break;
                case 7:
                    feed += "/category/informacion-tecnica";
                    break;
            }
            feed += "/feed/";

            ConnectivityManager conMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nI = conMgr.getActiveNetworkInfo();

            short cont = 0;
            while ((nI == null || !nI.isConnected()) && cont < 10) {
                SystemClock.sleep(3000);
                conMgr = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                nI = conMgr.getActiveNetworkInfo();
                cont++;
            }

            if ((nI == null || !nI.isConnected()) && cont == 10) {
                return;
            }

            RSSItem item = RSSItem.getLast(feed);
            if (item != null) {
                String titulo = item.getTitle();
                titulo = titulo.length() >= (70 + 3) ? titulo.substring(0, 70)
                        : titulo;
                views.setTextViewText(R.id.TextViewWidgetNoticia, titulo
                        + "...");
            }

            ComponentName thisWidget = new ComponentName(context,
                    WidgetLastNews.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            manager.updateAppWidget(thisWidget, views);

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                actualizarWidget(context);
            } catch (Exception e) {
                try {
                    actualizarWidget(context);
                } catch (Exception ex) {
                    return;
                }
            }
        }
    }

    public static final String PREFS_NAME = "Prefs";

    public void loadConf(Context con) {

        SharedPreferences settings = con.getSharedPreferences(PREFS_NAME, 0);

        String sActualizacion = settings.getString("listFrecuenciaWidget",
                "10800000");
        long actualizacion = Long.parseLong(sActualizacion);

        Intent intent = new Intent(con, WidgetAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(con, 1,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) con
                .getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), actualizacion, pendingIntent);

    }

    @Override
    public void onEnabled(Context con) {
        super.onEnabled(con);
        RemoteViews views = new RemoteViews(con.getPackageName(),
                R.layout.widget_ultima_noticia);

        Intent defineIntent = new Intent();
        defineIntent
                .setClass(con, News.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(con, 0,
                defineIntent, 0);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        ComponentName thisWidget = new ComponentName(con,
                WidgetLastNews.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(con);
        manager.updateAppWidget(thisWidget, views);
    }

    @Override
    public void onReceive(Context con, Intent intent) {
        super.onReceive(con, intent);
        if ("PreferencesUpdated".equals(intent.getAction())) {
            try {
                loadConf(con);
            } catch (Exception e) {
                try {
                    loadConf(con);
                } catch (Exception ex) {
                    return;
                }
            }
        }
        if (intent.getAction() == AppWidgetManager.ACTION_APPWIDGET_DISABLED) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(con, 1,
                    intent, 1);
            AlarmManager alarmManager = (AlarmManager) con
                    .getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        try {
            loadConf(context);
        } catch (Exception e) {
            try {
                loadConf(context);
            } catch (Exception ex) {
                return;
            }
        }
    }
}
