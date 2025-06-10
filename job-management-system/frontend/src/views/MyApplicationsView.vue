<template>
  <div class="my-applications-view">
    <h1>My Job Applications</h1>

    <div v-if="applicationStore.loading" class="loading">Loading your applications...</div>
    <div v-if="applicationStore.error" class="error-message">
      <p>Error: {{ applicationStore.error }}</p>
    </div>

    <table v-if="!applicationStore.loading && applicationStore.applications.length > 0" class="applications-table">
      <thead>
        <tr>
          <th>Position Title</th>
          <th>Company</th>
          <th>Application Date</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="app in applicationStore.applications" :key="app.id">
          <td>
            <router-link :to="{ name: 'position-detail', params: { id: app.positionId } }">
              {{ app.positionTitle || 'N/A' }}
            </router-link>
          </td>
          <td>{{ app.companyName || 'N/A' }}</td> {/* Assuming companyName is part of ApplicationDto */}
          <td>{{ new Date(app.applicationDate).toLocaleDateString() }}</td>
          <td><span :class="`status status-${app.status?.toLowerCase()}`">{{ app.status }}</span></td>
          <td>
            <button
              @click="confirmWithdraw(app.id)"
              class="btn btn-sm btn-danger"
              :disabled="applicationStore.loading || app.status === 'Withdrawn'"
              v-if="canWithdraw(app.status)">
              Withdraw
            </button>
            <span v-if="app.status === 'Withdrawn'" class="status-withdrawn-text">Application Withdrawn</span>
            <!-- Link to view application details if such a view exists -->
            <!-- <router-link :to="{ name: 'application-detail', params: { id: app.id } }">Details</router-link> -->
          </td>
        </tr>
      </tbody>
    </table>
    <p v-if="!applicationStore.loading && applicationStore.applications.length === 0 && !applicationStore.error">
      You have not submitted any applications yet.
    </p>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useApplicationStore } from '../stores/applicationStore';
import { useAuthStore } from '../stores/authStore';

const applicationStore = useApplicationStore();
const authStore = useAuthStore();

onMounted(() => {
  if (authStore.isAuthenticated) {
    applicationStore.fetchMyApplications();
  }
});

const canWithdraw = (status) => {
    // Define statuses from which an application can be withdrawn
    const withdrawableStatuses = ['Submitted', 'Under Review', 'Interviewing']; // Example
    return withdrawableStatuses.includes(status);
};

const confirmWithdraw = (id) => {
  if (window.confirm('Are you sure you want to withdraw this application?')) {
    applicationStore.withdrawApplication(id).catch(error => {
      alert(`Failed to withdraw application: ${applicationStore.error}`);
    });
  }
};
</script>

<style scoped>
.my-applications-view {
  padding: 20px;
  max-width: 900px;
  margin: auto;
}
.loading, .error-message {
  margin-top: 20px;
  padding: 10px;
  border-radius: 4px;
  text-align: center;
}
.error-message {
  color: #721c24;
  background-color: #f8d7da;
  border-color: #f5c6cb;
}
.loading {
  color: #004085;
  background-color: #cce5ff;
  border-color: #b8daff;
}
.applications-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}
.applications-table th, .applications-table td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: left;
}
.applications-table th {
  background-color: #f2f2f2;
}
.btn {
  padding: 6px 12px;
  border-radius: 4px;
  text-decoration: none;
  cursor: pointer;
  margin-right: 5px;
  border: none;
}
.btn-danger { background-color: #dc3545; color: white; }
.btn-sm { padding: 4px 8px; font-size: 0.9em; }

.status {
  padding: 3px 7px;
  border-radius: 4px;
  font-weight: bold;
  font-size: 0.9em;
  color: white;
}
.status-submitted { background-color: #007bff; }
.status-under.review { background-color: #ffc107; color: #333; } /* Example - adjust class name if needed */
.status-interviewing { background-color: #17a2b8; }
.status-offered { background-color: #28a745; }
.status-rejected { background-color: #6c757d; }
.status-withdrawn { background-color: #adb5bd; color: #333; }
.status-withdrawn-text { font-style: italic; color: #6c757d; }
</style>
