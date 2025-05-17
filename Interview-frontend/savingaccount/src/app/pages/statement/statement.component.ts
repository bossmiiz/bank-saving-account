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
  selector: 'app-statement',
  templateUrl: './statement.component.html',
  styleUrl: './statement.component.scss',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
})
export class StatementComponent implements OnInit {
  pinForm: FormGroup;
  loading = false;
  errorMsg = '';
  statementData: any[] = [];
  showTable = false;
  accountNumber = '';
  today = new Date();

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private authService: AuthService,
    private router: Router
  ) {
    this.pinForm = this.fb.group({
      pin: ['', [Validators.required, Validators.pattern(/^\d{4}$|^\d{6}$/)]],
    });
  }

  ngOnInit() {
    // สมมติว่ามีบัญชีเดียว ดึงบัญชีแรก
    this.accountService.getAccounts().subscribe((accounts) => {
      if (accounts && accounts.length > 0) {
        this.accountNumber = accounts[0].accountNumber;
      }
    });
  }

  onSubmit() {
    this.errorMsg = '';
    if (this.pinForm.valid && this.accountNumber) {
      this.loading = true;
      // สมมติ verify PIN ผ่าน AuthService (mock)
      this.authService
        .verifyPin(this.pinForm.value.pin)
        .pipe(
          catchError((err) => {
            this.errorMsg = 'PIN ไม่ถูกต้อง';
            this.loading = false;
            return of(null);
          })
        )
        .subscribe((res) => {
          if (res && res.valid) {
            // ดึง statement เฉพาะวันปัจจุบัน (ส่ง pin ไปด้วย)
            const dateStr = this.today.toISOString().slice(0, 10);
            this.accountService
              .getTransactionsByDate(
                this.accountNumber,
                dateStr,
                this.pinForm.value.pin
              )
              .pipe(
                catchError((err) => {
                  this.errorMsg =
                    err?.error?.message || 'ไม่พบข้อมูล statement';
                  this.loading = false;
                  return of([]);
                })
              )
              .subscribe((data) => {
                this.statementData = data || [];
                this.showTable = true;
                this.loading = false;
              });
          } else {
            this.errorMsg = 'PIN ไม่ถูกต้อง';
            this.loading = false;
          }
        });
    } else {
      this.pinForm.markAllAsTouched();
    }
  }

  goBack() {
    this.router.navigateByUrl('/login');
  }
}
