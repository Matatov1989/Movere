import { Component, OnInit } from '@angular/core';
import { Router, Params } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  titleNewApp: string;
  descNewApp: string;
  tetleRegistration: string;
  descRegistration: string;
  titleNavigation: string;
  descNavigation: string;
  titleCommunication: string;
  descCommunication: string;
  titleNews: string;
  descNews: string;
  titleHelp: string;
  descHelp: string;
  titleTotal: string;
  descTotal: string;
  titleDowload: string;
  descDowload: string;
  btnPrivacyPolicy: string;

  constructor() { }

  ngOnInit(): void {
    this.titleNewApp = 'New Movere App';
    this.descNewApp = 'it is a worldwide community of lovers of light and heavy vehicles, and most importantly, it is an opportunity to find new friends of common interest and quickly seek help if something happened to you on the road!';
    this.tetleRegistration = 'Registration';
    this.descRegistration = 'Registration and use of the program are FREE! When registering, you do not need to remember a new username and password. Movere uses your Google account to log in.';
    this.titleNavigation = 'Navigation';
    this.descNavigation = 'Movere determines your location and displays it on a Google map in order to unite all motor vehicle enthusiasts into. This makes it possible to see on the map the closest users, as well as users of other cities and countries and get directions to another user.';
    this.titleCommunication = 'Communication';
    this.descCommunication = 'Movere uses your phone’s Internet connection (4G / 3G / 2G / EDGE or WiFi, if available), for easy and quick communication with any user through the chat provided in the program. If there is no connection or the phone is disconnected, Movere services will deliver you all the missed messages the next time the program starts.';
    this.titleNews = 'News';
    this.descNews = 'Movere will not let you miss events related to cars: exhibitions, parades, races and all kinds of shows. All events are available at the user s location within a radius of 30 km. Where does the news come from? Everything is very simple! You, as a user, and you are familiar with a certain event, you can create it by filling out a small form of creating an event.';
    this.titleHelp = 'Help';
    this.descHelp = 'The main feature of Movere is that in case of any problems on the road, with good telephone connection, can ask for help to users (community) of Movere send an SOS signal to all users within a radius of 30 km.';
    this.titleTotal = 'Total';
    this.descTotal = 'In total, we have a simple social program that allows us to be in touch with the news, make new friends both in your own city and around the world. In the event of a difficult situation, you can quickly and easily seek help from our Movere community. The Movere system is designed so that the more users, the faster you will be helped and there will be a small chance that you will miss something important related to cars. Therefore, do not be lazy and advise Movere to your   best friend, or share the link in the news feed of social networks!';
    this.titleDowload = 'Dowload Movere';
    this.descDowload = 'for Android';
    this.btnPrivacyPolicy = 'Privacy Policy';
  }
}
