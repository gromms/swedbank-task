import { Component, OnInit, ViewChild } from '@angular/core';
import { ApiService, Account } from '../services/api.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormControl, Validators, NgForm, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  accounts: Account[] = []
  currencies: string[] = []

  paymentFormGroup: FormGroup;

  selectedTab = new FormControl(0);
  displayedColumns = ["name", "balance", "currency", "options"]
  accountsDataSource = new MatTableDataSource();

  @ViewChild("newPaymentForm", {static: false}) paymentForm: NgForm;
  @ViewChild("newAccountForm", {static: false}) accountForm: NgForm;

  newAccount: Account = {
    id: null,
    name: null,
    currency: null,
    balance: null,
  }

  selectedAccount: Account = {
    id: null,
    name: null,
    currency: null,
    balance: null,
  }

  editedAccount: Account = {
    id: null,
    name: null,
    currency: null,
    balance: null,
  }

  newPayment: Payment = {
    sourceAccount: null,
    targetAccount: null,
    amount: null,
  }

  constructor(
    private apiService: ApiService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit() {
    this.refresh();
  }

  refresh() {
    this.pollAccounts();
    this.pollCurrencies();

    this.newPayment = {
      sourceAccount: null,
      targetAccount: null,
      amount: null,
    }

    this.newAccount = {
      id: null,
      name: null,
      currency: null,
      balance: null,
    }

    this.selectedAccount = {
      id: null,
      name: null,
      currency: null,
      balance: null,
    }
  }

  getSourceAccountCurrency() {
    return this.newPayment.sourceAccount != null ? this.newPayment.sourceAccount.currency : "";
  }

  accountSelected() {
    this.editedAccount = {...this.selectedAccount};
  }

  pollAccounts() {
    this.apiService.getAccounts()
      .subscribe((data: Account[]) => {
        this.accounts =  data;
        this.accountsDataSource = new MatTableDataSource(this.accounts);
      })
  }

  pollCurrencies() {
    this.apiService.getCurrencies()
      .subscribe((data: string[]) => {
        this.currencies = data;      
      })
  }

  editAccount() {
    this.apiService.editAccount(this.editedAccount).subscribe(account => {
      this.snackBar.open("Account successfully updated.", "Close", {
        duration: 2000,
      })
      this.refresh();
    }, error => {
      this.snackBar.open(error.error.message, "Close", {
        duration: 4000,
      })
    })
  }

  addAccount() {
    this.apiService.addAccount(this.newAccount).subscribe(account => {
      this.snackBar.open("Account successfully added.", "Close", {
        duration: 2000,
      })
      this.refresh();
      this.accountForm.resetForm();
    }, error => {
      this.snackBar.open(error.error.message, "Close", {
        duration: 4000,
      })
    })
  }

  submitPayment() {
    this.apiService.submitPayment(this.newPayment).subscribe(data => {
      this.snackBar.open("Payment successful.", "Close", {
        duration: 2000,
      })
      this.refresh();
      this.paymentForm.resetForm();
    }, error => {
      this.snackBar.open(error.error.message, "Close", {
        duration: 4000,
      })
    })
  }

  editButtonClicked(account) {
    this.selectedAccount = account;
    this.accountSelected();
    this.selectedTab.setValue(3);
  }

  deleteButtonClicked(account) {
    this.apiService.deleteAccount(account.id).subscribe(() => {
      this.snackBar.open("Account deleted.", "Close", {
        duration: 2000,
      })
      this.refresh();
    });
  }

  paymentSourceAccountChanged() {
    if(this.newPayment.sourceAccount == this.newPayment.targetAccount) {
      this.newPayment.targetAccount = null;
    }
  }
}

export interface Payment {
  sourceAccount: Account,
  targetAccount: Account,
  amount: number,
}
