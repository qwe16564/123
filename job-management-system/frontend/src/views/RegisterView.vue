<template>
  <div class="register-view">
    <h1>Register</h1>
    <form @submit.prevent="handleRegister">
      <div>
        <label for="username">Username:</label>
        <input type="text" id="username" v-model="form.username" required />
      </div>
      <div>
        <label for="email">Email:</label>
        <input type="email" id="email" v-model="form.email" required />
      </div>
      <div>
        <label for="password">Password:</label>
        <input type="password" id="password" v-model="form.password" required />
      </div>
      <div>
        <label for="role">Role:</label>
        <select id="role" v-model="form.role" required>
          <option value="applicant">Applicant</option>
          <option value="recruiter">Recruiter</option>
          <!-- <option value="admin">Admin</option> --> {/* Admin role might be set differently */}
        </select>
      </div>
      <button type="submit" :disabled="isLoading">
        {{ isLoading ? 'Registering...' : 'Register' }}
      </button>
      <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/authStore';

const form = reactive({
  username: '',
  email: '',
  password: '',
  role: 'applicant', // Default role
});
const isLoading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');
const router = useRouter();
const authStore = useAuthStore();

const handleRegister = async () => {
  isLoading.value = true;
  errorMessage.value = '';
  successMessage.value = '';
  try {
    await authStore.registerAction(form);
    successMessage.value = 'Registration successful! Please login.';
    // Optionally clear form or redirect after a delay
    setTimeout(() => {
      router.push('/login');
    }, 2000);
  } catch (error) {
    errorMessage.value = error.message || 'Failed to register. Please try again.';
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.register-view {
  max-width: 400px;
  margin: auto;
  padding: 20px;
}
form div {
  margin-bottom: 10px;
}
.error-message {
  color: red;
}
.success-message {
  color: green;
}
</style>
