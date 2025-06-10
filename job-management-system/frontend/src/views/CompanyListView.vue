<template>
  <div class="company-list-view">
    <h1>Companies</h1>
    <div class="actions">
      <router-link to="/companies/new" class="btn btn-primary">Create New Company</router-link>
    </div>

    <div v-if="companyStore.loading" class="loading">Loading companies...</div>
    <div v-if="companyStore.error" class="error-message">
      <p>Error fetching companies: {{ companyStore.error }}</p>
    </div>

    <table v-if="!companyStore.loading && companyStore.companies.length > 0" class="companies-table">
      <thead>
        <tr>
          <th>Name</th>
          <th>Industry</th>
          <th>Website</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="company in companyStore.companies" :key="company.id">
          <td>{{ company.name }}</td>
          <td>{{ company.industry }}</td>
          <td><a :href="company.website" target="_blank">{{ company.website }}</a></td>
          <td>
            <router-link :to="{ name: 'company-detail', params: { id: company.id } }" class="btn btn-sm btn-info">View</router-link>
            <router-link :to="{ name: 'company-edit', params: { id: company.id } }" class="btn btn-sm btn-warning">Edit</router-link>
            <button @click="confirmDelete(company.id)" class="btn btn-sm btn-danger">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>
    <p v-if="!companyStore.loading && companyStore.companies.length === 0 && !companyStore.error">
      No companies found.
    </p>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useCompanyStore } from '../stores/companyStore';
import { useRouter } from 'vue-router';

const companyStore = useCompanyStore();
const router = useRouter();

onMounted(() => {
  companyStore.fetchCompanies();
});

const confirmDelete = (id) => {
  if (window.confirm('Are you sure you want to delete this company?')) {
    companyStore.deleteCompany(id).catch(error => {
      // Error is already logged in store, could show a notification here
      alert(`Failed to delete company: ${companyStore.error}`);
    });
  }
};
</script>

<style scoped>
.company-list-view {
  padding: 20px;
  max-width: 900px;
  margin: auto;
}
.actions {
  margin-bottom: 20px;
}
.loading, .error-message {
  margin-top: 20px;
}
.error-message {
  color: red;
}
.companies-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}
.companies-table th, .companies-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}
.companies-table th {
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
.btn-warning { background-color: #ffc107; color: black; }
.btn-danger { background-color: #dc3545; color: white; }
.btn-sm { padding: 4px 8px; font-size: 0.9em; }
</style>
