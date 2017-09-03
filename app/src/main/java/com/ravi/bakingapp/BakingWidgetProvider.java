package com.ravi.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.ravi.bakingapp.utils.JsonKeys;
import com.ravi.bakingapp.utils.PreferencesUtils;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidgetProvider extends AppWidgetProvider {

    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
//        Log.v(Constants.TAG, "updateAppWidget()");
        RemoteViews rv = getRecipeListRemoteView(context);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_widget_ingredientsList);
    }

    /**
     * Creates and returns the RemoteViews to be displayed in the ListView widget
     *
     * @param context The context
     * @return The RemoteViews for the ListView widget
     */
    private static RemoteViews getRecipeListRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);
        // Set the ListWidgetService intent to act as the adapter for the ListView
        PreferencesUtils pref = new PreferencesUtils(context);
        views.setTextViewText(R.id.tv_title, pref.getData(JsonKeys.NAME_KEY));
        Intent intent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.lv_widget_ingredientsList, intent);
        // Set the PendingIntent template in getRecipeListRemoteView to launch MainActivity
        // Set the MainActivity intent to launch when clicked
        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.lv_widget_ingredientsList, appPendingIntent);
        // Handle empty recipe
        views.setEmptyView(R.id.lv_widget_ingredientsList, R.id.empty_view);
        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care of updating the widgets UI
//        Log.v(Constants.TAG, "Updating widget provider onUpdate()");
        WidgetUpdateService.startActionUpdateRecipeWidget(context);
    }



    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

