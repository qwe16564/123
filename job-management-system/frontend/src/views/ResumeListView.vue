<template>
  <div class="resume-list-view">
    <h1>My Resumes</h1>
    <div class="actions">
      <router-link to="/resumes/upload" class="btn btn-primary">Upload New Resume</router-link>
    </div>

    <div v-if="resumeStore.loading" class="loading">Loading resumes...</div>
    <div v-if="resumeStore.error" class="error-message">
      <p>Error: {{ resumeStore.error }}</p>
    </div>

    <table v-if="!resumeStore.loading && resumeStore.resumes.length > 0" class="resumes-table">
      <thead>
        <tr>
          <th>Original Filename</th>
          <th>Uploaded At</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="resume in resumeStore.resumes" :key="resume.id">
          <td>{{ resume.originalFileName }}</td>
          <td>{{ new Date(resume.uploadedAt).toLocaleString() }}</td>
          <td>
            <button @click="download(resume.id, resume.originalFileName)" class="btn btn-sm btn-info" :disabled="resumeStore.loading">Download</button>
            <button @click="confirmDelete(resume.id)" class="btn btn-sm btn-danger" :disabled="resumeStore.loading">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>
    <p v-if="!resumeStore.loading && resumeStore.resumes.length === 0 && !resumeStore.error">
      You haven't uploaded any resumes yet.
    </p>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useResumeStore } from '../stores/resumeStore';
import { useAuthStore } from '../stores/authStore'; // To ensure user is logged in

const resumeStore = useResumeStore();
const authStore = useAuthStore();

onMounted(() => {
  // Only fetch if user is authenticated, otherwise store action will handle error/empty.
  if (authStore.isAuthenticated) {
    resumeStore.fetchMyResumes();
  }
});

const download = async (resumeId, originalFileName) => {
    await resumeStore.downloadResumeLink(resumeId, originalFileName);
};

const confirmDelete = (id) => {
  if (window.confirm('Are you sure you want to delete this resume?')) {
    resumeStore.deleteResume(id).catch(error => {
      alert(`Failed to delete resume: ${resumeStore.error}`);
    });
  }
};
</script>

<style scoped>
.resume-list-view {
  padding: 20px;
  max-width: 800px;
  margin: auto;
}
.actions {
  margin-bottom: 20px;
}
.loading, .error-message {
  margin-top: 20px;
  padding: 10px;
  border-radius: 4px;
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
.resumes-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}
.resumes-table th, .resumes-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}
.resumes-table th {
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
.btn-primary { background-color: #007bff; color: white; }
.btn-info { background-color: #17a2b8; color: white; }
.btn-danger { background-color: #dc3545; color: white; }
.btn-sm { padding: 4px 8px; font-size: 0.9em; }
</style>
