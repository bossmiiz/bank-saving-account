import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  /**
   * จัดการการ submit form
   */
  onSubmit(): void {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;
      this.login(email, password);
    }
  }

  /**
   * เข้าสู่ระบบ
   */
  private login(email: string, password: string): void {
    this.authService.login(email, password).subscribe({
      next: () => {
        this.handleLoginSuccess();
      },
      error: (error) => {
        this.handleLoginError(error);
      },
    });
  }

  /**
   * จัดการเมื่อเข้าสู่ระบบสำเร็จ
   */
  private handleLoginSuccess(): void {
    // reload user from localStorage ทันทีหลัง login (ป้องกัน race condition)
    this.authService['loadStoredUser']();
    // debug log
    console.log('Current user after login:', this.authService['currentUserSubject'].value);
    if (this.authService.isTeller()) {
      console.log('Redirecting to /new-account');
      this.router.navigate(['/new-account']);
    } else if (this.authService.isCustomer()) {
      console.log('Redirecting to /transfer');
      this.router.navigate(['/transfer']);
    } else {
      console.log('Redirecting to /register');
      this.router.navigate(['/register']);
    }
  }

  /**
   * จัดการเมื่อเข้าสู่ระบบไม่สำเร็จ
   */
  private handleLoginError(error: any): void {
    this.errorMessage = error.error?.message || 'เกิดข้อผิดพลาดในการเข้าสู่ระบบ';
  }

  /**
   * ไปยังหน้าลงทะเบียน
   */
  goToRegister(): void {
    this.router.navigate(['/register']);
  }
} 