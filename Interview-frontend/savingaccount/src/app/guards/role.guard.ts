import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: any): boolean {
    const requiredRoles = route.data['roles'] as Array<string>;
    
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return false;
    }

    const hasRole = requiredRoles.some(role => {
      switch (role) {
        case 'TELLER':
          return this.authService.isTeller();
        case 'CUSTOMER':
          return this.authService.isCustomer();
        default:
          return false;
      }
    });

    if (!hasRole) {
      // Redirect to appropriate page based on role
      if (this.authService.isTeller()) {
        this.router.navigate(['/account-create']);
      } else if (this.authService.isCustomer()) {
        this.router.navigate(['/transfer']);
      } else {
        this.router.navigate(['/login']);
      }
      return false;
    }

    return true;
  }
} 