import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountService } from '../../services/account.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-account-info',
  templateUrl: './account-info.component.html',
  styleUrl: './account-info.component.scss',
  standalone: true,
  imports: [CommonModule],
})
export class AccountInfoComponent implements OnInit {
  accounts: any[] = [];
  loading = false;
  errorMsg = '';

  constructor(
    private accountService: AccountService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadAccounts();
  }

  loadAccounts() {
    this.loading = true;
    this.errorMsg = '';
    this.accountService
      .getAccounts()
      .pipe(
        catchError((err) => {
          console.error('Error loading accounts:', err);
          this.errorMsg = 'Unable to load account information';
          return of([]);
        })
      )
      .subscribe((accounts) => {
        this.accounts = accounts;
        this.loading = false;
      });
  }

  goToTransfer() {
    this.router.navigate(['/transfer']);
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
