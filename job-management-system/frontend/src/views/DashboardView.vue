<template>
  <div class="dashboard-view">
    <h1>Dashboard</h1>
    <p v-if="authStore.isAuthenticated" class="welcome-message">
      Welcome, {{ authStore.currentUser?.username }}!
    </p>

    <div v-if="dashboardStore.loading" class="loading">Loading statistics...</div>
    <div v-if="dashboardStore.error" class="error-message">
      <p>Error fetching statistics: {{ dashboardStore.error }}</p>
    </div>

    <div v-if="!dashboardStore.loading && !dashboardStore.error" class="statistics-grid">
      <div class="stat-card">
        <h2>Total Companies</h2>
        <p class="stat-number">{{ dashboardStore.statistics.totalCompanies }}</p>
      </div>
      <div class="stat-card">
        <h2>Total Positions</h2>
        <p class="stat-number">{{ dashboardStore.statistics.totalPositions }}</p>
      </div>
      <div class="stat-card">
        <h2>Open Positions</h2>
        <p class="stat-number">{{ dashboardStore.statistics.totalOpenPositions }}</p>
      </div>
      <div class="stat-card">
        <h2>Total Resumes</h2>
        <p class="stat-number">{{ dashboardStore.statistics.totalResumes }}</p>
      </div>
      <div class="stat-card">
        <h2>Total Applications</h2>
        <p class="stat-number">{{ dashboardStore.statistics.totalApplications }}</p>
      </div>

      <div class="stat-card full-width" v-if="dashboardStore.statistics.applicationCountByStatus && Object.keys(dashboardStore.statistics.applicationCountByStatus).length > 0">
        <h2>Application Statuses</h2>
        <ul class="status-list">
          <li v-for="(count, status) in dashboardStore.statistics.applicationCountByStatus" :key="status">
            <span class="status-name">{{ status }}:</span>
            <span class="status-count">{{ count }}</span>
          </li>
        </ul>
        <!-- Basic Bar Chart Representation -->
        <div class="basic-chart">
            <div v-for="(count, status) in dashboardStore.statistics.applicationCountByStatus"
                 :key="status + '-bar'"
                 class="bar-item">
                <div class="bar-label">{{ status }} ({{ count }})</div>
                <div class="bar" :style="{ width: calculateBarWidth(count) + '%' }"></div>
            </div>
        </div>
      </div>
    </div>
    <p v-if="!dashboardStore.loading && dashboardStore.error" class="info-message">
      Could not load dashboard statistics.
    </p>
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue';
import { useAuthStore } from '../stores/authStore';
import { useDashboardStore } from '../stores/dashboardStore';

const authStore = useAuthStore();
const dashboardStore = useDashboardStore();

const maxApplicationCount = computed(() => {
    const counts = Object.values(dashboardStore.statistics.applicationCountByStatus || {});
    return Math.max(...counts, 0);
});

const calculateBarWidth = (count) => {
    if (maxApplicationCount.value === 0) return 0;
    return (count / maxApplicationCount.value) * 100;
};

onMounted(() => {
  dashboardStore.fetchStatistics();
});
</script>

<style scoped>
.dashboard-view {
  padding: 20px;
  max-width: 1200px;
  margin: auto;
}
.welcome-message {
  font-size: 1.2em;
  margin-bottom: 20px;
  color: #333;
}
.loading, .error-message, .info-message {
  margin-top: 20px;
  padding: 15px;
  border-radius: 5px;
  text-align: center;
}
.error-message {
  color: #721c24;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
}
.loading {
  color: #004085;
  background-color: #cce5ff;
  border: 1px solid #b8daff;
}
.info-message {
    color: #0c5460;
    background-color: #d1ecf1;
    border: 1px solid #bee5eb;
}
.statistics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}
.stat-card {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  text-align: center;
}
.stat-card h2 {
  font-size: 1.1em;
  color: #5a5a5a;
  margin-bottom: 10px;
}
.stat-card .stat-number {
  font-size: 2.5em;
  font-weight: bold;
  color: #007bff;
}
.stat-card.full-width {
  grid-column: 1 / -1; /* Span all columns */
}
.status-list {
  list-style: none;
  padding: 0;
  margin-top: 10px;
}
.status-list li {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  border-bottom: 1px solid #eee;
}
.status-list li:last-child {
  border-bottom: none;
}
.status-name {
  font-weight: 500;
}
.status-count {
  font-weight: bold;
  color: #333;
}

.basic-chart {
    margin-top: 20px;
    padding: 10px;
    border: 1px solid #eee;
    border-radius: 4px;
}
.bar-item {
    margin-bottom: 8px;
}
.bar-label {
    font-size: 0.9em;
    color: #555;
    margin-bottom: 3px;
}
.bar {
    height: 20px;
    background-color: #007bff;
    border-radius: 3px;
    transition: width 0.5s ease-in-out;
    min-width: 5px; /* So even 0 count shows a sliver or use different color */
}
</style>
