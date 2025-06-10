import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api/v1', // Adjust if your backend URL is different
  headers: {
    'Content-Type': 'application/json',
  },
});

// Optional: Interceptors can be added here for request/response handling
// For example, to automatically add an auth token to requests:
// apiClient.interceptors.request.use(config => {
//   const token = localStorage.getItem('token');
//   if (token) {
//     config.headers.Authorization = `Bearer ${token}`;
//   }
//   return config;
// }, error => {
//   return Promise.reject(error);
// });

export default apiClient;

// You can also export individual API functions here if preferred:
// export const getCompanies = () => apiClient.get('/companies');
// export const createCompany = (data) => apiClient.post('/companies', data);
// ... and so on for other entities and operations.
