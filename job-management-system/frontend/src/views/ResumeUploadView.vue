<template>
  <div class="resume-upload-view">
    <h1>Upload New Resume</h1>
    <form @submit.prevent="handleSubmit" class="upload-form">
      <div class="form-group">
        <label for="resumeFile">Select Resume File:</label>
        <input type="file" id="resumeFile" @change="handleFileChange" accept=".pdf,.doc,.docx,.txt" required />
        <p class="file-info" v-if="selectedFile">Selected file: {{ selectedFile.name }} ({{ (selectedFile.size / 1024).toFixed(2) }} KB)</p>
      </div>

      <div class="form-group">
        <label for="originalFileName">Save as (optional, defaults to filename):</label>
        <input type="text" id="originalFileName" v-model="customFileName" placeholder="e.g., My Latest CV.pdf" />
      </div>

      <div v-if="resumeStore.error" class="error-message">
        <p>Error: {{ resumeStore.error }}</p>
      </div>
       <div v-if="uploadProgress > 0 && uploadProgress < 100" class="progress-bar">
        Uploading: {{ uploadProgress }}%
      </div>
      <div v-if="successMessage" class="success-message">{{ successMessage }}</div>


      <button type="submit" :disabled="!selectedFile || resumeStore.loading" class="btn btn-primary">
        {{ resumeStore.loading ? 'Uploading...' : 'Upload Resume' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useResumeStore } from '../stores/resumeStore';
import { useAuthStore } from '../stores/authStore';
import { useRouter } from 'vue-router';

const resumeStore = useResumeStore();
const authStore = useAuthStore(); // To ensure user is logged in
const router = useRouter();

const selectedFile = ref(null);
const customFileName = ref('');
const uploadProgress = ref(0); // For potential progress bar, not fully implemented with current apiClient
const successMessage = ref('');


const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    // Basic client-side validation (example)
    const allowedTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'text/plain'];
    if (!allowedTypes.includes(file.type)) {
      alert('Invalid file type. Please upload PDF, DOC, DOCX, or TXT.');
      event.target.value = null; // Clear the input
      selectedFile.value = null;
      return;
    }
    if (file.size > 5 * 1024 * 1024) { // 5MB limit example
      alert('File is too large. Maximum size is 5MB.');
      event.target.value = null;
      selectedFile.value = null;
      return;
    }
    selectedFile.value = file;
    if (!customFileName.value) { // Pre-fill custom name if empty
        customFileName.value = file.name;
    }
  } else {
    selectedFile.value = null;
  }
};

const handleSubmit = async () => {
  if (!selectedFile.value) {
    alert('Please select a file to upload.');
    return;
  }
  if (!authStore.isAuthenticated) {
    alert('You must be logged in to upload a resume.');
    router.push('/login'); // Redirect to login if not authenticated
    return;
  }

  const formData = new FormData();
  formData.append('file', selectedFile.value);
  // Only append originalFileName if user has provided a custom one
  if (customFileName.value && customFileName.value !== selectedFile.value.name) {
    formData.append('originalFileName', customFileName.value);
  } else {
    formData.append('originalFileName', selectedFile.value.name); // Default to actual filename
  }
  // UserId is handled by backend based on auth token

  successMessage.value = '';
  resumeStore.error = null; // Clear previous errors

  try {
    await resumeStore.uploadResume(formData);
    successMessage.value = 'Resume uploaded successfully!';
    setTimeout(() => {
        router.push({ name: 'resume-list' }); // Redirect after a short delay
    }, 1500);
  } catch (error) {
    // Error is already set in the store and displayed by the template
    console.error("Failed to upload resume from view:", error);
    // alert(`Upload failed: ${resumeStore.error}`); // Redundant if template shows error
  }
};
</script>

<style scoped>
.resume-upload-view {
  max-width: 600px;
  margin: auto;
  padding: 20px;
}
.upload-form .form-group {
  margin-bottom: 20px;
}
.upload-form label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
}
.upload-form input[type="file"],
.upload-form input[type="text"] {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}
.file-info {
  font-size: 0.9em;
  margin-top: 5px;
  color: #555;
}
.error-message, .success-message {
  padding: 10px;
  margin-bottom: 15px;
  border-radius: 4px;
  text-align: center;
}
.error-message {
  color: #721c24;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
}
.success-message {
  color: #155724;
  background-color: #d4edda;
  border: 1px solid #c3e6cb;
}
.progress-bar { /* Basic styling for progress */
    width: 100%; background-color: #e0e0e0; border-radius: 4px; padding: 3px; text-align: center; margin-bottom: 10px;
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
