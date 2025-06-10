<template>
  <div class="position-list-view">
    <h1>Job Positions</h1>
    <div class="actions">
      <router-link to="/positions/new" class="btn btn-primary">Create New Position</router-link>
    </div>

    <!-- Basic Filters -->
    <div class="filters">
      <input type="text" v-model="filters.title" placeholder="Search by title..." @input="applyFiltersDebounced" />
      <select v-model="filters.status" @change="applyFilters">
        <option value="">All Statuses</option>
        <option value="Open">Open</option>
        <option value="Closed">Closed</option>
        <option value="Filled">Filled</option>
      </select>
       <!-- Company filter could be a select dropdown populated from companyStore -->
    </div>

    <div v-if="positionStore.loading" class="loading">Loading positions...</div>
    <div v-if="positionStore.error" class="error-message">
      <p>Error fetching positions: {{ positionStore.error }}</p>
    </div>

    <table v-if="!positionStore.loading && positionStore.positions.length > 0" class="positions-table">
      <thead>
        <tr>
          <th>Title</th>
          <th>Company</th>
          <th>Location</th>
          <th>Type</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="position in positionStore.positions" :key="position.id">
          <td>{{ position.title }}</td>
          <td>{{ position.companyName || 'N/A' }}</td> {/* companyName should come from backend DTO */}
          <td>{{ position.location }}</td>
          <td>{{ position.employmentType }}</td>
          <td>{{ position.status }}</td>
          <td>
            <router-link :to="{ name: 'position-detail', params: { id: position.id } }" class="btn btn-sm btn-info">View</router-link>
            <router-link :to="{ name: 'position-edit', params: { id: position.id } }" class="btn btn-sm btn-warning">Edit</router-link>
            <button @click="confirmDelete(position.id)" class="btn btn-sm btn-danger">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>
    <p v-if="!positionStore.loading && positionStore.positions.length === 0 && !positionStore.error">
      No positions found.
    </p>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { usePositionStore } from '../stores/positionStore';
import { useRouter } from 'vue-router'; // Not strictly needed here yet, but good for consistency

const positionStore = usePositionStore();
// const router = useRouter(); // If needed for navigation from this component

const filters = reactive({
  title: '',
  status: '',
  companyId: null, // Example, would need a select input
});

let debounceTimer = null;

const applyFilters = () => {
  // Construct a clean filters object to pass to the store action
  const activeFilters = {};
  if (filters.title) activeFilters.title = filters.title;
  if (filters.status) activeFilters.status = filters.status;
  if (filters.companyId) activeFilters.companyId = filters.companyId;
  positionStore.fetchPositions(activeFilters);
};

const applyFiltersDebounced = () => {
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(() => {
    applyFilters();
  }, 500); // Debounce by 500ms
};


onMounted(() => {
  applyFilters(); // Fetch initial list (or with default filters)
});

const confirmDelete = (id) => {
  if (window.confirm('Are you sure you want to delete this position?')) {
    positionStore.deletePosition(id).catch(error => {
      alert(`Failed to delete position: ${positionStore.error}`);
    });
  }
};
</script>

<style scoped>
.position-list-view {
  padding: 20px;
  max-width: 1000px;
  margin: auto;
}
.actions {
  margin-bottom: 20px;
}
.filters {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}
.filters input, .filters select {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.loading, .error-message {
  margin-top: 20px;
}
.error-message {
  color: red;
}
.positions-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}
.positions-table th, .positions-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}
.positions-table th {
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
