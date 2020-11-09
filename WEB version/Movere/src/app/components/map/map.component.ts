import { Component, Output, EventEmitter, Input, OnChanges, SimpleChanges} from '@angular/core';
import { Router, Params } from '@angular/router';

import { timer } from 'rxjs';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnChanges {

//  @Input() messages: CometChat.TextMessage[] | null;
//  @Output() sendMessage = new EventEmitter<string>();

  lat = 51.678418;
  lng = 7.809007;

  constructor() { }

  ngOnChanges(changes: SimpleChanges): void {
    console.log({ changes });
  }

  onSendMessage(message: string) {
    this.sendMessage.emit(message);
    console.log({ message });
  }

}
