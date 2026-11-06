import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';


@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './login.html',
    styleUrl: './login.css'
})
export class Login {

    email = '';
    password = '';

    errorMessage = '';
    isLoading = false;

    constructor(
        private authService: AuthService,
        private router: Router,
        private cdr: ChangeDetectorRef
    ) { }

    loginUser() {

        this.errorMessage = '';

        if (!this.email.trim() || !this.password) {
            this.errorMessage = 'Please enter email and password.';
            return;
        }
        if (!this.isValidEmail(this.email)) {
            this.errorMessage = 'Please enter a valid email address.';
            return;
        }

        this.isLoading = true;

        this.authService.login(this.email.trim(), this.password)
            .subscribe({

                next: (response) => {

                    this.isLoading = false;

                    console.log('Login successful:', response);

                    this.router.navigate(['/files']);
                },

                error: (error) => {
                    console.log('Login failed');
                    console.log('Status:', error.status);
                    console.log('Message:', error.message);
                    console.log('Error body:', error.error);

                    this.isLoading = false;
                    this.errorMessage = 'Login request failed. Check console.';
                    this.cdr.detectChanges();
                }

            });

    }
    googleLogin(): void {
        window.location.href =
            'http://localhost:8080/oauth2/authorization/google';
    }


    isRegister = false;
    fullName = '';
    confirmPassword = '';

    successMessage = '';

    toggleMode() {
        this.isRegister = !this.isRegister;
        this.errorMessage = '';
        this.successMessage = '';
    }

    registerUser() {

        if (!this.fullName.trim()) {
            this.errorMessage = 'Please enter your full name.';
            return;
        }
        if (!this.isValidEmail(this.email)) {
            this.errorMessage = 'Please enter a valid email address.';
            return;
        }
        if (this.password.length < 6) {
            this.errorMessage = 'Password must be at least 6 characters.';
            return;
        }
        if (this.password !== this.confirmPassword) {
            this.errorMessage = 'Passwords do not match.';
            return;
        }

        this.authService.register(this.fullName, this.email.toLocaleLowerCase(), this.password)
            .subscribe({
                next: () => {
                    this.isRegister = false;
                    this.errorMessage = '';
                    this.successMessage = 'Account created. Please login.';
                    this.cdr.detectChanges();
                },
                error: () => {
                    this.errorMessage = 'Registration failed.';
                    this.cdr.detectChanges();
                }
            });
    }

    isValidEmail(email: string): boolean {
        const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
        return pattern.test(email.trim());
    }


}