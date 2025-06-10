<template>
  <div class="company-edit-view">
    <h1>Edit Company</h1>
    <div v-if="initialLoading" class="loading">Loading company data...</div>
    <div v-if="pageError" class="error-message">{{ pageError }}</div>

    <form v-if="!initialLoading && form.id" @submit.prevent="handleSubmit" class="company-form">
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

      <div v-if="companyStore.error && submitAttempted" class="error-message">
        <p>Error updating: {{ companyStore.error }}</p>
      </div>

      <button type="submit" :disabled="companyStore.loading" class="btn btn-primary">
        {{ companyStore.loading ? 'Updating...' : 'Update Company' }}
      </button>
      <router-link :to="{ name: 'company-detail', params: { id: form.id } }" class="btn btn-secondary">Cancel</router-link>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useCompanyStore } from '../stores/companyStore';
import { useRoute, useRouter } from 'vue-router';

const companyStore = useCompanyStore();
const route = useRoute();
const router = useRouter();

const form = reactive({
  id: null,
  name: '',
  description: '',
  industry: '',
  website: '',
  address: '',
});

const initialLoading = ref(true);
const pageError = ref(null);
const submitAttempted = ref(false); // To show store error only after submit attempt

const companyId = ref(route.params.id);

const loadCompanyData = async (id) => {
  initialLoading.value = true;
  pageError.value = null;
  companyStore.error = null; // Clear previous store errors
  try {
    // Use existing currentCompany if IDs match and it's loaded, else fetch
    if (companyStore.currentCompany && companyStore.currentCompany.id === parseInt(id)) {
        Object.assign(form, companyStore.currentCompany);
    } else {
        const companyData = await companyStore.fetchCompany(id);
        Object.assign(form, companyData);
    }
  } catch (err) {
    console.error("Failed to load company data for edit:", err);
    pageError.value = companyStore.error || "Could not load company data.";
  } finally {
    initialLoading.value = false;
  }
};

onMounted(() => {
  if (companyId.value) {
    loadCompanyData(companyId.value);
  }
});

// Watch for route param changes if user navigates from one edit view to another (unlikely here)
watch(() => route.params.id, (newId) => {
  if (newId) {
    companyId.value = newId;
    loadCompanyData(newId);
  }
});


const handleSubmit = async () => {
  submitAttempted.value = true;
  const updateData = { ...form };
  // Remove ID from data to be sent, as it's in URL. Backend DTO might not expect it.
  // Or ensure backend UpdateCompanyRequest DTO doesn't have 'id' or it's ignored.
  // For this example, we send all, assuming service/backend handles it.

  try {
    const updatedCompany = await companyStore.updateCompany(form.id, updateData);
    router.push({ name: 'company-detail', params: { id: updatedCompany.id } });
  } catch (error) {
    // Error is set in store and displayed by the template
    console.error("Failed to update company from view:", error);
  }
};
</script>

<style scoped>
.company-edit-view {
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
  margin-right: 10px;
}
.btn-primary { background-color: #007bff; color: white; }
.btn-secondary { background-color: #6c757d; color: white; }
.loading { text-align: center; padding: 20px; }
</style>
