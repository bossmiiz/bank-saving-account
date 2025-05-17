import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { AccountService } from '../../services/account.service';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-deposit',
  templateUrl: './deposit.component.html',
  styleUrl: './deposit.component.scss',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
})
export class DepositComponent {
  depositForm: FormGroup;
  loading = false;
  errorMsg = '';
  successMsg = '';

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private authService: AuthService
  ) {
    this.depositForm = this.fb.group({
      accountNumber: ['', [Validators.required, Validators.pattern(/^\d{7}$/)]],
      amount: ['', [Validators.required, Validators.min(1)]],
    });
  }

  isTeller(): boolean {
    return this.authService.isTeller();
  }

  onSubmit() {
    this.errorMsg = '';
    this.successMsg = '';
    if (!this.isTeller()) {
      this.errorMsg = 'Only TELLER can deposit money';
      return;
    }
    if (this.depositForm.valid) {
      this.loading = true;
      const { accountNumber, amount } = this.depositForm.value;
      this.accountService.deposit(accountNumber, +amount).subscribe({
        next: (res) => {
          this.successMsg = 'ฝากเงินสำเร็จ!';
          this.depositForm.reset();
          this.loading = false;
        },
        error: (err) => {
          this.errorMsg = err?.error?.message || 'ฝากเงินไม่สำเร็จ';
          this.loading = false;
        },
      });
    } else {
      this.depositForm.markAllAsTouched();
    }
  }
}
