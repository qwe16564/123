import { defineStore } from 'pinia';
import apiClient from '../services/api'; // Import apiClient for potential auth requests

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    user: JSON.parse(localStorage.getItem('user')) || null, // User object (e.g., { id, username, role })
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    currentUser: (state) => state.user,
    userRole: (state) => state.user?.role,
  },
  actions: {
    async loginAction(credentials) {
      try {
        const response = await apiClient.post('/auth/login', credentials);
        const { token, userId, username, email, role } = response.data;

        this.token = token;
        this.user = { id: userId, username, email, role }; // Store user details

        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(this.user));

        apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        return this.user;
      } catch (error) {
        // Remove any potentially stale token/user data
        this.logoutAction(); // Ensures clean state on login failure
        if (error.response && error.response.data && error.response.data.message) {
          throw new Error(error.response.data.message);
        }
        throw new Error(error.message || 'Login failed');
      }
    },
    async registerAction(registrationData) {
      try {
        // Assuming UserController POST /api/v1/users is the registration endpoint
        // It should return the created UserDto (or similar)
        const response = await apiClient.post('/users', registrationData);
        // Depending on backend response, you might auto-login or just confirm registration
        // For now, let's assume it returns the new user, but we don't auto-login.
        return response.data; // Return created user data
      } catch (error) {
        if (error.response && error.response.data && error.response.data.message) {
          // Handle specific validation errors if backend returns them in a structured way
          if (error.response.data.details) {
            throw new Error(`Registration failed: ${error.response.data.message} - ${error.response.data.details.join(', ')}`);
          }
          throw new Error(error.response.data.message);
        }
        throw new Error(error.message || 'Registration failed');
      }
    },
    logoutAction() {
      this.token = null;
      this.user = null;
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      // Remove token from apiClient default headers
      delete apiClient.defaults.headers.common['Authorization'];
      // Optionally, redirect to login page or reload
      // router.push('/login');
    },
    // Action to initialize auth state from localStorage, could be called in App.vue or router
    initializeAuth() {
        const token = localStorage.getItem('token');
        const user = JSON.parse(localStorage.getItem('user'));
        if (token && user) {
            this.token = token;
            this.user = user;
            apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        }
    }
  },
});
