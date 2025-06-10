<template>
  <div class="position-edit-view">
    <h1>Edit Job Position</h1>
    <div v-if="initialLoading" class="loading">Loading position data...</div>
    <div v-if="pageError" class="error-message">{{ pageError }}</div>

    <form v-if="!initialLoading && form.id" @submit.prevent="handleSubmit" class="position-form">
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
        <div v-if="companyStore.loading && !companyStore.companies.length" class="loading-companies">Loading companies...</div>
        <div v-if="companyStore.error && !companyStore.companies.length" class="error-companies">Error loading companies: {{ companyStore.error }}</div>
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
       <div class="form-group">
        <label for="status">Status:</label>
        <select id="status" v-model="form.status" required>
            <option value="Open">Open</option>
            <option value="Closed">Closed</option>
            <option value="Filled">Filled</option>
        </select>
      </div>

      <div v-if="positionStore.error && submitAttempted" class="error-message">
        <p>Error updating: {{ positionStore.error }}</p>
      </div>

      <button type="submit" :disabled="positionStore.loading || companyStore.loading" class="btn btn-primary">
        {{ positionStore.loading ? 'Updating...' : 'Update Position' }}
      </button>
      <router-link :to="{ name: 'position-detail', params: { id: form.id } }" class="btn btn-secondary">Cancel</router-link>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { usePositionStore } from '../stores/positionStore';
import { useCompanyStore } from '../stores/companyStore'; // To fetch companies
import { useRoute, useRouter } from 'vue-router';

const positionStore = usePositionStore();
const companyStore = useCompanyStore();
const route = useRoute();
const router = useRouter();

const form = reactive({
  id: null,
  title: '',
  companyId: '',
  description: '',
  requirements: '',
  location: '',
  employmentType: '',
  status: 'Open', // Default status
});

const initialLoading = ref(true);
const pageError = ref(null);
const submitAttempted = ref(false);

const positionId = ref(route.params.id);

const loadPositionData = async (id) => {
  initialLoading.value = true;
  pageError.value = null;
  positionStore.error = null; // Clear store error from previous attempts
  try {
    // Fetch companies if not already loaded
    if (companyStore.companies.length === 0) {
      await companyStore.fetchCompanies();
    }
    // Fetch position data
    const positionData = await positionStore.fetchPosition(id);
    // Assign to form, making sure companyId is correctly set for the select
    form.id = positionData.id;
    form.title = positionData.title;
    form.companyId = positionData.companyId; // Assuming DTO has companyId directly
    form.description = positionData.description;
    form.requirements = positionData.requirements;
    form.location = positionData.location;
    form.employmentType = positionData.employmentType;
    form.status = positionData.status;

  } catch (err) {
    console.error("Failed to load position data for edit:", err);
    pageError.value = positionStore.error || companyStore.error || "Could not load data for editing.";
  } finally {
    initialLoading.value = false;
  }
};

onMounted(() => {
  if (positionId.value) {
    loadPositionData(positionId.value);
  }
});

watch(() => route.params.id, (newId) => {
  if (newId && newId !== positionId.value) {
    positionId.value = newId;
    loadPositionData(newId);
  }
});

const handleSubmit = async () => {
  submitAttempted.value = true;
  if (!form.companyId) {
      alert("Company must be selected.");
      positionStore.error = "Company not selected for update.";
      return;
  }
  // Backend DTO for update might not need/want postedByUserId
  // It's also possible the backend only allows certain fields to be updated.
  // The current form sends all its fields.
  const updateData = { ...form };

  try {
    const updatedPosition = await positionStore.updatePosition(form.id, updateData);
    router.push({ name: 'position-detail', params: { id: updatedPosition.id } });
  } catch (error) {
    console.error("Failed to update position from view:", error);
    // Error is set in store and displayed by the template
  }
};
</script>

<style scoped>
.position-edit-view {
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
.loading, .error-message, .loading-companies, .error-companies {
  padding: 10px;
  margin-bottom: 15px;
  border-radius: 4px;
}
.loading-companies, .error-companies {
    font-size: 0.9em;
    margin-top: 5px;
}
.error-message, .error-companies {
  color: #721c24;
  background-color: #f8d7da;
  border-color: #f5c6cb;
}
.loading {
  color: #004085;
  background-color: #cce5ff;
  border-color: #b8daff;
}
.btn {
  padding: 10px 15px;
  border-radius: 4px;
  text-decoration: none;
  cursor: pointer;
  border: none;
  margin-right: 10px;
}
.btn-primary { background-color: #007bff; color: white; }
.btn-secondary { background-color: #6c757d; color: white; }
</style>
