import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class Header {

  constructor(private router: Router) {}

  logout() {

    // Remove JWT token
    localStorage.removeItem('token');

    // Navigate to login page
    this.router.navigate(['/login']);

  }

}