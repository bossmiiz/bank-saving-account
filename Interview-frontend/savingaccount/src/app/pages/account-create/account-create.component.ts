import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { AccountService } from '../../services/account.service';
import { CommonModule } from '@angular/common';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-account-create',
  templateUrl: './account-create.component.html',
  styleUrl: './account-create.component.scss',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
})
export class AccountCreateComponent implements OnInit {
  createForm: FormGroup;
  loading = false;
  errorMsg = '';
  successMsg = '';
  newAccountNumber = '';
  isTeller = false;

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private authService: AuthService,
    private router: Router
  ) {
    this.createForm = this.fb.group({
      citizenId: [''],
      thaiName: [''],
      englishName: [''],
      initialDeposit: [''],
    });
  }

  ngOnInit() {
    this.isTeller = this.authService.isTeller();
    if (!this.isTeller) {
      // ถ้าไม่ใช่ teller ให้ disable ช่องที่ไม่เกี่ยวข้อง
      this.createForm.get('citizenId')?.disable();
      this.createForm.get('thaiName')?.disable();
      this.createForm.get('englishName')?.disable();
    }
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }

  goToDeposit() {
    this.router.navigate(['/deposit']);
  }

  onSubmit() {
    this.errorMsg = '';
    this.successMsg = '';
    if (this.createForm.valid) {
      this.loading = true;
      // ส่งเฉพาะ field ที่จำเป็นตาม role
      const req = this.isTeller
        ? this.createForm.getRawValue()
        : { initialDeposit: this.createForm.value.initialDeposit };
      this.accountService
        .createAccount(req)
        .pipe(
          catchError((err) => {
            this.errorMsg = err?.error?.message || 'สร้างบัญชีไม่สำเร็จ';
            this.loading = false;
            return of(null);
          })
        )
        .subscribe((res) => {
          if (res) {
            this.successMsg =
              'สร้างบัญชีสำเร็จ! เลขบัญชี: ' + res.accountNumber;
            this.newAccountNumber = res.accountNumber;
            this.createForm.reset();
          }
          this.loading = false;
        });
    } else {
      this.createForm.markAllAsTouched();
    }
  }
}
