const API_BASE_URL = window.CLOUDDOC_API_BASE_URL || 'http://localhost:8081/api';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, options);
  const body = response.status === 204 ? null : await response.json().catch(() => null);
  if (!response.ok) {
    throw new Error(body?.message || 'The request could not be completed.');
  }
  return body;
}

export function uploadDocument({ file, title, category, description, onProgress }) {
  return new Promise((resolve, reject) => {
    const request = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('file', file);
    formData.append('metadata', new Blob([JSON.stringify({ title, category, description })], {
      type: 'application/json'
    }));
    request.open('POST', `${API_BASE_URL}/documents`);
    request.upload.addEventListener('progress', (event) => {
      if (event.lengthComputable && onProgress) onProgress(Math.round((event.loaded / event.total) * 100));
    });
    request.addEventListener('load', () => {
      const body = JSON.parse(request.responseText || 'null');
      if (request.status >= 200 && request.status < 300) resolve(body);
      else reject(new Error(body?.message || 'Upload failed.'));
    });
    request.addEventListener('error', () => reject(new Error('Network error while uploading.')));
    request.send(formData);
  });
}

export function listDocuments({ search = '', category = '', page = 0, size = 24 } = {}) {
  const params = new URLSearchParams({ page, size });
  if (search.trim()) params.set('search', search.trim());
  if (category) params.set('category', category);
  return request(`/documents?${params}`);
}
