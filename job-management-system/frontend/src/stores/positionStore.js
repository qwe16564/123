import { defineStore } from 'pinia';
import apiClient from '../services/api';
import { useAuthStore } from './authStore';

export const usePositionStore = defineStore('position', {
  state: () => ({
    positions: [],
    currentPosition: null,
    loading: false,
    error: null,
    // We might also need a list of companies for select dropdowns
    // This could be fetched here or by using companyStore directly in components.
    // For simplicity, components can use companyStore.
  }),
  getters: {
    getPositionById: (state) => (id) => {
      return state.positions.find(pos => pos.id === parseInt(id)) || state.currentPosition;
    },
  },
  actions: {
    async fetchPositions(filters = {}) { // filters can be { companyId, status, etc. }
      this.loading = true;
      this.error = null;
      try {
        // Example: apiClient.get('/positions', { params: filters });
        // For now, fetching all positions as filter UI is not yet implemented
        const response = await apiClient.get('/positions', { params: filters });
        this.positions = response.data;
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Failed to fetch positions';
        console.error("Error fetching positions:", error);
      } finally {
        this.loading = false;
      }
    },
    async fetchPosition(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await apiClient.get(`/positions/${id}`);
        this.currentPosition = response.data;
        // Update in list as well
        const index = this.positions.findIndex(p => p.id === response.data.id);
        if (index !== -1) {
            this.positions[index] = response.data;
        }
        return response.data; // Return for immediate use
      } catch (error) {
        this.error = error.response?.data?.message || error.message || `Failed to fetch position ${id}`;
        this.currentPosition = null;
        console.error(`Error fetching position ${id}:`, error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async createPosition(positionData) { // positionData should include companyId
      this.loading = true;
      this.error = null;
      const authStore = useAuthStore();

      const requestData = {
        ...positionData,
        postedByUserId: authStore.currentUser?.id
      };

      if (!requestData.postedByUserId) {
        this.error = "User must be logged in to create a position.";
        this.loading = false;
        console.error("User ID not found for creating position.");
        throw new Error(this.error);
      }
      if (!requestData.companyId) {
        this.error = "Company ID is required to create a position.";
        this.loading = false;
        console.error("Company ID not found for creating position.");
        throw new Error(this.error);
      }

      try {
        const response = await apiClient.post('/positions', requestData);
        this.positions.push(response.data); // Add to local state
        this.currentPosition = response.data;
        return response.data;
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Failed to create position';
        if (error.response?.data?.details) {
             this.error += `: ${error.response.data.details.join(', ')}`;
        }
        console.error("Error creating position:", error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async updatePosition(id, positionData) {
      this.loading = true;
      this.error = null;
      try {
        // If companyId is part of positionData, it will be sent.
        // Backend UpdatePositionRequest DTO should handle which fields are updatable.
        const response = await apiClient.put(`/positions/${id}`, positionData);
        const index = this.positions.findIndex(p => p.id === response.data.id);
        if (index !== -1) {
          this.positions[index] = response.data;
        }
        if (this.currentPosition && this.currentPosition.id === response.data.id) {
            this.currentPosition = response.data;
        }
        return response.data;
      } catch (error) {
        this.error = error.response?.data?.message || error.message || `Failed to update position ${id}`;
        if (error.response?.data?.details) {
             this.error += `: ${error.response.data.details.join(', ')}`;
        }
        console.error(`Error updating position ${id}:`, error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async deletePosition(id) {
      this.loading = true;
      this.error = null;
      try {
        await apiClient.delete(`/positions/${id}`);
        this.positions = this.positions.filter(p => p.id !== id);
        if (this.currentPosition && this.currentPosition.id === id) {
            this.currentPosition = null;
        }
      } catch (error) {
        this.error = error.response?.data?.message || error.message || `Failed to delete position ${id}`;
        console.error(`Error deleting position ${id}:`, error);
        throw error;
      } finally {
        this.loading = false;
      }
    },
    // Action to update status specifically (if using the PATCH endpoint)
    async updatePositionStatus(id, status) {
        this.loading = true;
        this.error = null;
        try {
            const response = await apiClient.patch(`/positions/${id}/status`, { status }); // Backend expects { "status": "new_status" }
            const index = this.positions.findIndex(p => p.id === response.data.id);
            if (index !== -1) {
                this.positions[index] = response.data;
            }
            if (this.currentPosition && this.currentPosition.id === response.data.id) {
                this.currentPosition = response.data;
            }
            return response.data;
        } catch (error) {
            this.error = error.response?.data?.message || error.message || `Failed to update status for position ${id}`;
            console.error(`Error updating status for position ${id}:`, error);
            throw error;
        } finally {
            this.loading = false;
        }
    }
  },
});
