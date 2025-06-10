<template>
  <div class="company-create-view">
    <h1>Create Company</h1>
    <form @submit.prevent="handleSubmit" class="company-form">
      <div class="form-group">
        <label for="name">Company Name:</label>
        <input type="text" id="name" v-model="form.name" required />
      </div>
      <div class="form-group">
        <label for="description">Description:</label>
        <textarea id="description" v-model="form.description"></textarea>
      </div>
      <div class="form-group">
        <label for="industry">Industry:</label>
        <input type="text" id="industry" v-model="form.industry" />
      </div>
      <div class="form-group">
        <label for="website">Website:</label>
        <input type="url" id="website" v-model="form.website" />
      </div>
      <div class="form-group">
        <label for="address">Address:</label>
        <input type="text" id="address" v-model="form.address" />
      </div>

      <div v-if="companyStore.error" class="error-message">
        <p>Error: {{ companyStore.error }}</p>
      </div>

      <button type="submit" :disabled="companyStore.loading" class="btn btn-primary">
        {{ companyStore.loading ? 'Creating...' : 'Create Company' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import { useCompanyStore } from '../stores/companyStore';
import { useAuthStore } from '../stores/authStore'; // To get current user ID
import { useRouter } from 'vue-router';

const companyStore = useCompanyStore();
const authStore = useAuthStore(); // Needed to pass createdByUserId
const router = useRouter();

const form = reactive({
  name: '',
  description: '',
  industry: '',
  website: '',
  address: '',
});

const handleSubmit = async () => {
  if (!authStore.currentUser?.id) {
      alert("You must be logged in to create a company."); // Or set error in store
      companyStore.error = "User not authenticated."; // Example of setting error
      return;
  }
  // The createdByUserId is added in the store action now
  // const companyData = { ...form, createdByUserId: authStore.currentUser.id };

  try {
    const newCompany = await companyStore.createCompany(form); // Pass only form data
    router.push({ name: 'company-detail', params: { id: newCompany.id } });
  } catch (error) {
    // Error is already set and logged in the store action
    // Optionally, display a user-friendly message here or rely on the template's error display
    console.error("Failed to create company from view:", error);
  }
};
</script>

<style scoped>
.company-create-view {
  max-width: 600px;
  margin: auto;
  padding: 20px;
}
.company-form .form-group {
  margin-bottom: 15px;
}
.company-form label {
  display: block;
  margin-bottom: 5px;
}
.company-form input[type="text"],
.company-form input[type="url"],
.company-form textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}
.company-form textarea {
  min-height: 100px;
}
.error-message {
  color: red;
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
