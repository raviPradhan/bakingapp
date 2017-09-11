package com.ravi.bakingapp.tasks;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.ravi.bakingapp.IdlingResource.SimpleIdlingResource;
import com.ravi.bakingapp.model.Ingredients;
import com.ravi.bakingapp.model.Recipe;
import com.ravi.bakingapp.model.Steps;
import com.ravi.bakingapp.utils.JsonKeys;
import com.ravi.bakingapp.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class GetRecipesLoader extends AsyncTaskLoader<ArrayList<Recipe>> {

    private ArrayList<Recipe> recipeList;
    private String url;
    SimpleIdlingResource idlingResource;

    public GetRecipesLoader(Context context, String url, @Nullable SimpleIdlingResource idlingResource) {
        super(context);
        this.url = url;
        this.idlingResource = idlingResource;
    }

    @Override
    protected void onStartLoading() {
        /*
        *If the idle state is true, Espresso can perform the next action.
         * If the idle state is false, Espresso will wait until it is true before
         * performing the next action.
        * */
        if (idlingResource != null)
            idlingResource.setIdleState(false);

        if (recipeList != null) {
            // Delivers any previously loaded data immediately
            deliverResult(recipeList);
        } else {
            // Force a new load
            forceLoad();
        }
    }

    @Override
    public ArrayList<Recipe> loadInBackground() {
        ArrayList<Recipe> recipeData = new ArrayList<>();
        try {
            recipeList = new ArrayList<>();
            String response = NetworkUtils.getJson(url);

            JSONArray resultArray = new JSONArray(response);
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject recipeObject = resultArray.getJSONObject(i);
                Recipe recipeItem = new Recipe();
                recipeItem.setId(recipeObject.getInt(JsonKeys.ID_KEY));
                recipeItem.setName(recipeObject.getString(JsonKeys.NAME_KEY));
                recipeItem.setImgPath(recipeObject.getString(JsonKeys.IMAGE_KEY));
                recipeItem.setServes(recipeObject.getInt(JsonKeys.SERVINGS_KEY));

                JSONArray ingredientsArray = recipeObject.getJSONArray(JsonKeys.INGREDIENTS_KEY);
                ArrayList<Ingredients> ingredientsList = new ArrayList<>();
                for (int j = 0; j < ingredientsArray.length(); j++) {
                    JSONObject ingredientsObject = ingredientsArray.getJSONObject(j);
                    Ingredients ingredientsItem = new Ingredients();
                    ingredientsItem.setIngredientName(ingredientsObject.getString(JsonKeys.INGREDIENT_KEY));
                    ingredientsItem.setMeasure(ingredientsObject.getString(JsonKeys.MEASURE_KEY));
                    ingredientsItem.setQuantity(ingredientsObject.getString(JsonKeys.QUANTITY_KEY));
                    ingredientsList.add(ingredientsItem);
                }

                JSONArray stepsArray = recipeObject.getJSONArray(JsonKeys.STEPS_KEY);
                ArrayList<Steps> stepsList = new ArrayList<>();
                for (int k = 0; k < stepsArray.length(); k++) {
                    JSONObject stepsObject = stepsArray.getJSONObject(k);
                    Steps stepsItem = new Steps();
                    stepsItem.setId(stepsObject.getInt(JsonKeys.ID_KEY));
                    stepsItem.setShortDescription(stepsObject.getString(JsonKeys.SHORT_DESCRIPTION_KEY));
                    stepsItem.setDescription(stepsObject.getString(JsonKeys.DESCRIPTION_KEY));
                    stepsItem.setThumbnailUrl(stepsObject.getString(JsonKeys.THUMBNAIL_URL_KEY));
                    stepsItem.setVideoUrl(stepsObject.getString(JsonKeys.VIDEO_URL_KEY));
                    stepsList.add(stepsItem);
                }
                recipeItem.setIngredientsList(ingredientsList);
                recipeItem.setStepsList(stepsList);
                recipeData.add(recipeItem);
            }
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } catch (JSONException jex) {
            jex.printStackTrace();
        }
        return recipeData;
    }

    // deliverResult sends the result of the load, a ArrayList, to the registered listener
    public void deliverResult(ArrayList<Recipe> data) {
        recipeList = data;
        super.deliverResult(data);
    }
}
