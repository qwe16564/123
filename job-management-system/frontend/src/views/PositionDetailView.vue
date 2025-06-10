<template>
  <div class="position-detail-view">
    <div v-if="positionStore.loading" class="loading">Loading position details...</div>
    <div v-if="positionStore.error && !positionStore.currentPosition" class="error-message">
      <p>Error loading position: {{ positionStore.error }}</p>
    </div>

    <article v-if="positionStore.currentPosition && !positionStore.loading" class="position-details">
      <h1>{{ positionStore.currentPosition.title }}</h1>
      <p><strong>Company:</strong> {{ positionStore.currentPosition.companyName || 'N/A' }}</p> {/* Assuming companyName is in DTO */}
      <p><strong>Location:</strong> {{ positionStore.currentPosition.location || 'N/A' }}</p>
      <p><strong>Employment Type:</strong> {{ positionStore.currentPosition.employmentType || 'N/A' }}</p>
      <p><strong>Status:</strong> {{ positionStore.currentPosition.status || 'N/A' }}</p>

      <section class="description-section">
        <h2>Description</h2>
        <p style="white-space: pre-wrap;">{{ positionStore.currentPosition.description || 'No description provided.' }}</p>
      </section>

      <section class="requirements-section" v-if="positionStore.currentPosition.requirements">
        <h2>Requirements</h2>
        <p style="white-space: pre-wrap;">{{ positionStore.currentPosition.requirements }}</p>
      </section>

      <p v-if="positionStore.currentPosition.postedByUsername">
        <strong>Posted by:</strong> {{ positionStore.currentPosition.postedByUsername }}
      </p>
      <p><strong>Posted At:</strong> {{ new Date(positionStore.currentPosition.createdAt).toLocaleString() }}</p>
      <p><strong>Last Updated:</strong> {{ new Date(positionStore.currentPosition.updatedAt).toLocaleString() }}</p>

      <div class="actions">
        <router-link :to="{ name: 'position-edit', params: { id: positionStore.currentPosition.id } }" class="btn btn-warning">Edit Position</router-link>
        <button @click="confirmDelete(positionStore.currentPosition.id)" class="btn btn-danger" :disabled="deleting">
          {{ deleting ? 'Deleting...' : 'Delete Position' }}
        </button>
        <router-link to="/positions" class="btn btn-secondary">Back to List</router-link>

        <button
            v-if="authStore.isAuthenticated && authStore.userRole === 'applicant'"
            @click="showApplyModal = true"
            class="btn btn-success">
            Apply Now
        </button>
        <router-link
            v-if="authStore.isAuthenticated && (authStore.userRole === 'recruiter' || authStore.userRole === 'admin')"
            :to="{ name: 'position-applications', params: { id: positionStore.currentPosition.id } }"
            class="btn btn-info">
            View Applications
        </router-link>
      </div>
    </article>

    <!-- Apply Modal -->
    <div v-if="showApplyModal" class="modal-overlay">
      <div class="modal-content">
        <h3>Apply for: {{ positionStore.currentPosition?.title }}</h3>
        <div v-if="resumeStore.loading">Loading your resumes...</div>
        <div v-if="resumeStore.error" class="error-message">{{ resumeStore.error }}</div>

        <form @submit.prevent="handleApplySubmit">
          <div class="form-group">
            <label for="resumeSelect">Select your resume:</label>
            <select id="resumeSelect" v-model="selectedResumeId" required :disabled="resumeStore.resumes.length === 0">
              <option value="" disabled>{{ resumeStore.resumes.length === 0 ? 'No resumes available' : 'Select a resume' }}</option>
              <option v-for="resume in resumeStore.resumes" :key="resume.id" :value="resume.id">
                {{ resume.originalFileName }} (Uploaded: {{ new Date(resume.uploadedAt).toLocaleDateString() }})
              </option>
            </select>
            <router-link v-if="resumeStore.resumes.length === 0 && !resumeStore.loading" to="/resumes/upload">Upload a Resume</router-link>
          </div>
          <div class="form-group">
            <label for="applicationNotes">Notes (optional):</label>
            <textarea id="applicationNotes" v-model="applicationNotes" rows="3"></textarea>
          </div>

          <div v-if="applicationStore.error" class="error-message">{{ applicationStore.error }}</div>
          <div v-if="applySuccessMessage" class="success-message">{{ applySuccessMessage }}</div>

          <div class="modal-actions">
            <button type="submit" class="btn btn-primary" :disabled="!selectedResumeId || applicationStore.loading">
              {{ applicationStore.loading ? 'Submitting...' : 'Submit Application' }}
            </button>
            <button type="button" @click="closeApplyModal" class="btn btn-secondary">Cancel</button>
          </div>
        </form>
      </div>
    </div>

  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { usePositionStore } from '../stores/positionStore';
import { useAuthStore } from '../stores/authStore';
import { useResumeStore } from '../stores/resumeStore';
import { useApplicationStore } from '../stores/applicationStore';
import { useRoute, useRouter } from 'vue-router';

const positionStore = usePositionStore();
const authStore = useAuthStore();
const resumeStore = useResumeStore();
const applicationStore = useApplicationStore();
const route = useRoute();
const router = useRouter();
const deleting = ref(false);
const showApplyModal = ref(false);
const selectedResumeId = ref('');
const applicationNotes = ref('');
const applySuccessMessage = ref('');


const positionId = ref(route.params.id);

const loadPosition = async (id) => {
    positionStore.error = null;
    applicationStore.error = null; // Clear application errors too
    applySuccessMessage.value = '';
    try {
        await positionStore.fetchPosition(id);
    } catch(err) {
        console.error("Failed to fetch position from view's loadPosition:", err);
    }
};

onMounted(() => {
  if (positionId.value) {
    loadPosition(positionId.value);
  }
  // Fetch user's resumes if they are an applicant and modal might be shown
  if (authStore.isAuthenticated && authStore.userRole === 'applicant') {
    resumeStore.fetchMyResumes();
  }
});

watch(() => route.params.id, (newId) => {
  if (newId && newId !== positionId.value) {
    positionId.value = newId;
    loadPosition(newId);
    closeApplyModal(); // Close modal if navigating to a different position
  }
});

const closeApplyModal = () => {
    showApplyModal.value = false;
    selectedResumeId.value = '';
    applicationNotes.value = '';
    applicationStore.error = null; // Clear errors when closing
    applySuccessMessage.value = '';
};

const handleApplySubmit = async () => {
    if (!selectedResumeId.value || !positionStore.currentPosition?.id) return;

    const applicationData = {
        resumeId: selectedResumeId.value,
        positionId: positionStore.currentPosition.id,
        status: 'Submitted', // Initial status
        notes: applicationNotes.value,
    };

    try {
        await applicationStore.createApplication(applicationData);
        applySuccessMessage.value = "Application submitted successfully!";
        setTimeout(() => {
            closeApplyModal();
            // Optionally, navigate or refresh data
        }, 2000);
    } catch (err) {
        // Error is handled by store and displayed in modal
        console.error("Failed to submit application:", err);
    }
};

const confirmDelete = async (id) => {
  if (window.confirm('Are you sure you want to delete this position?')) {
    deleting.value = true;
    try {
      await positionStore.deletePosition(id);
      router.push({ name: 'position-list' });
    } catch (error) {
      alert(`Failed to delete position: ${positionStore.error}`);
    } finally {
      deleting.value = false;
    }
  }
};
</script>

<style scoped>
.position-detail-view {
  max-width: 800px;
  margin: auto;
  padding: 20px;
}
.position-details h1 {
  margin-bottom: 10px;
  color: #333;
}
.position-details p {
  margin-bottom: 8px;
  line-height: 1.6;
  color: #555;
}
.description-section, .requirements-section {
  margin-top: 20px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-left: 4px solid #007bff;
}
.description-section h2, .requirements-section h2 {
    margin-top: 0;
    color: #0056b3;
}
.actions {
  margin-top: 25px;
}
.btn {
  padding: 10px 15px;
  border-radius: 4px;
  text-decoration: none;
  cursor: pointer;
  margin-right: 10px;
  border: none;
  font-weight: bold;
}
.btn-warning { background-color: #ffc107; color: black; }
.btn-danger { background-color: #dc3545; color: white; }
.btn-secondary { background-color: #6c757d; color: white; }
.loading, .error-message {
  text-align: center;
  padding: 20px;
  font-size: 1.2em;
}
.error-message {
  color: red;
}
</style>
