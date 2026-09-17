import { setupDashboard } from './dashboard.js';
import { setupUpload } from './upload.js';

const dashboard = setupDashboard({
  grid: document.querySelector('#document-grid'),
  searchInput: document.querySelector('#search-input'),
  categoryFilter: document.querySelector('#category-filter'),
  message: document.querySelector('#library-message')
});
setupUpload({
  form: document.querySelector('#upload-form'),
  dropZone: document.querySelector('#drop-zone'),
  fileInput: document.querySelector('#file-input'),
  chooseButton: document.querySelector('#choose-file'),
  selectedFile: document.querySelector('#selected-file'),
  message: document.querySelector('#upload-message'),
  onSuccess: dashboard.refresh
});
