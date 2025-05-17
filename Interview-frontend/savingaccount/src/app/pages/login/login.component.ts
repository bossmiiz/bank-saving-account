import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { RouterModule } from '@angular/router';
import { AccountService } from '../../services/account.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  errorMsg = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private accountService: AccountService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onSubmit() {
    this.errorMsg = '';
    if (this.loginForm.valid) {
      this.loading = true;
      console.log('Attempting login with:', this.loginForm.value.email);
      this.authService
        .login(this.loginForm.value.email, this.loginForm.value.password)
        .pipe(
          catchError((err) => {
            console.error('Login error:', err);
            this.errorMsg =
              err?.error?.message || 'อีเมลหรือรหัสผ่านไม่ถูกต้อง';
            this.loading = false;
            return of(null);
          })
        )
        .subscribe((res) => {
          console.log('Login response:', res);
          if (res) {
            console.log('User roles:', res.roles);
            if (res.roles.includes('ROLE_TELLER')) {
              console.log('User is TELLER, redirecting to /account-create');
              this.router.navigateByUrl('/account-create');
            } else if (res.roles.includes('ROLE_CUSTOMER')) {
              console.log('User is CUSTOMER, redirecting to /account-info');
              this.router.navigateByUrl('/account-info');
            } else {
              console.log('User has no valid role');
              this.errorMsg = `Can't find account in the system. Please contact the teller to open a new account.`;
              this.loading = false;
            }
          }
          this.loading = false;
        });
    } else {
      this.loginForm.markAllAsTouched();
    }
  }
}
