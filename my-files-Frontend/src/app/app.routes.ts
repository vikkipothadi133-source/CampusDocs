import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Files } from './files/files';

export const routes: Routes = [
  {
    path: 'login',
    component: Login
  },
  {
    path: 'files',
    component: Files
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  }
];