import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia' // Import Pinia

const app = createApp(App)
app.use(router)
app.use(createPinia()) // Use Pinia
app.mount('#app')
