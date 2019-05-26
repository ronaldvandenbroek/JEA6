import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Kwetter} from '../model/kwetter';
import {Observable} from 'rxjs';
import {AuthenticationService} from './authentication.service';

@Injectable()
export class KwetterService {
  private readonly headers: HttpHeaders;

  constructor(private http: HttpClient, private authenticationService: AuthenticationService) {
    this.headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + this.authenticationService.currentLoginValue.token
    });
  }

  public timeline(): Observable<Kwetter[]> {
    return this.http.get<Kwetter[]>(
      'http://localhost:8080/api/token/secure/kwetter/timeline/' +
      this.authenticationService.currentLoginValue.user.id, {headers: this.headers});
  }

  createKwetter(text: string) {
    const kwetter = new Kwetter();
    kwetter.text = text;

    console.log(kwetter);

    const body = kwetter;
    console.log(body);
    const response = this.http.post<Kwetter>(
      `http://localhost:8080/api/token/secure/kwetter/create/` +
      this.authenticationService.currentLoginValue.user.id, body, {headers: this.headers});
    response.subscribe(data => {
      console.log(data);
    });
    return response;
  }

  searchKwetter(text: string): Observable<Kwetter[]> {
    const body = text;
    console.log(body);
    return this.http.post<Kwetter[]>(`http://localhost:8080/api/token/secure/kwetter/search_for`, body, {headers: this.headers});
  }
}
