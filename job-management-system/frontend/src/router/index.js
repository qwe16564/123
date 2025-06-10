import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
// Import other views - create them as placeholders if they don't exist
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DashboardView from '../views/DashboardView.vue'
import CompanyListView from '../views/CompanyListView.vue'
import CompanyCreateView from '../views/CompanyCreateView.vue'
import CompanyDetailView from '../views/CompanyDetailView.vue'
import CompanyEditView from '../views/CompanyEditView.vue'
import PositionListView from '../views/PositionListView.vue'
import PositionCreateView from '../views/PositionCreateView.vue'
import PositionDetailView from '../views/PositionDetailView.vue'
import PositionEditView from '../views/PositionEditView.vue'
import ResumeListView from '../views/ResumeListView.vue'
import ResumeUploadView from '../views/ResumeUploadView.vue'
import MyApplicationsView from '../views/MyApplicationsView.vue' // Renamed
import PositionApplicationsView from '../views/PositionApplicationsView.vue'; // New View
import AboutView from '../views/AboutView.vue' // Keep if still used

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/about',
    name: 'about',
    component: AboutView
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: DashboardView,
    meta: { requiresAuth: true }
  },
  {
    path: '/companies',
    name: 'company-list',
    component: CompanyListView
  },
  {
    path: '/companies/new',
    name: 'company-create',
    component: CompanyCreateView,
    meta: { requiresAuth: true }
  },
  {
    path: '/companies/:id',
    name: 'company-detail',
    component: CompanyDetailView,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/companies/:id/edit',
    name: 'company-edit',
    component: CompanyEditView,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/positions',
    name: 'position-list',
    component: PositionListView
  },
  {
    path: '/positions/new',
    name: 'position-create',
    component: PositionCreateView,
    meta: { requiresAuth: true }
  },
  {
    path: '/positions/:id',
    name: 'position-detail',
    component: PositionDetailView,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/positions/:id/edit',
    name: 'position-edit',
    component: PositionEditView,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/resumes',
    name: 'resume-list',
    component: ResumeListView,
    meta: { requiresAuth: true }
  },
  {
    path: '/resumes/upload',
    name: 'resume-upload',
    component: ResumeUploadView,
    meta: { requiresAuth: true }
  },
  {
    path: '/my-applications', // Changed path for clarity
    name: 'my-application-list', // Renamed route name
    component: MyApplicationsView,
    meta: { requiresAuth: true }
  },
  {
    path: '/positions/:id/applications', // Route for viewing applications for a specific position
    name: 'position-applications',
    component: PositionApplicationsView,
    props: true, // Passes route.params.id as a prop to the component
    meta: { requiresAuth: true } // Should be protected, accessible by recruiters/admins
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// Navigation Guards
import { useAuthStore } from '../stores/authStore';

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  // Initialize auth store from localStorage if not already initialized by App.vue
  // This is a fallback if App.vue's onMounted hasn't run yet or if user navigates directly.
  if (authStore.token === null && localStorage.getItem('token')) {
      authStore.initializeAuth();
  }

  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const isAuthenticated = authStore.isAuthenticated;

  if (requiresAuth && !isAuthenticated) {
    // Redirect to login if route requires auth and user is not authenticated
    next({ name: 'login', query: { redirect: to.fullPath } });
  } else if ((to.name === 'login' || to.name === 'register') && isAuthenticated) {
    // Redirect to dashboard if user is authenticated and tries to access login/register
    next({ name: 'dashboard' });
  } else {
    // Proceed as normal
    next();
  }
});

export default router
