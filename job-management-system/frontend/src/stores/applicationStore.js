import { defineStore } from 'pinia';
import apiClient from '../services/api';
import { useAuthStore } from './authStore'; // To get current user for operations

export const useApplicationStore = defineStore('application', {
  state: () => ({
    applications: [], // Can hold user's applications or applications for a specific position
    currentApplication: null,
    loading: false,
    error: null,
  }),
  getters: {
    // getApplicationById: (state) => (id) => state.applications.find(app => app.id === id),
  },
  actions: {
    async fetchMyApplications() {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      if (!authStore.isAuthenticated) {
        this.error = 'User not authenticated.';
        this.loading = false;
        this.applications = [];
        return;
      }
      try {
        const response = await apiClient.get('/applications/my-applications');
        this.applications = response.data;
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Failed to fetch your applications';
        this.applications = [];
        console.error("Error fetching user's applications:", error);
      } finally {
        this.loading = false;
      }
    },
    async fetchApplicationsForPosition(positionId) {
      this.loading = true;
      this.error = null;
      try {
        // Corrected endpoint based on existing backend controller: /api/v1/applications/position/{positionId}
        const response = await apiClient.get(`/applications/position/${positionId}`);
        this.applications = response.data; // Store these applications, might overwrite user's ones if not careful
      } catch (error) {
        this.error = error.response?.data?.message || error.message || `Failed to fetch applications for position ${positionId}`;
        this.applications = [];
        console.error(`Error fetching applications for position ${positionId}:`, error);
      } finally {
        this.loading = false;
      }
    },
    async fetchApplicationDetails(id) {
        this.loading = true;
        this.error = null;
        try {
            const response = await apiClient.get(`/applications/${id}`);
            this.currentApplication = response.data;
            // Optionally update in list if it exists
            const index = this.applications.findIndex(a => a.id === id);
            if (index !== -1) this.applications[index] = response.data;
            return response.data;
        } catch (error) {
            this.error = error.response?.data?.message || error.message || `Failed to fetch application ${id}`;
            this.currentApplication = null;
            console.error(`Error fetching application ${id}:`, error);
            throw error;
        } finally {
            this.loading = false;
        }
    },
    async createApplication(applicationData) { // { resumeId, positionId, status, notes }
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      if (!authStore.isAuthenticated || !authStore.currentUser?.id) {
        this.error = "User must be logged in to apply.";
        this.loading = false;
        throw new Error(this.error);
      }
      // applicantUserId is set by backend from auth token
      const requestData = {
        ...applicationData,
        // applicantUserId: authStore.currentUser.id, // Not needed, backend handles this
      };
      try {
        const response = await apiClient.post('/applications', requestData);
        // Optionally add to a local list of 'my applications' if this store instance is for that
        // Or trigger a refresh of 'my applications'
        this.currentApplication = response.data; // Set newly created as current
        return response.data;
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Failed to submit application';
         if (error.response?.data?.details) {
             this.error += `: ${error.response.data.details.join(', ')}`;
        }
        console.error("Error creating application:", error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async updateApplicationStatus(id, status, notes = null) { // Added optional notes
      this.loading = true;
      this.error = null;
      try {
        const payload = { status };
        if (notes !== null) payload.notes = notes; // Include notes if provided
        const response = await apiClient.patch(`/applications/${id}/status`, payload);

        // Update in local list
        const index = this.applications.findIndex(app => app.id === response.data.id);
        if (index !== -1) {
          this.applications[index] = response.data;
        }
        if (this.currentApplication && this.currentApplication.id === response.data.id) {
            this.currentApplication = response.data;
        }
        return response.data;
      } catch (error) {
        this.error = error.response?.data?.message || error.message || `Failed to update status for application ${id}`;
        console.error(`Error updating application status ${id}:`, error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async withdrawApplication(id) { // This can be a DELETE or a status update to 'Withdrawn'
      this.loading = true;
      this.error = null;
      try {
        // Option 1: DELETE endpoint
        await apiClient.delete(`/applications/${id}`);
        this.applications = this.applications.filter(app => app.id !== id);
        if (this.currentApplication && this.currentApplication.id === id) {
            this.currentApplication = null;
        }
        // Option 2: Update status to 'Withdrawn'
        // return await this.updateApplicationStatus(id, 'Withdrawn');
      } catch (error) {
        this.error = error.response?.data?.message || error.message || `Failed to withdraw application ${id}`;
        console.error(`Error withdrawing application ${id}:`, error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
  },
});
