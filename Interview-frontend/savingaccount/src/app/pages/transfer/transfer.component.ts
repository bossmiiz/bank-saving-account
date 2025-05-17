import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AccountService } from '../../services/account.service';
import { AuthService } from '../../services/auth.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-transfer',
  templateUrl: './transfer.component.html',
  styleUrl: './transfer.component.scss',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
})
export class TransferComponent implements OnInit {
  transferForm: FormGroup;
  loading = false;
  verifySuccess = false;
  errorMsg = '';
  successMsg = '';
  // ข้อมูลบัญชีของผู้ใช้
  balance = 0;
  accountNumber = '';
  fromName = '';
  // ข้อมูลบัญชีปลายทาง
  toName = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private accountService: AccountService,
    private authService: AuthService
  ) {
    this.transferForm = this.fb.group({
      toAccount: ['', [Validators.required]],
      amount: ['', [Validators.required, Validators.min(1)]],
      pin: ['', [Validators.required, Validators.pattern('^[0-9]{6}$')]],
    });
  }

  ngOnInit() {
    this.getMyAccount();
  }

  getMyAccount() {
    this.accountService
      .getAccounts()
      .pipe(
        catchError(() => {
          this.errorMsg = 'ไม่สามารถโหลดข้อมูลบัญชีได้';
          return of([]);
        })
      )
      .subscribe((accounts) => {
        if (accounts && accounts.length > 0) {
          const acc = accounts[0];
          this.accountNumber = acc.accountNumber;
          this.balance = acc.balance;
          this.fromName = acc.user?.englishName || '-';
        } else {
          this.errorMsg = 'ไม่พบข้อมูลบัญชี';
        }
      });
  }

  verifyAccount() {
    this.errorMsg = '';
    this.successMsg = '';
    const toAccount = this.transferForm.value.toAccount;
    if (!toAccount) {
      this.errorMsg = 'กรุณากรอกเลขบัญชีปลายทาง';
      return;
    }
    this.accountService
      .getAccount(toAccount)
      .pipe(
        catchError(() => {
          this.errorMsg = 'ไม่พบเลขบัญชีปลายทาง';
          this.toName = '';
          this.verifySuccess = false;
          return of(null);
        })
      )
      .subscribe((acc) => {
        if (acc && acc.user) {
          this.toName = acc.user.englishName;
          this.verifySuccess = true;
        } else {
          this.errorMsg = 'ไม่พบเลขบัญชีปลายทาง';
          this.toName = '';
          this.verifySuccess = false;
        }
      });
  }

  onSubmit() {
    this.errorMsg = '';
    this.successMsg = '';
    if (this.transferForm.valid && this.verifySuccess) {
      this.loading = true;
      const req = {
        toAccountNumber: this.transferForm.value.toAccount.trim(),
        amount: this.transferForm.value.amount,
        pin: this.transferForm.value.pin.trim(),
      };
      this.accountService
        .transfer(this.accountNumber, req)
        .pipe(
          catchError((err) => {
            this.errorMsg = err?.error?.message || 'โอนเงินไม่สำเร็จ';
            this.loading = false;
            return of(null);
          })
        )
        .subscribe((res) => {
          if (res) {
            this.successMsg = 'โอนเงินสำเร็จ!';
            this.transferForm.reset();
            this.verifySuccess = false;
            this.getMyAccount(); // refresh balance
          }
          this.loading = false;
        });
    } else {
      this.transferForm.markAllAsTouched();
      if (!this.verifySuccess) {
        this.errorMsg = 'กรุณากด Verify เลขบัญชีปลายทาง';
      }
    }
  }

  viewStatement() {
    this.router.navigate(['/statement']);
  }
}
