import { listDocuments } from './api.js';

export function setupDashboard({ grid, searchInput, categoryFilter, message }) {
  let state = { search: '', category: '', page: 0 };
  let timer;
  async function refresh() {
    message.textContent = 'Loading documents...';
    try {
      const result = await listDocuments(state);
      const documents = result.content || result.documents || [];
      grid.innerHTML = documents.length ? documents.map(renderCard).join('') : '<div class="empty-state">No documents match your current filters.</div>';
      message.textContent = documents.length ? `${result.totalElements ?? documents.length} document(s)` : '';
      const categories = [...new Set(documents.map((document) => document.category).filter(Boolean))];
      const selected = categoryFilter.value;
      categoryFilter.innerHTML = '<option value="">All categories</option>' + categories.map((category) => `<option value="${escapeHtml(category)}">${escapeHtml(category)}</option>`).join('');
      categoryFilter.value = selected;
    } catch (error) { grid.innerHTML = '<div class="empty-state">Documents could not be loaded.</div>'; message.textContent = error.message; }
  }
  function scheduleRefresh() { clearTimeout(timer); timer = setTimeout(refresh, 250); }
  searchInput.addEventListener('input', () => { state = { ...state, search: searchInput.value, page: 0 }; scheduleRefresh(); });
  categoryFilter.addEventListener('change', () => { state = { ...state, category: categoryFilter.value, page: 0 }; refresh(); });
  refresh();
  return { refresh };
}

function renderCard(document) {
  const url = escapeHtml(document.cloudUrl || '#');
  return `<article class="document-card"><div><p class="eyebrow">${escapeHtml(document.category || 'Uncategorized')}</p><h3>${escapeHtml(document.title || document.fileName)}</h3><div class="card-meta"><p>${escapeHtml(document.fileName || '')}</p><p>${escapeHtml(document.type || '')} · ${formatSize(document.size)}</p><p>${formatDate(document.uploadTimestamp)}</p></div></div><div class="card-actions"><a href="${url}" target="_blank" rel="noopener">View</a><a href="${url}" download>Download</a></div></article>`;
}
function escapeHtml(value) { return String(value).replace(/[&<>'"]/g, (character) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[character])); }
function formatSize(bytes) { if (!bytes) return 'Unknown size'; const units = ['B', 'KB', 'MB', 'GB']; let index = 0; let size = bytes; while (size >= 1024 && index < units.length - 1) { size /= 1024; index += 1; } return `${size.toFixed(index ? 1 : 0)} ${units[index]}`; }
function formatDate(value) { return value ? new Date(value).toLocaleString() : 'Unknown upload time'; }
