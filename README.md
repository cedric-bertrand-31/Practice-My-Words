
# Practice My Words
Android native application for registering yourself words that you learn

# General presentation
**Practice My Words** is an Android App allowing you to add each new words you have learned in a list where the translation is hidden. You can instantly display the translation if you have forgotten it.

Reinforce the memorization of words of the language you want to learn by consulting regularly the application. You can also choose to receive notifications at certain periods of the day, they will propose you to translate words from your list. The application also offers an activity to test your knowledge.

# Requirements
 - [Android Studio + Android SDK](https://developer.android.com/studio#downloads)
 - Pull backend project (coming soon)

# Installation
The end-point URL must provided to the App source code so user's data can be stored on an external DB.
For that create, in the ```app``` folder, create a file called:
```
backend.properties
```

That must contains the following lines:
```
REQUEST_USER_ID=
REQUEST_WRITE_WORD=
REQUEST_READ_USER_WORDS=
REQUEST_DELETE_WORDS=
REQUEST_UPDATE_MARK_WORDS=
```

Then backend project takes the lead, manage the data and put it into DB.