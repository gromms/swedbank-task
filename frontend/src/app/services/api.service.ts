import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Payment } from '../home/home.component';

const httpOptions = {
  headers: new HttpHeaders({ 
    'Access-Control-Allow-Origin':'*',
    'Content-Type':'application/json',
    'Accept':'application/json',
  })
};

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  constructor(private http: HttpClient) { }

  apiUrl = 'http://localhost:8889/api/'

  getAccounts() {
    return this.http.get<Account[]>(this.apiUrl + "accounts");
  }

  getCurrencies() {
    return this.http.get<string[]>(this.apiUrl + "currencies");
  }

  addAccount(account: Account) {
    return this.http.post<Account>(this.apiUrl + "accounts", account);
  }

  editAccount(account: Account) {
    return this.http.patch<Account>(this.apiUrl + "accounts", account);
  }

  deleteAccount(accountId: number) {
    return this.http.delete(this.apiUrl + "accounts/" + accountId);
  }

  submitPayment(payment: Payment) {
    return this.http.post(this.apiUrl + "payment", { sourceId: payment.sourceAccount.id, targetId: payment.targetAccount.id, amount: payment.amount }, httpOptions);
  }
}

export interface Account {
  id: number,
  name: string,
  currency: string,
  balance: string,
}