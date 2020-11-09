import { Component, OnInit } from '@angular/core';
import { Router, Params } from '@angular/router';

@Component({
  selector: 'app-about',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.css']
})
export class AboutComponent implements OnInit {

  titlePage: string;
  titleWorkers: string;

  workerVadim: Worker;
  workerNikita: Worker;
  workerYury: Worker;

  constructor() { }

  ngOnInit(): void {

    this.titlePage = 'About Us';
    this.titleWorkers = 'Working on the project';

    this.workerVadim = {
      name: 'Vadim Matatov',
      image: '/assets/img/vadim_movere.jpg',
      profession: 'Engineer PAO AutoVAZ',
      positionMovere: 'Editor of Movere',
      linkedinLink: 'https://www.linkedin.com/in/vadim-matatov-4479b1169/',
      skypeName: 'live:matatvolga',
      gmailAdres: 'matatvolga@gmail.com',
      whatsAppNumber: '+7 964 987 4859'
    };

    this.workerNikita = {
      name: 'Nikita Matatov',
      image: '/assets/img/nikita_movere.jpg',
      profession: 'Student',
      positionMovere: 'Designer of Movere',
      linkedinLink: '',
      skypeName: 'matatovi',
      gmailAdres: 'Docmat63@gmail.com',
      whatsAppNumber: '+972 54 902 6496'
    };

    this.workerYury = {
      name: 'Yury Matatov',
      image: '/assets/img/yura_movere.jpg',
      profession: 'Software Engeneer',
      positionMovere: 'Developer of Movere',
      linkedinLink: 'https://www.linkedin.com/in/yury-matatov-a54030127/',
      skypeName: 'matatov1989',
      gmailAdres: 'Matatov1989@gmail.com',
      whatsAppNumber: '+972 52 646 1150'
    };
  }
}

interface Worker{
  name: string,
  image:string,
  profession: string,
  positionMovere: string,
  linkedinLink: string,
  skypeName: string,
  gmailAdres: string,
  whatsAppNumber: string
}
