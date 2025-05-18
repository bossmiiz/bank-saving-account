import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  constructor(private http: HttpClient) {}

  createAccount(request: any): Observable<any> {
    return this.http.post(`${environment.apiUrl}/api/accounts`, request);
  }

  getAccounts(): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/api/accounts`);
  }

  getAccount(accountNumber: string): Observable<any> {
    return this.http.get<any>(
      `${environment.apiUrl}/api/accounts/${accountNumber}`
    );
  }

  deposit(accountNumber: string, amount: number): Observable<any> {
    return this.http.post(
      `${environment.apiUrl}/api/accounts/${accountNumber}/deposit`,
      { amount }
    );
  }

  transfer(accountNumber: string, request: any): Observable<any> {
    return this.http.post(
      `${environment.apiUrl}/api/accounts/${accountNumber}/transfer`,
      request
    );
  }

  getTransactions(accountNumber: string): Observable<any[]> {
    return this.http.get<any[]>(
      `${environment.apiUrl}/api/transactions/account/${accountNumber}`
    );
  }

  getTransactionsByDate(
    accountNumber: string,
    date: string,
    pin: string
  ): Observable<any[]> {
    const payload = { date, pin };
    console.log('Statement request payload:', payload);
    return this.http.post<any[]>(
      `${environment.apiUrl}/api/transactions/${accountNumber}/statement`,
      payload
    );
  }
}
