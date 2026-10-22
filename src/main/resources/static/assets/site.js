(() => {
  const menuButton = document.querySelector('[data-menu-toggle]');
  const menu = document.querySelector('[data-nav]');

  if (menuButton && menu) {
    menuButton.addEventListener('click', () => {
      const isOpen = menu.dataset.open === 'true';
      menu.dataset.open = String(!isOpen);
      menuButton.setAttribute('aria-expanded', String(!isOpen));
    });
  }

  const sidebarButton = document.querySelector('[data-sidebar-toggle]');
  const sidebar = document.querySelector('[data-sidebar]');

  if (sidebarButton && sidebar) {
    sidebarButton.addEventListener('click', () => {
      const isOpen = sidebar.dataset.open === 'true';
      sidebar.dataset.open = String(!isOpen);
      sidebarButton.setAttribute('aria-expanded', String(!isOpen));
    });
  }

  let toastTimer;
  const showToast = (message) => {
    let toast = document.querySelector('[data-toast]');
    if (!toast) {
      toast = document.createElement('div');
      toast.className = 'toast';
      toast.dataset.toast = '';
      toast.setAttribute('role', 'status');
      toast.setAttribute('aria-live', 'polite');
      document.body.append(toast);
    }
    toast.textContent = message;
    toast.dataset.visible = 'true';
    window.clearTimeout(toastTimer);
    toastTimer = window.setTimeout(() => {
      toast.dataset.visible = 'false';
    }, 4200);
  };

  document.querySelectorAll('[data-demo-action]').forEach((button) => {
    button.addEventListener('click', () => {
      const product = button.dataset.product || 'esta cosecha';
      showToast(`Reserva de ${product} preparada. Inicia sesión para confirmarla.`);
    });
  });

  const filterForm = document.querySelector('[data-catalog-filters]');
  const cards = [...document.querySelectorAll('[data-product-card]')];
  const count = document.querySelector('[data-result-count]');
  const empty = document.querySelector('[data-empty-state]');

  const normalize = (value) => value.toLocaleLowerCase('es').normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '');

  const applyFilters = () => {
    if (!filterForm || cards.length === 0) return;
    const query = normalize(filterForm.querySelector('[name="buscar"]')?.value.trim() || '');
    const municipality = normalize(filterForm.querySelector('[name="municipio"]')?.value || '');
    const category = normalize(filterForm.querySelector('[name="categoria"]')?.value || '');
    const producer = normalize(filterForm.querySelector('[name="productor"]')?.value || '');
    let visible = 0;

    cards.forEach((card) => {
      const haystack = normalize(card.textContent);
      const matches = (!query || haystack.includes(query))
        && (!municipality || normalize(card.dataset.municipio) === municipality)
        && (!category || normalize(card.dataset.categoria) === category)
        && (!producer || normalize(card.dataset.productor) === producer);
      card.hidden = !matches;
      if (matches) visible += 1;
    });

    if (count) count.textContent = `${visible} cosecha${visible === 1 ? '' : 's'} disponible${visible === 1 ? '' : 's'}`;
    if (empty) empty.dataset.visible = String(visible === 0);
  };

  if (filterForm) {
    filterForm.addEventListener('input', applyFilters);
    filterForm.addEventListener('change', applyFilters);
    filterForm.addEventListener('reset', () => window.setTimeout(applyFilters));

    const params = new URLSearchParams(window.location.search);
    ['buscar', 'municipio', 'categoria'].forEach((name) => {
      const value = params.get(name);
      const field = filterForm.querySelector(`[name="${name}"]`);
      if (field && value) field.value = value;
    });
    applyFilters();
  }

  document.querySelectorAll('[data-demo-form]').forEach((form) => {
    form.addEventListener('submit', (event) => {
      event.preventDefault();
      const message = form.dataset.success
        || 'La información quedó validada en este prototipo.';
      showToast(message);
    });
  });

  const trackingForm = document.querySelector('[data-tracking-form]');
  const trackingResult = document.querySelector('[data-tracking-result]');
  if (trackingForm && trackingResult) {
    trackingForm.addEventListener('submit', (event) => {
      event.preventDefault();
      trackingResult.dataset.visible = 'true';
      trackingResult.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    });
  }

  const notificationButton = document.querySelector('[data-notifications]');
  if (notificationButton) {
    notificationButton.addEventListener('click', () => {
      const badge = notificationButton.querySelector('.notification-count');
      if (badge) {
        badge.textContent = '0';
        badge.hidden = true;
      }
      showToast('No quedan mensajes nuevos. Se conservaron en el historial.');
    });
  }

  document.querySelectorAll('[data-api-status]').forEach(async (status) => {
    try {
      const response = await fetch('/api/v1/estado', { headers: { Accept: 'application/json' } });
      if (!response.ok) throw new Error('Respuesta no disponible');
      const payload = await response.json();
      status.dataset.state = payload.estado === 'OK' ? 'ok' : 'idle';
      status.textContent = payload.estado === 'OK' ? 'Servicio conectado' : 'Servicio en revisión';
    } catch {
      status.dataset.state = 'idle';
      status.textContent = 'Vista de prototipo';
    }
  });
})();
