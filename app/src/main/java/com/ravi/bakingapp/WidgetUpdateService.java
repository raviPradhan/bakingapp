package com.ravi.bakingapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ravi.bakingapp.utils.Constants;

import static com.ravi.bakingapp.database.RecipesContract.BASE_CONTENT_URI;
import static com.ravi.bakingapp.database.RecipesContract.PATH_RECIPES;
import static com.ravi.bakingapp.database.RecipesContract.RecipeEntry.CONTENT_URI;

public class WidgetUpdateService extends IntentService{

    public static final String ACTION_UPDATE_WIDGET = "com.ravi.bakingapp.action.update_widget";

    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    /**
     * Starts this service to perform UpdatePlantWidgets action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateRecipeWidget(Context context) {
        Log.v(Constants.TAG, "startActionUpdateRecipeWidget()");
        Intent intent = new Intent(context, WidgetUpdateService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        context.startService(intent);
    }

    /**
     * Starts this service to update Recipe action with the given parameters. If
     * the service is already performing a task this action will be queued.
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_WIDGET.equals(action)) {
                handleActionUpdateWidget();
            }
        }
    }

    /**
     * Handle action UpdateWidget in the provided background thread
     */
    private void handleActionUpdateWidget() {
//        Uri PLANT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();
        Cursor cursor = getContentResolver().query(
                CONTENT_URI,
                null,
                null,
                null,
                null);
//        Log.v(Constants.TAG, "" + cursor.getCount());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidgetProvider.class));
        //Now update all widgets
        Log.v(Constants.TAG, "Updating widgets from handleActionUpdateWidget()");
        BakingWidgetProvider.updateRecipeWidgets(this, appWidgetManager, appWidgetIds);
    }
}
