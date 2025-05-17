import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, of } from 'rxjs';
import { environment } from '../../environments/environment';

// เพิ่ม interface LoginResponse
export interface LoginResponse {
  token: string;
  roles: string[];
}

export interface User {
  token: string;
  roles: string[];
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadStoredUser();
  }

  /**
   * เข้าสู่ระบบ
   */
  login(email: string, password: string): Observable<LoginResponse> {
    console.log('AuthService: Attempting login for email:', email);
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/auth/login`, {
        email,
        password,
      })
      .pipe(
        tap((response) => {
          console.log('AuthService: Login successful, response:', response);
          this.storeUser(response);
        })
      );
  }

  /**
   * ลงทะเบียน
   */
  register(userData: any): Observable<any> {
    return this.http.post(`${environment.apiUrl}/auth/register`, userData);
  }

  /**
   * ออกจากระบบ
   */
  logout(): void {
    this.clearStoredUser();
  }

  /**
   * ตรวจสอบว่าล็อกอินอยู่หรือไม่
   */
  isAuthenticated(): boolean {
    return !!this.currentUserSubject.value;
  }

  /**
   * ตรวจสอบว่าเป็น TELLER หรือไม่
   */
  isTeller(): boolean {
    return (
      this.currentUserSubject.value?.roles.includes('ROLE_TELLER') || false
    );
  }

  /**
   * ตรวจสอบว่าเป็น CUSTOMER หรือไม่
   */
  isCustomer(): boolean {
    return (
      this.currentUserSubject.value?.roles.includes('ROLE_CUSTOMER') || false
    );
  }

  /**
   * ดึง token
   */
  getToken(): string | null {
    return this.currentUserSubject.value?.token || null;
  }

  /**
   * โหลดข้อมูลผู้ใช้จาก localStorage
   */
  private loadStoredUser(): void {
    const token = localStorage.getItem('token');
    const roles = localStorage.getItem('roles');
    if (token && roles) {
      this.currentUserSubject.next({
        token,
        roles: JSON.parse(roles),
      });
    }
  }

  /**
   * เก็บข้อมูลผู้ใช้ลง localStorage
   */
  private storeUser(response: LoginResponse): void {
    console.log('AuthService: Storing user data:', response);
    localStorage.setItem('token', response.token);
    localStorage.setItem('roles', JSON.stringify(response.roles));
    this.currentUserSubject.next(response);
    console.log(
      'AuthService: Current user after login:',
      this.currentUserSubject.value
    );
  }

  /**
   * ล้างข้อมูลผู้ใช้จาก localStorage
   */
  private clearStoredUser(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('roles');
    this.currentUserSubject.next(null);
  }

  /**
   * ตรวจสอบ PIN
   */
  verifyPin(pin: string): Observable<{ valid: boolean }> {
    const token = this.getToken();
    if (!token) {
      return of({ valid: false });
    }
    return this.http.post<{ valid: boolean }>(
      `${environment.apiUrl}/auth/verify-pin`,
      { pin }
    );
  }
}
