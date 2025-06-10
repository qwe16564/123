import { defineStore } from 'pinia';
import apiClient from '../services/api';

export const useDashboardStore = defineStore('dashboard', {
  state: () => ({
    statistics: {
      totalCompanies: 0,
      totalPositions: 0,
      totalOpenPositions: 0,
      totalResumes: 0,
      totalApplications: 0,
      applicationCountByStatus: {}, // e.g., { "Submitted": 10, "Under Review": 5 }
    },
    loading: false,
    error: null,
  }),
  actions: {
    async fetchStatistics() {
      this.loading = true;
      this.error = null;
      try {
        const response = await apiClient.get('/statistics/summary');
        this.statistics = response.data;
      } catch (error) {
        this.error = error.response?.data?.message || error.message || 'Failed to fetch dashboard statistics';
        console.error("Error fetching statistics:", error);
        // Reset to default state on error to avoid displaying stale data
        this.statistics = {
            totalCompanies: 0,
            totalPositions: 0,
            totalOpenPositions: 0,
            totalResumes: 0,
            totalApplications: 0,
            applicationCountByStatus: {},
        };
      } finally {
        this.loading = false;
      }
    },
  },
});
