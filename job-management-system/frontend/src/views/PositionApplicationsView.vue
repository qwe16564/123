<template>
  <div class="position-applications-view">
    <h1>Applications for {{ positionStore.currentPosition?.title || 'Position' }}</h1>
    <p v-if="positionStore.currentPosition?.companyName">
      Company: {{ positionStore.currentPosition.companyName }}
    </p>
    <router-link v-if="positionId" :to="{ name: 'position-detail', params: { id: positionId } }">Back to Position</router-link>

    <div v-if="applicationStore.loading" class="loading">Loading applications...</div>
    <div v-if="applicationStore.error" class="error-message">
      <p>Error: {{ applicationStore.error }}</p>
    </div>

    <table v-if="!applicationStore.loading && applications.length > 0" class="applications-table">
      <thead>
        <tr>
          <th>Applicant</th>
          <th>Resume</th>
          <th>Applied On</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="app in applications" :key="app.id">
          <td>{{ app.applicantUsername || 'N/A' }}</td>
          <td>
            <a href="#" @click.prevent="downloadResume(app.resumeId, app.resumeFileName)" v-if="app.resumeId && app.resumeFileName">
              {{ app.resumeFileName }}
            </a>
            <span v-else>No Resume</span>
          </td>
          <td>{{ new Date(app.applicationDate).toLocaleDateString() }}</td>
          <td>
             <select v-model="app.status" @change="updateStatus(app.id, $event.target.value)"
                     :disabled="updatingStatus[app.id]"
                     class="status-select">
                <option value="Submitted">Submitted</option>
                <option value="Under Review">Under Review</option>
                <option value="Interviewing">Interviewing</option>
                <option value="Offered">Offered</option>
                <option value="Rejected">Rejected</option>
                <option value="Withdrawn">Withdrawn (by applicant)</option>
            </select>
            <small v-if="updatingStatus[app.id]">Updating...</small>
          </td>
          <td>
            <!-- View Applicant Profile (future), View Application Details (future) -->
          </td>
        </tr>
      </tbody>
    </table>
    <p v-if="!applicationStore.loading && applications.length === 0 && !applicationStore.error">
      No applications found for this position.
    </p>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import { useRoute } from 'vue-router';
import { useApplicationStore } from '../stores/applicationStore';
import { usePositionStore } from '../stores/positionStore'; // To get position title/company
import { useResumeStore } from '../stores/resumeStore'; // For downloading resume
// import { useAuthStore } from '../stores/authStore'; // For role checks if implementing full recruiter auth

const route = useRoute();
const applicationStore = useApplicationStore();
const positionStore = usePositionStore();
const resumeStore = useResumeStore();
// const authStore = useAuthStore();

const positionId = ref(route.params.id); // Assuming ID is passed as route param
const applications = computed(() => applicationStore.applications);
const updatingStatus = reactive({});


onMounted(async () => {
  if (positionId.value) {
    // Fetch position details to display title/company (if not already loaded)
    if (!positionStore.currentPosition || positionStore.currentPosition.id !== parseInt(positionId.value)) {
      await positionStore.fetchPosition(positionId.value).catch(e => console.error(e));
    }
    // Fetch applications for this position
    await applicationStore.fetchApplicationsForPosition(positionId.value);
  }
});

const downloadResume = async (resumeId, originalFileName) => {
  if (!resumeId) return;
  await resumeStore.downloadResumeLink(resumeId, originalFileName);
};

const updateStatus = async (applicationId, newStatus) => {
  updatingStatus[applicationId] = true;
  try {
    await applicationStore.updateApplicationStatus(applicationId, newStatus);
    // Optionally show a success message
  } catch (error) {
    alert(`Failed to update status: ${applicationStore.error}`);
    // Revert UI change if needed, though store should hold the source of truth
    applicationStore.fetchApplicationsForPosition(positionId.value); // Refresh to revert
  } finally {
    updatingStatus[applicationId] = false;
  }
};

// TODO: Add authorization checks for recruiter role to access this view
</script>

<style scoped>
.position-applications-view {
  padding: 20px;
  max-width: 1000px;
  margin: auto;
}
.loading, .error-message {
  margin-top: 20px;
  padding: 10px;
  border-radius: 4px;
  text-align: center;
}
.error-message { color: #721c24; background-color: #f8d7da; }
.loading { color: #004085; background-color: #cce5ff; }

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
.status-select {
    padding: 5px;
    border-radius: 4px;
    border: 1px solid #ccc;
}
</style>
