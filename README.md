# Movere

---

### Platforms:
- [Android](https://github.com/Matatov1989/Movere-Android);
- [WEB](https://github.com/Matatov1989/Movere-WEB);

---

### Description:
To use the program user must register by verifying Google account. All data of the user stored in Cloud Firestore. The program determines the location of the user and displays the coordinates on a google map so that users can quickly communicate with each other through the built-in chat or plot a route. In chat can send messages and images. When using the “get directions” function, Google Maps opens. The program has a function, which sending alarm SOS to all user in radius 30km (+/- 2km). Every user can to create an Event. Event contains location, title, description, start and stops event. Newly created event receives all users in radios 30km (+/- 2km) from the event location. The new event created is received by all users within a radius of 30km from the event location. An event is deleted after its expiration date through the Cloud Function.

---

### Functions:
- location determination
- output all users to google map
- chat with users via chat
- Get directions to another user;
- View events within a radius of 30 km;
- create an event;
- Send an SOS signal to users within a radius of 32 km.

---

### Tools and Technology:
- Android Studio
- Angular
- Bootstrap
- Firebase

---

### Firebase:
- Authentication
- Cloud Firestore
- Cloud Storage
- Cloud Functions:
	- [sending a welcome email to a newly registered user](https://github.com/firebase/functions-samples/tree/master/quickstarts/email-users);
	- [sending a farewell email to a user who has left the program](https://github.com/firebase/functions-samples/tree/master/quickstarts/email-users);
	- [removing inactive users. The script runs every day on the link](https://github.com/firebase/functions-samples/tree/master/delete-unused-accounts-cron);
	- [remove all user data (personal data, chat, images) from Cloud Firestore and Cloud Store after sign out a user](https://github.com/Matatov1989/Movere-Firebase-Cloud-Functions/tree/master/cleanup%20user%20data);
	- [sending FCM notifications to inactive users. The script runs every day on the link](https://github.com/Matatov1989/Movere-Firebase-Cloud-Functions/tree/master/reminder%20about%20visit);
	- [removing old events (location, time, picture, etc.) from Cloud Firestore and Cloud Store. Every hour the script is launched by the link](https://github.com/Matatov1989/Movere-Firebase-Cloud-Functions/tree/master/remove%20old%20events);
- Cloud Messaging
- Dynamic Links

---
