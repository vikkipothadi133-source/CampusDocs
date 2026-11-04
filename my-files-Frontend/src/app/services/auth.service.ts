import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) {}

   register(fullName: string, email: string, password: string): Observable<any> {
    return this.http.post(this.apiUrl + '/register', { fullName, email, password } ,{ responseType: 'text' });
  }

  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(this.apiUrl + '/login', { email, password });
  }
}

