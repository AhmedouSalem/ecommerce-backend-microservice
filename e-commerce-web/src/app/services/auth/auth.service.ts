import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { UserStorageService } from '../storage/user-storage.service';
import {environment} from "../../../environments/environment";

const BASIC_URL = environment.backendHost;

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient,private userStorageService: UserStorageService) { }
  register(signupRequest: any): Observable<any> {
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/json');
      // .set('Authorization', 'Bearer ecom-token'); // à ajouter

    return this.http.post(
      BASIC_URL + "user-service/sign-up",
      signupRequest,
      { headers }
    );
  }


  login(username: string, password: string): any {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      // 'Authorization': 'Bearer ecom-token'
    });

    const body = { username, password };

    return this.http.post(BASIC_URL + "user-service/authenticate", body, { headers, observe: 'response' })
      .pipe(
        map((res) => {
          console.log('Login response:', res);
          const token = res.headers.get('authorization')?.substring(7);
          const user = res.body;
          if (token && user) {
            this.userStorageService.saveToken(token);
            this.userStorageService.saveUser(user);
            return true;
          }
          return false;
        })
      );
  }


  getOrderByTrackingId(trackingId:number):Observable<any>{
    return this.http.get(BASIC_URL+`cart-service/api/customer/command/tracking/order/${trackingId}`);
  }

}
