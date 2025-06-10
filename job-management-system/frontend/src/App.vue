<template>
  <div id="app">
    <nav class="main-nav">
      <router-link to="/">Home</router-link>
      <router-link to="/dashboard">Dashboard</router-link>
      <router-link to="/companies">Companies</router-link>
      <router-link to="/positions">Positions</router-link>

      <template v-if="authStore.isAuthenticated">
        <router-link to="/resumes">My Resumes</router-link>
        <router-link to="/my-applications">My Applications</router-link> {/* Changed path */}
        <a href="#" @click.prevent="handleLogout">Logout ({{ authStore.currentUser?.username }})</a>
      </template>
      <template v-else>
        <router-link to="/login">Login</router-link>
        <router-link to="/register">Register</router-link>
      </template>
      <router-link to="/about">About</router-link>
    </nav>
    <hr/>
    <router-view v-slot="{ Component }">
      <Suspense>
        <component :is="Component" />
      </Suspense>
    </router-view>
  </div>
</template>

<script setup>
import { useAuthStore } from './stores/authStore';
import { useRouter } from 'vue-router';
import { onMounted } from 'vue';

const authStore = useAuthStore();
const router = useRouter();

onMounted(() => {
  // Initialize auth state from localStorage when app mounts
  authStore.initializeAuth();
});

const handleLogout = () => {
  authStore.logoutAction();
  router.push('/login'); // Redirect to login after logout
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

.main-nav {
  padding: 15px;
  text-align: center;
  background-color: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
}

.main-nav a {
  font-weight: bold;
  color: #2c3e50;
  margin: 0 10px;
  text-decoration: none;
}

.main-nav a.router-link-exact-active {
  color: #42b983;
  border-bottom: 2px solid #42b983;
}

hr {
  margin-bottom: 20px;
}
</style>
