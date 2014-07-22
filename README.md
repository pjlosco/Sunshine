Sunshine
========

Sunshine App for android


* Progress Notes

1. Setup new project with package name. Might need to be a "module" since that gives full options to customize
2. Project should be a blank activity with a fragment
3. Min SDK is what is the minimum supported OS, target is what should be used to test on.
4. Try not to use AbsoluteLayout when creating UI, and keep it responsive
5. Using an adapter populate a list and insert into the UI using ListView on an XML
6. Created AsyncTask class for getting data
 - override the doInBackground() method to get and analyze the data. Must be used over a network
 - override the onPostExecute() to take in the result of doInBackground and interact with the main thread
 - use AsyncTask.execute() to start the task
7. Create a refresh button in the options menu
 - added a new xml menu (see forecastfragment.xml)
 - created a string for the name of the option in strings.xml
    <string name="action_refresh">Refresh</string>
 - add methods in the fragment to populate the menu
    onCreate()
    onCreateOptionsMenu() - actually inflate it here
    onOptionsItemSelected() - set references and create the action when selected
8. Add a permission for internet access
 - <uses-permission android:name="android.permission.INTERNET"/>
9. Create JSON manager/parser
10. Update local data from JSON
11. Create setOnItemClickListener statements within onCreateView to have actions when an item in the list is clicked
12. Using a Toast is an easy way to get started and confirm its acting right
13. Using an Intent is the way to connect to other Activities.
14. Create a new activity by generating files within Android Studio, new Blank Activity with Fragment. This will update the manifest automatically
15. Connect intent with Activity
 - Intent intent = new Intent(getActivity(),DetailActivity.class).putExtra(Intent.EXTRA_TEXT, "Something useful");
 - startActivity(intent);
16. Create layout for new activity. res > layout > activity_detail.xml, fragment_detail.xml
17. Update intent with useful data to populate into new activity
18. Update new activity with importing the intent during onCreateView and apply to an object that was added in the XML
 - String forecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
 - ((TextView) rootView.findViewById(R.id.detail_text)).setText(forecastStr);
19.