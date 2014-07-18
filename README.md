Sunshine
========

Sunshine App for android


* Progress Notes

1. Setup new project with package name. Might need to be a "module" since that gives full options to customize
2. Project should be a blank activity with a fragment

(fill in other stuff that happened here)

X1. Create a refresh button in the options menu
 - added a new xml menu (see forecastfragment.xml)
 - created a string for the name of the option in strings.xml
    <string name="action_refresh">Refresh</string>
 - add methods in the fragment to populate the menu
    onCreate()
    onCreateOptionsMenu() - actually inflate it here
    onOptionsItemSelected() - set references and create the action when selected
X2. Add a permission for internet access - TODO
