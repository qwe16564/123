import { defineStore } from 'pinia';
import apiClient from '../services/api';
import { useAuthStore } from './authStore'; // To get current user for operations

export const useResumeStore = defineStore('resume', {
  state: () => ({
    resumes: [], // List of resumes for the logged-in user
    currentResume: null, // For viewing details, not heavily used for resume
    loading: false,
    error: null,
  }),
  getters: {
    // getResumeById: (state) => (id) => state.resumes.find(r => r.id === id),
  },
  actions: {
    async fetchMyResumes() {
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      if (!authStore.isAuthenticated || !authStore.currentUser?.id) {
        this.error = 'User not authenticated.';
        this.loading = false;
        this.resumes = [];
        return;
      }
      try {
        // Using the new /my-resumes endpoint that infers user from auth token
        const response = await apiClient.get('/resumes/my-resumes');
        this.resumes = response.data;
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Failed to fetch resumes';
        this.resumes = [];
        console.error("Error fetching resumes:", error);
      } finally {
        this.loading = false;
      }
    },
    async uploadResume(formData) { // formData should be a FormData object
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();
      if (!authStore.isAuthenticated) {
          this.error = 'User not authenticated to upload resume.';
          this.loading = false;
          throw new Error(this.error);
      }
      // UserId is implicitly handled by backend via Authentication principal
      // formData should contain 'file' and optionally 'originalFileName'

      try {
        const response = await apiClient.post('/resumes', formData, {
          headers: {
            'Content-Type': 'multipart/form-data', // Axios usually sets this for FormData but good to be explicit
          },
        });
        this.resumes.push(response.data); // Add to local state
        return response.data; // Return created resume DTO
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Failed to upload resume';
        if (error.response?.data?.details) {
             this.error += `: ${error.response.data.details.join(', ')}`;
        }
        console.error("Error uploading resume:", error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async deleteResume(id) {
      this.loading = true;
      this.error = null;
      try {
        await apiClient.delete(`/resumes/${id}`);
        this.resumes = this.resumes.filter(r => r.id !== id);
        if (this.currentResume && this.currentResume.id === id) {
            this.currentResume = null;
        }
      } catch (error) {
        this.error = error.response?.data?.message || error.message || `Failed to delete resume ${id}`;
        console.error(`Error deleting resume ${id}:`, error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async downloadResumeLink(resumeId, originalFileName) {
        // This action generates a temporary link or initiates download via browser.
        // Direct file download from JS can be tricky due to browser security.
        // Option 1: Open in new tab (browser might display or download based on Content-Disposition)
        // Option 2: Create an <a> element and click it.
        this.loading = true;
        this.error = null;
        try {
            const response = await apiClient.get(`/resumes/${resumeId}/download`, {
                responseType: 'blob', // Important for file downloads
            });
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', originalFileName || `resume-${resumeId}.pdf`); // Or determine extension
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            this.error = error.response?.data?.message || error.message || `Failed to download resume ${resumeId}`;
            console.error(`Error downloading resume ${resumeId}:`, error);
            alert(this.error); // Simple feedback
        } finally {
            this.loading = false;
        }
    }
  },
});
