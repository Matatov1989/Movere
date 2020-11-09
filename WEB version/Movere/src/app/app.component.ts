import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Movere';
  navBtnHome = 'Home';
  navBtnAbout = 'About Us';
  navBtnMaps = 'Maps';
  navBtnDownload = 'Download';
  navBtnPrivacyPolicy = 'Privacy Policy';
  btnSignUp = 'Sign up';
}
