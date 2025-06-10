<template>
  <div class="company-detail-view">
    <div v-if="companyStore.loading" class="loading">Loading company details...</div>
    <div v-if="companyStore.error && !companyStore.currentCompany" class="error-message">
      <p>Error loading company: {{ companyStore.error }}</p>
    </div>

    <article v-if="companyStore.currentCompany && !companyStore.loading" class="company-details">
      <h1>{{ companyStore.currentCompany.name }}</h1>
      <p><strong>Industry:</strong> {{ companyStore.currentCompany.industry || 'N/A' }}</p>
      <p><strong>Website:</strong>
        <a :href="companyStore.currentCompany.website" target="_blank" v-if="companyStore.currentCompany.website">{{ companyStore.currentCompany.website }}</a>
        <span v-else>N/A</span>
      </p>
      <p><strong>Address:</strong> {{ companyStore.currentCompany.address || 'N/A' }}</p>

      <section class="description-section">
        <h2>Description</h2>
        <p>{{ companyStore.currentCompany.description || 'No description provided.' }}</p>
      </section>

      <p v-if="companyStore.currentCompany.createdByUsername">
        <strong>Created by:</strong> {{ companyStore.currentCompany.createdByUsername }}
      </p>
      <p><strong>Created At:</strong> {{ new Date(companyStore.currentCompany.createdAt).toLocaleString() }}</p>
      <p><strong>Last Updated:</strong> {{ new Date(companyStore.currentCompany.updatedAt).toLocaleString() }}</p>

      <div class="actions">
        <router-link :to="{ name: 'company-edit', params: { id: companyStore.currentCompany.id } }" class="btn btn-warning">Edit Company</router-link>
        <button @click="confirmDelete(companyStore.currentCompany.id)" class="btn btn-danger" :disabled="deleting">
          {{ deleting ? 'Deleting...' : 'Delete Company' }}
        </button>
        <router-link to="/companies" class="btn btn-secondary">Back to List</router-link>
      </div>
    </article>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { useCompanyStore } from '../stores/companyStore';
import { useRoute, useRouter } from 'vue-router';

const companyStore = useCompanyStore();
const route = useRoute();
const router = useRouter();
const deleting = ref(false);

const companyId = ref(route.params.id);

onMounted(() => {
  if (companyId.value) {
    companyStore.fetchCompany(companyId.value).catch(err => {
        // Error already handled in store, component can react if needed
        console.error("Failed to fetch company from view's onMounted:", err);
    });
  }
});

// Watch for route changes if navigating between detail views (less common for this setup)
watch(() => route.params.id, (newId) => {
  if (newId) {
    companyId.value = newId;
    companyStore.fetchCompany(newId).catch(err => {
        console.error("Failed to fetch company from view's watch:", err);
    });
  }
});

const confirmDelete = async (id) => {
  if (window.confirm('Are you sure you want to delete this company? This action cannot be undone.')) {
    deleting.value = true;
    try {
      await companyStore.deleteCompany(id);
      router.push({ name: 'company-list' });
    } catch (error) {
      alert(`Failed to delete company: ${companyStore.error}`);
    } finally {
      deleting.value = false;
    }
  }
};
</script>

<style scoped>
.company-detail-view {
  max-width: 800px;
  margin: auto;
  padding: 20px;
}
.company-details h1 {
  margin-bottom: 10px;
}
.company-details p {
  margin-bottom: 8px;
  line-height: 1.6;
}
.description-section {
  margin-top: 20px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-left: 4px solid #007bff;
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
}
.btn-warning { background-color: #ffc107; color: black; }
.btn-danger { background-color: #dc3545; color: white; }
.btn-secondary { background-color: #6c757d; color: white; }
.loading, .error-message {
  text-align: center;
  padding: 20px;
}
.error-message {
  color: red;
}
</style>
