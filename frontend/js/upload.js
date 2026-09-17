import { uploadDocument } from './api.js';

export function setupUpload({ form, dropZone, fileInput, chooseButton, selectedFile, message, onSuccess }) {
  let file;
  const setFile = (nextFile) => {
    file = nextFile;
    selectedFile.textContent = file ? `${file.name} (${Math.ceil(file.size / 1024)} KB)` : 'No file selected';
  };
  chooseButton.addEventListener('click', () => fileInput.click());
  fileInput.addEventListener('change', () => setFile(fileInput.files[0]));
  ['dragenter', 'dragover'].forEach((eventName) => dropZone.addEventListener(eventName, (event) => { event.preventDefault(); dropZone.classList.add('is-dragging'); }));
  ['dragleave', 'drop'].forEach((eventName) => dropZone.addEventListener(eventName, (event) => { event.preventDefault(); dropZone.classList.remove('is-dragging'); }));
  dropZone.addEventListener('drop', (event) => setFile(event.dataTransfer.files[0]));
  dropZone.addEventListener('keydown', (event) => { if (event.key === 'Enter' || event.key === ' ') fileInput.click(); });
  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const title = form.elements.title.value.trim();
    const category = form.elements.category.value.trim();
    const description = form.elements.description.value.trim();
    if (!file || !title || !category || !description) { message.textContent = 'Choose a file and complete all metadata fields.'; return; }
    message.textContent = 'Uploading...';
    try {
      await uploadDocument({ file, title, category, description, onProgress: (percent) => { message.textContent = `Uploading... ${percent}%`; } });
      message.textContent = 'Document uploaded successfully.';
      form.reset();
      setFile(undefined);
      onSuccess?.();
    } catch (error) { message.textContent = error.message; }
  });
}
