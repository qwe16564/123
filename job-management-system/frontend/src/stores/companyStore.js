import { defineStore } from 'pinia';
import apiClient from '../services/api';
import { useAuthStore } from './authStore'; // To get current user for createCompany

export const useCompanyStore = defineStore('company', {
  state: () => ({
    companies: [],
    currentCompany: null,
    loading: false,
    error: null,
  }),
  getters: {
    getCompanyById: (state) => (id) => {
      return state.companies.find(company => company.id === parseInt(id)) || state.currentCompany;
    },
  },
  actions: {
    async fetchCompanies() {
      this.loading = true;
      this.error = null;
      try {
        const response = await apiClient.get('/companies');
        this.companies = response.data;
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Failed to fetch companies';
        console.error("Error fetching companies:", error);
      } finally {
        this.loading = false;
      }
    },
    async fetchCompany(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await apiClient.get(`/companies/${id}`);
        this.currentCompany = response.data;
        // Also update in the list if it exists, or add if not (though typically detail view is separate)
        const index = this.companies.findIndex(c => c.id === response.data.id);
        if (index !== -1) {
            this.companies[index] = response.data;
        } else {
            // This might not be desired if the list is meant to be a specific snapshot
            // this.companies.push(response.data);
        }
        return response.data; // Return for immediate use
      } catch (error) {
        this.error = error.response?.data?.message || error.message || `Failed to fetch company ${id}`;
        this.currentCompany = null; // Reset on error
        console.error(`Error fetching company ${id}:`, error);
        throw error; // Re-throw for component to handle
      } finally {
        this.loading = false;
      }
    },
    async createCompany(companyData) {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      // Assuming companyData is an object like { name, description, ... }
      // The backend expects createdByUserId in the CreateCompanyRequest DTO.
      const requestData = {
        ...companyData,
        createdByUserId: authStore.currentUser?.id
      };

      if (!requestData.createdByUserId) {
        this.error = "User must be logged in to create a company.";
        this.loading = false;
        console.error("User ID not found for creating company.");
        throw new Error(this.error);
      }

      try {
        const response = await apiClient.post('/companies', requestData);
        this.companies.push(response.data); // Add to local state
        this.currentCompany = response.data; // Set as current
        return response.data; // Return created company
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Failed to create company';
        if (error.response?.data?.details) {
             this.error += `: ${error.response.data.details.join(', ')}`;
        }
        console.error("Error creating company:", error);
        throw error; // Re-throw for component to handle
      } finally {
        this.loading = false;
      }
    },
    async updateCompany(id, companyData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await apiClient.put(`/companies/${id}`, companyData);
        const index = this.companies.findIndex(c => c.id === response.data.id);
        if (index !== -1) {
          this.companies[index] = response.data;
        }
        if (this.currentCompany && this.currentCompany.id === response.data.id) {
            this.currentCompany = response.data;
        }
        return response.data; // Return updated company
      } catch (error) {
        this.error = error.response?.data?.message || error.message || `Failed to update company ${id}`;
         if (error.response?.data?.details) {
             this.error += `: ${error.response.data.details.join(', ')}`;
        }
        console.error(`Error updating company ${id}:`, error);
        throw error; // Re-throw for component to handle
      } finally {
        this.loading = false;
      }
    },
    async deleteCompany(id) {
      this.loading = true;
      this.error = null;
      try {
        await apiClient.delete(`/companies/${id}`);
        this.companies = this.companies.filter(c => c.id !== id);
        if (this.currentCompany && this.currentCompany.id === id) {
            this.currentCompany = null;
        }
      } catch (error) {
        this.error = error.response?.data?.message || error.message || `Failed to delete company ${id}`;
        console.error(`Error deleting company ${id}:`, error);
        throw error; // Re-throw for component to handle
      } finally {
        this.loading = false;
      }
    },
  },
});
