# bakingapp
This project shows the recipes and the preparation steps with their videos.

This project shows the following functionalities:
1. Recipes in RecyclerView in linear layout(vertical orientation) when the screen is portrait and a Gridview Orientation when the device is in landscape.

2. The ingredients and the steps of the recipe in a nested scrollview. This becomes a two pane UI when in landscape mode in tablet devices. It remains a single pane in phones (no matter what the orientation is).

3. When the user changes the recipe details they want to see, the widget also changes accordingly.

4. The user can navigate between recipes through previous/next buttons.

5. The user can navigate between recipe steps when in single pane UI design.

6. There is a Idling resource test for the first loading of recipe from network and a click on an item of the RecyclerView.

7. There is a full fledged UI Test generated using Espresso Test Recorder too.
