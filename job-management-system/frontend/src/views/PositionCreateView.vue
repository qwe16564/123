<template>
  <div class="position-create-view">
    <h1>Create Job Position</h1>
    <form @submit.prevent="handleSubmit" class="position-form">
      <div class="form-group">
        <label for="title">Title:</label>
        <input type="text" id="title" v-model="form.title" required />
      </div>

      <div class="form-group">
        <label for="companyId">Company:</label>
        <select id="companyId" v-model="form.companyId" required>
          <option disabled value="">Please select a company</option>
          <option v-for="company in companyStore.companies" :key="company.id" :value="company.id">
            {{ company.name }}
          </option>
        </select>
        <div v-if="companyStore.loading" class="loading-companies">Loading companies...</div>
        <div v-if="companyStore.error" class="error-companies">Error: {{ companyStore.error }}</div>
      </div>

      <div class="form-group">
        <label for="description">Description:</label>
        <textarea id="description" v-model="form.description" required></textarea>
      </div>
      <div class="form-group">
        <label for="requirements">Requirements:</label>
        <textarea id="requirements" v-model="form.requirements"></textarea>
      </div>
      <div class="form-group">
        <label for="location">Location:</label>
        <input type="text" id="location" v-model="form.location" />
      </div>
      <div class="form-group">
        <label for="employmentType">Employment Type:</label>
        <select id="employmentType" v-model="form.employmentType">
            <option value="">Select Type</option>
            <option value="Full-time">Full-time</option>
            <option value="Part-time">Part-time</option>
            <option value="Contract">Contract</option>
            <option value="Temporary">Temporary</option>
            <option value="Internship">Internship</option>
        </select>
      </div>

      <!-- Status is typically defaulted by backend, not set by user on creation -->

      <div v-if="positionStore.error" class="error-message">
        <p>Error creating position: {{ positionStore.error }}</p>
      </div>

      <button type="submit" :disabled="positionStore.loading || companyStore.loading" class="btn btn-primary">
        {{ positionStore.loading ? 'Creating...' : 'Create Position' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue';
import { usePositionStore } from '../stores/positionStore';
import { useCompanyStore } from '../stores/companyStore'; // To fetch companies for select
import { useAuthStore } from '../stores/authStore'; // To get current user ID
import { useRouter } from 'vue-router';

const positionStore = usePositionStore();
const companyStore = useCompanyStore();
const authStore = useAuthStore();
const router = useRouter();

const form = reactive({
  title: '',
  companyId: '', // Will be selected from dropdown
  description: '',
  requirements: '',
  location: '',
  employmentType: 'Full-time', // Default value
});

onMounted(() => {
  // Fetch companies for the dropdown if not already loaded or if a refresh is desired
  if (companyStore.companies.length === 0) {
    companyStore.fetchCompanies().catch(err => {
        console.error("Failed to load companies for selection:", err);
        // Error will be shown in template via companyStore.error
    });
  }
});

const handleSubmit = async () => {
  if (!authStore.currentUser?.id) {
    alert("You must be logged in to create a position.");
    positionStore.error = "User not authenticated.";
    return;
  }
  if (!form.companyId) {
    alert("Please select a company.");
    positionStore.error = "Company not selected.";
    return;
  }
  // postedByUserId is added by the positionStore action
  try {
    const newPosition = await positionStore.createPosition(form);
    router.push({ name: 'position-detail', params: { id: newPosition.id } });
  } catch (error) {
    // Error is set and logged in the store action
    console.error("Failed to create position from view:", error);
  }
};
</script>

<style scoped>
.position-create-view {
  max-width: 700px;
  margin: auto;
  padding: 20px;
}
.position-form .form-group {
  margin-bottom: 15px;
}
.position-form label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}
.position-form input[type="text"],
.position-form select,
.position-form textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}
.position-form textarea {
  min-height: 120px;
  resize: vertical;
}
.loading-companies, .error-companies {
    font-size: 0.9em;
    margin-top: 5px;
}
.error-companies, .error-message {
  color: red;
}
.error-message {
  margin-bottom: 15px;
}
.btn {
  padding: 10px 15px;
  border-radius: 4px;
  text-decoration: none;
  cursor: pointer;
  border: none;
}
.btn-primary { background-color: #007bff; color: white; }
</style>
