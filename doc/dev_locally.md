#Developing and Testing Locally - 08 Feb 2022

The real application (prod) uses an API key to authenticate to the database that is restricted (only works when the app
is deployed in prod - refer to the ApiKey val in CloudFirestore.scala). Therefore, when following the steps to test the
app locally (from the README: sbt fastOptJS followed by ./test_local.sh), it's not really going to work. I mean, the app
will open in http://localhost:5000, but a connection to the database won't be established and posts headlines will not
appear. You'll probably loose precious hours trying to figure out this issue.

It turns out there is an unrestricted API key, which I found in the API calls within Postman (had it not been saved
there, I don't know how I'd retrieve it, as I don't remember where it is in the Firebase UI). Here is the API key:

AIzaSyAZ2dyy-5GGWxvzQbTSRPjJpKED6jeCS-s

Very precious! When working locally, substitute the restricted one for this one and access the app normally at
localhost:5000. If it's not loading, it could be some browser blocking cors or something else, like Chrome at the time
of writing.



###Update on the above from 11 Feb 2022

You can find all API keys at:
Main menu --> APIs and services --> Credentials
