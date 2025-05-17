import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./pages/register/register.component').then(
        (m) => m.RegisterComponent
      ),
  },
  {
    path: 'account-create',
    loadComponent: () =>
      import('./pages/account-create/account-create.component').then(
        (m) => m.AccountCreateComponent
      ),
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['TELLER'] },
  },
  {
    path: 'transfer',
    loadComponent: () =>
      import('./pages/transfer/transfer.component').then(
        (m) => m.TransferComponent
      ),
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['CUSTOMER'] },
  },
  {
    path: 'statement',
    loadComponent: () =>
      import('./pages/statement/statement.component').then(
        (m) => m.StatementComponent
      ),
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['CUSTOMER'] },
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
