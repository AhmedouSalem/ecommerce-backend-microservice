import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserStorageService } from '../../services/storage/user-storage.service';

import {environment} from "../../../environments/environment";

const BASIC_URL = environment.backendHost;

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(private http: HttpClient) { }

  addCategory(categoryDto: any): Observable<any> {
    return this.http.post(BASIC_URL + 'category-service/api/admin/category', categoryDto, {
      headers: this.createAuthorizationHeader()
    });
  }

  getAllCategories(): Observable<any> {
    return this.http.get(BASIC_URL + 'category-service/api/admin/categories', {
      headers: this.createAuthorizationHeader()
    });
  }

  addProduct(productDto: any): Observable<any> {
    return this.http.post(BASIC_URL + 'product-service/api/admin/product', productDto, {
      headers: this.createAuthorizationHeader()
    });
  }

  updateProduct(productId:any, productDto: any): Observable<any> {
    return this.http.put(BASIC_URL + `product-service/api/admin/product/${productId}`, productDto, {
      headers: this.createAuthorizationHeader()
    });
  }

  getAllProducts(): Observable<any> {
    return this.http.get(BASIC_URL + 'product-service/api/admin/products', {
      headers: this.createAuthorizationHeader()
    });
  }


  getProductById(productId): Observable<any> {
    return this.http.get(BASIC_URL + `product-service/api/admin/product/${productId}`, {
      headers: this.createAuthorizationHeader()
    });
  }

  getAllProductsByName(name:any): Observable<any> {
    return this.http.get(BASIC_URL + `product-service/api/admin/search/${name}`, {
      headers: this.createAuthorizationHeader()
    });
  }


  deleteProduct(productId: any): Observable<any> {
    return this.http.delete(BASIC_URL + `product-service/api/admin/product/${productId}`, {
      headers: this.createAuthorizationHeader()
    });
  }

  addCoupon(couponDto: any): Observable<any> {
    return this.http.post(BASIC_URL + 'coupon-service/api/admin/coupons', couponDto, {
      headers: this.createAuthorizationHeader()
    });
  }

  getCoupons(): Observable<any> {
    return this.http.get(BASIC_URL + 'coupon-service/api/admin/coupons', {
      headers: this.createAuthorizationHeader()
    });
  }

  getPlacedOrders(): Observable<any> {
    return this.http.get(BASIC_URL + 'order-service/api/admin/placedOrders', {
      headers: this.createAuthorizationHeader()
    });
  }

  changeOrderStatus(orderId: number, status: string): Observable<any> {
    const headers = this.createAuthorizationHeader(); // Créer les en-têtes d'autorisation

    return this.http.put(
      `${BASIC_URL}order-service/api/admin/order/${orderId}/${status}`,
      {},  // Corps de la requête vide si nécessaire (dépend du backend)
      { headers }  // Inclure les en-têtes dans l'objet d'options
    );
  }



  postFAQ(productId:number,faqFto:string): Observable<any> {
    return this.http.post(BASIC_URL + `product-service/api/admin/faq/${productId}`,faqFto, {
      headers: this.createAuthorizationHeader()
    });
  }

  getAnalytics(): Observable<any> {
    return this.http.get(BASIC_URL + 'order-service/api/admin/order/analytics', {
      headers: this.createAuthorizationHeader()
    });
  }

  private createAuthorizationHeader(): HttpHeaders {
    return new HttpHeaders().set(
      'Authorization', 'Bearer ' + UserStorageService.getToken()
    )
  }

}
