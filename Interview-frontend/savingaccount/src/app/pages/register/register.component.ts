import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = false;
  successMsg = '';
  errorMsg = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      citizenId: [
        '',
        [Validators.required, Validators.pattern('^[0-9]{9,13}$')],
      ],
      thaiName: ['', Validators.required],
      englishName: ['', Validators.required],
      pin: ['', [Validators.required, Validators.pattern('^[0-9]{6}$')]],
    });
  }

  onSubmit() {
    this.successMsg = '';
    this.errorMsg = '';
    if (this.registerForm.valid) {
      this.loading = true;
      const formValue = {
        email: this.registerForm.value.email.trim(),
        password: this.registerForm.value.password.trim(),
        citizenId: this.registerForm.value.citizenId.trim(),
        thaiName: this.registerForm.value.thaiName.trim(),
        englishName: this.registerForm.value.englishName.trim(),
        pin: this.registerForm.value.pin.trim(),
      };
      this.authService
        .register(formValue)
        .pipe(
          catchError((err) => {
            this.errorMsg =
              err?.error?.message || 'เกิดข้อผิดพลาด กรุณาลองใหม่';
            this.loading = false;
            return of(null);
          })
        )
        .subscribe((res) => {
          if (res) {
            this.successMsg = 'สมัครสมาชิกสำเร็จ! กำลังพาไปหน้า Login...';
            setTimeout(() => {
              this.router.navigateByUrl('/login');
            }, 1200);
            this.registerForm.reset();
          }
          this.loading = false;
        });
    } else {
      this.registerForm.markAllAsTouched();
    }
  }
}
