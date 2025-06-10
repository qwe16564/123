<template>
  <div class="login-view">
    <h1>Login</h1>
    <!-- Login form will go here -->
    <form @submit.prevent="handleLogin">
      <div>
        <label for="username">Username:</label>
        <input type="text" id="username" v.model="username" />
      </div>
      <div>
        <label for="password">Password:</label>
        <input type="password" id="password" v.model="password" />
      </div>
      <button type="submit">Login</button>
      <p v-if="errorMessage">{{ errorMessage }}</p>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/authStore';

const username = ref('');
const password = ref('');
const errorMessage = ref('');
const router = useRouter();
const authStore = useAuthStore();

const handleLogin = async () => {
  try {
    await authStore.loginAction({ username: username.value, password: password.value });
    router.push('/dashboard'); // Redirect to dashboard on successful login
  } catch (error) {
    errorMessage.value = error.message || 'Failed to login. Please check your credentials.';
  }
};
</script>

<style scoped>
.login-view {
  max-width: 400px;
  margin: auto;
  padding: 20px;
}
form div {
  margin-bottom: 10px;
}
</style>
