(() => {
  const SESSION_KEY = 'agrovalle.session';
  const MUNICIPALITIES = [
    'Alcalá', 'Andalucía', 'Ansermanuevo', 'Argelia', 'Bolívar', 'Buenaventura', 'Buga',
    'Bugalagrande', 'Caicedonia', 'Cali', 'Calima', 'Candelaria', 'Cartago', 'Dagua',
    'El Águila', 'El Cairo', 'El Cerrito', 'El Dovio', 'Florida', 'Ginebra', 'Guacarí',
    'Jamundí', 'La Cumbre', 'La Unión', 'La Victoria', 'Obando',
    'Palmira', 'Pradera', 'Restrepo', 'Riofrío', 'Roldanillo', 'San Pedro', 'Sevilla',
    'Toro', 'Trujillo', 'Tuluá', 'Ulloa', 'Versalles', 'Vijes', 'Yotoco', 'Yumbo',
    'Zarzal'
  ];

  const normalize = (value = '') => String(value).toLocaleLowerCase('es').normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '');

  const escapeHtml = (value = '') => String(value)
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');

  const readSession = () => {
    try {
      return JSON.parse(window.sessionStorage.getItem(SESSION_KEY)) || null;
    } catch {
      return null;
    }
  };

  const saveSession = (session) => {
    window.sessionStorage.setItem(SESSION_KEY, JSON.stringify(session));
  };

  const clearSession = () => {
    window.sessionStorage.removeItem(SESSION_KEY);
  };

  const responseMessage = (payload, fallback) => {
    if (!payload) return fallback;
    if (payload.errors && typeof payload.errors === 'object') {
      return Object.values(payload.errors).join('. ');
    }
    return payload.message || fallback;
  };

  const apiRequest = async (path, options = {}) => {
    const headers = new Headers(options.headers || {});
    headers.set('Accept', 'application/json');
    if (options.body && !headers.has('Content-Type')) {
      headers.set('Content-Type', 'application/json');
    }
    if (options.auth) {
      const session = readSession();
      if (!session?.accessToken) throw new Error('Debes iniciar sesión para continuar.');
      headers.set('Authorization', `Bearer ${session.accessToken}`);
    }
    const response = await fetch(path, { ...options, headers });
    const contentType = response.headers.get('content-type') || '';
    const payload = contentType.includes('application/json') ? await response.json() : null;
    if (!response.ok) {
      if (response.status === 401 && options.auth) clearSession();
      throw new Error(responseMessage(payload,
        `No fue posible completar la solicitud (${response.status}).`));
    }
    return payload;
  };

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

  const setFormMessage = (form, message, state = 'error') => {
    let output = form.querySelector('[data-form-message]');
    if (!output) {
      output = document.createElement('p');
      output.dataset.formMessage = '';
      output.className = 'form-message';
      output.setAttribute('role', 'status');
      form.prepend(output);
    }
    output.textContent = message;
    output.dataset.state = state;
    output.hidden = !message;
  };

  const setFormPending = (form, pending, label) => {
    const submit = form.querySelector('[type="submit"]');
    if (!submit) return;
    if (!submit.dataset.originalLabel) submit.dataset.originalLabel = submit.textContent;
    submit.disabled = pending;
    submit.textContent = pending ? label : submit.dataset.originalLabel;
  };

  const dashboardFor = (session) => session?.user?.rol === 'AGRICULTOR'
    ? 'agricultor.html' : 'catalogo.html';

  const safeNextPage = () => {
    const next = new URLSearchParams(window.location.search).get('next');
    return next && /^[a-z0-9-]+\.html(?:[?#].*)?$/i.test(next) ? next : null;
  };

  const currentPage = () => window.location.pathname.split('/').pop() || 'index.html';

  const enforceAccess = () => {
    const requiredRole = document.body.dataset.requiresRole;
    if (!requiredRole) return true;
    const session = readSession();
    if (!session?.accessToken) {
      window.location.replace(`login.html?next=${encodeURIComponent(currentPage())}`);
      return false;
    }
    if (session.user?.rol !== requiredRole) {
      window.location.replace(dashboardFor(session));
      return false;
    }
    return true;
  };

  const updateSessionUi = () => {
    const session = readSession();
    const displayName = session?.user?.nombre
      || session?.user?.correo?.split('@')[0] || 'Usuario';
    const initials = displayName.split(/\s+/).slice(0, 2)
      .map((part) => part[0]).join('').toUpperCase();

    document.querySelectorAll('[data-session-name]').forEach((element) => {
      element.textContent = displayName;
    });
    document.querySelectorAll('[data-session-email]').forEach((element) => {
      element.textContent = session?.user?.correo || '';
    });
    document.querySelectorAll('[data-session-initials]').forEach((element) => {
      element.textContent = initials || 'AV';
    });
    document.querySelectorAll('[data-session-link]').forEach((link) => {
      if (session?.accessToken) {
        link.href = dashboardFor(session);
        link.textContent = session.user?.rol === 'AGRICULTOR' ? 'Mi panel' : 'Mi cuenta';
      }
    });
    document.querySelectorAll('[data-guest-only]').forEach((element) => {
      element.hidden = Boolean(session?.accessToken);
    });
    document.querySelectorAll('[data-auth-only]').forEach((element) => {
      element.hidden = !session?.accessToken;
    });
  };

  const initializeMunicipalities = () => {
    document.querySelectorAll('[data-municipality-select]').forEach((select) => {
      const first = select.options[0];
      const placeholder = first?.textContent || 'Seleccionar';
      const selected = select.value;
      select.innerHTML = '';
      select.append(new Option(placeholder, ''));
      MUNICIPALITIES.forEach((municipality) => {
        select.append(new Option(municipality, municipality));
      });
      if (MUNICIPALITIES.includes(selected)) select.value = selected;
    });
  };

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

  document.addEventListener('click', (event) => {
    const logout = event.target.closest('[data-logout]');
    if (logout) {
      clearSession();
      window.location.assign('index.html');
      return;
    }
    const pendingFeature = event.target.closest('[data-demo-action]');
    if (pendingFeature) {
      showToast('Esta función pertenece al siguiente incremento de programación.');
    }
  });

  const initializeLogin = () => {
    const form = document.querySelector('[data-login-form]');
    if (!form) return;
    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      setFormMessage(form, '');
      setFormPending(form, true, 'Verificando…');
      const data = new FormData(form);
      try {
        const token = await apiRequest('/api/v1/auth/login', {
          method: 'POST',
          body: JSON.stringify({
            correo: data.get('correo'),
            contrasena: data.get('contrasena')
          })
        });
        saveSession({ accessToken: token.accessToken, user: {} });
        const user = await apiRequest('/api/v1/auth/me', { auth: true });
        saveSession({
          accessToken: token.accessToken,
          tokenType: token.tokenType,
          expiresInSeconds: token.expiresInSeconds,
          user
        });
        setFormMessage(form, 'Inicio de sesión correcto. Abriendo tu cuenta…', 'success');
        window.location.assign(safeNextPage() || dashboardFor(readSession()));
      } catch (error) {
        clearSession();
        setFormMessage(form, error.message);
      } finally {
        setFormPending(form, false, '');
      }
    });
  };

  const initializeRegistration = () => {
    const form = document.querySelector('[data-register-form]');
    if (!form) return;
    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      setFormMessage(form, '');
      setFormPending(form, true, 'Creando cuenta…');
      const data = new FormData(form);
      const credentials = {
        correo: data.get('correo'),
        contrasena: data.get('contrasena')
      };
      try {
        const registered = await apiRequest('/api/v1/auth/register', {
          method: 'POST',
          body: JSON.stringify({
            nombre: data.get('nombre'),
            correo: credentials.correo,
            contrasena: credentials.contrasena,
            cedula: data.get('documento'),
            ubicacionValle: data.get('municipio'),
            rol: data.get('rol')
          })
        });
        const token = await apiRequest('/api/v1/auth/login', {
          method: 'POST',
          body: JSON.stringify(credentials)
        });
        saveSession({ accessToken: token.accessToken, user: {} });
        const identity = await apiRequest('/api/v1/auth/me', { auth: true });
        saveSession({
          accessToken: token.accessToken,
          tokenType: token.tokenType,
          expiresInSeconds: token.expiresInSeconds,
          user: { ...identity, nombre: registered.nombre }
        });
        setFormMessage(form, 'Cuenta creada correctamente. Abriendo tu perfil…', 'success');
        window.location.assign(dashboardFor(readSession()));
      } catch (error) {
        clearSession();
        setFormMessage(form, error.message);
      } finally {
        setFormPending(form, false, '');
      }
    });
  };

  const initializeFarmForm = () => {
    const form = document.querySelector('[data-farm-form]');
    if (!form) return;
    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      setFormMessage(form, '');
      setFormPending(form, true, 'Guardando finca…');
      const data = new FormData(form);
      try {
        await apiRequest('/api/v1/fincas', {
          method: 'POST',
          auth: true,
          body: JSON.stringify({
            nombre: data.get('nombre'),
            municipio: data.get('municipio'),
            descripcion: data.get('descripcion')
          })
        });
        setFormMessage(form,
          'Finca registrada. Ya puedes publicar su primera cosecha.', 'success');
        window.setTimeout(() => window.location.assign('publicar-cosecha.html'), 900);
      } catch (error) {
        setFormMessage(form, error.message);
      } finally {
        setFormPending(form, false, '');
      }
    });
  };

  const colombiaToday = () => {
    const parts = new Intl.DateTimeFormat('en', {
      timeZone: 'America/Bogota', year: 'numeric', month: '2-digit', day: '2-digit'
    }).formatToParts(new Date());
    const values = Object.fromEntries(parts.map((part) => [part.type, part.value]));
    return `${values.year}-${values.month}-${values.day}`;
  };

  const initializeHarvestForm = async () => {
    const form = document.querySelector('[data-harvest-form]');
    if (!form) return;
    const farmSelect = form.querySelector('[name="fincaId"]');
    const submit = form.querySelector('[type="submit"]');
    const date = form.querySelector('[name="fechaCosecha"]');
    if (date) date.min = colombiaToday();
    try {
      const farms = await apiRequest('/api/v1/fincas', { auth: true });
      farmSelect.innerHTML = '<option value="">Seleccionar finca</option>';
      farms.forEach((farm) => {
        const option = document.createElement('option');
        option.value = farm.id;
        option.textContent = `${farm.nombre} · ${farm.municipio}`;
        farmSelect.append(option);
      });
      const farmNotice = document.querySelector('[data-farm-notice]');
      if (farms.length === 0) {
        if (farmNotice) farmNotice.hidden = false;
        submit.disabled = true;
      }
    } catch (error) {
      setFormMessage(form, error.message);
      submit.disabled = true;
    }

    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      setFormMessage(form, '');
      setFormPending(form, true, 'Publicando…');
      const data = new FormData(form);
      try {
        await apiRequest('/api/v1/productos', {
          method: 'POST',
          auth: true,
          body: JSON.stringify({
            fincaId: Number(data.get('fincaId')),
            tipo: data.get('tipo'),
            categoria: data.get('categoria'),
            cantidadKg: Number(data.get('cantidadKg')),
            precioCopKg: Number(data.get('precioCopKg')),
            fechaCosecha: data.get('fechaCosecha')
          })
        });
        setFormMessage(form,
          'Cosecha publicada correctamente. Abriendo el catálogo…', 'success');
        window.setTimeout(() => window.location.assign('catalogo.html'), 900);
      } catch (error) {
        setFormMessage(form, error.message);
      } finally {
        setFormPending(form, false, '');
      }
    });
  };

  const formatCop = (value) => new Intl.NumberFormat('es-CO', {
    style: 'currency', currency: 'COP', maximumFractionDigits: 0
  }).format(Number(value));

  const formatKg = (value) => new Intl.NumberFormat('es-CO', {
    maximumFractionDigits: 3
  }).format(Number(value));

  const mediaClass = (item) => {
    const text = normalize(`${item.tipo} ${item.categoria}`);
    if (text.includes('mango')) return 'media--mango';
    if (text.includes('platano') || text.includes('banano')) return 'media--platano';
    if (text.includes('tomate')) return 'media--tomate';
    if (text.includes('aguacate')) return 'media--aguacate';
    if (text.includes('pimenton')) return 'media--pimenton';
    if (text.includes('limon')) return 'media--limon';
    return 'media--harvest';
  };

  const productCard = (item) => `
    <article class="product-card" data-product-card
      data-productor="${escapeHtml(item.agricultorNombre)}">
      <div class="product-media ${mediaClass(item)}">
        <span class="product-badge">Cosecha publicada</span>
      </div>
      <div class="product-body">
        <p class="product-meta">${escapeHtml(item.categoria)} · ${escapeHtml(item.municipio)}</p>
        <h3>${escapeHtml(item.tipo)}</h3>
        <p class="product-price">${formatCop(item.precioCopKg)} <small>COP/kg</small></p>
        <p class="product-origin">${escapeHtml(item.agricultorNombre)} ·
          ${escapeHtml(item.fincaNombre)} · ${formatKg(item.cantidadKg)} kg</p>
        <p class="product-date">Cosecha: ${escapeHtml(item.fechaCosecha)}</p>
        <div class="product-actions">
          <button class="button button--light button--small" type="button"
            data-demo-action>Contactar</button>
          <button class="button button--small" type="button" data-demo-action>Reservar</button>
        </div>
      </div>
    </article>`;

  const initializeCatalog = () => {
    const form = document.querySelector('[data-catalog-filters]');
    const grid = document.querySelector('[data-catalog-grid]');
    const count = document.querySelector('[data-result-count]');
    const empty = document.querySelector('[data-empty-state]');
    if (!form || !grid) return;
    let items = [];

    const render = () => {
      const query = normalize(form.elements.buscar?.value.trim());
      const producer = normalize(form.elements.productor?.value);
      const visible = items.filter((item) => (!query || normalize(item.tipo).includes(query))
        && (!producer || normalize(item.agricultorNombre) === producer));
      grid.querySelectorAll('[data-product-card]').forEach((card) => card.remove());
      grid.insertAdjacentHTML('afterbegin', visible.map(productCard).join(''));
      if (count) {
        count.textContent = `${visible.length} cosecha${visible.length === 1 ? '' : 's'} disponible${visible.length === 1 ? '' : 's'}`;
      }
      if (empty) empty.hidden = visible.length !== 0;
    };

    const updateProducers = () => {
      const select = form.elements.productor;
      if (!select) return;
      const selected = select.value;
      const producers = [...new Set(items.map((item) => item.agricultorNombre))].sort();
      select.innerHTML = '<option value="">Todos</option>';
      producers.forEach((producer) => {
        const option = document.createElement('option');
        option.value = producer;
        option.textContent = producer;
        select.append(option);
      });
      if (producers.includes(selected)) select.value = selected;
    };

    const load = async () => {
      grid.dataset.loading = 'true';
      if (count) count.textContent = 'Consultando cosechas…';
      const params = new URLSearchParams();
      if (form.elements.municipio?.value) {
        params.set('municipio', form.elements.municipio.value);
      }
      if (form.elements.categoria?.value) {
        params.set('categoria', form.elements.categoria.value);
      }
      try {
        items = await apiRequest(`/api/v1/productos${params.size ? `?${params}` : ''}`);
        updateProducers();
        render();
      } catch (error) {
        items = [];
        render();
        if (count) count.textContent = 'No fue posible consultar el catálogo';
        showToast(error.message);
      } finally {
        grid.dataset.loading = 'false';
      }
    };

    const params = new URLSearchParams(window.location.search);
    ['buscar', 'municipio', 'categoria'].forEach((name) => {
      const field = form.elements[name];
      if (field && params.get(name)) field.value = params.get(name);
    });
    form.elements.buscar?.addEventListener('input', render);
    form.elements.productor?.addEventListener('change', render);
    form.elements.municipio?.addEventListener('change', load);
    form.elements.categoria?.addEventListener('change', load);
    form.addEventListener('reset', () => window.setTimeout(load));
    load();
  };

  const initializeFarmerDashboard = async () => {
    const body = document.querySelector('[data-my-products-body]');
    if (!body) return;
    const session = readSession();
    try {
      const [products, farms] = await Promise.all([
        apiRequest('/api/v1/productos'),
        apiRequest('/api/v1/fincas', { auth: true })
      ]);
      const mine = products.filter((item) => item.agricultorId === session.user.id);
      const productCount = document.querySelector('[data-product-count]');
      const farmCount = document.querySelector('[data-farm-count]');
      if (productCount) productCount.textContent = String(mine.length);
      if (farmCount) farmCount.textContent = String(farms.length);
      body.innerHTML = mine.length ? mine.map((item) => `
        <tr><td>${escapeHtml(item.tipo)}</td><td>${formatKg(item.cantidadKg)} kg</td>
        <td>${formatCop(item.precioCopKg)} COP/kg</td>
        <td><span class="pill">Publicado</span></td></tr>`).join('')
        : '<tr><td colspan="4">Aún no has publicado cosechas.</td></tr>';
    } catch (error) {
      body.innerHTML = `<tr><td colspan="4">${escapeHtml(error.message)}</td></tr>`;
      if (!readSession()) {
        window.location.replace(`login.html?next=${encodeURIComponent(currentPage())}`);
      }
    }
  };

  document.querySelectorAll('[data-demo-form]').forEach((form) => {
    form.addEventListener('submit', (event) => {
      event.preventDefault();
      showToast('Esta función está diseñada y se programará en el siguiente incremento.');
    });
  });

  const trackingForm = document.querySelector('[data-tracking-form]');
  if (trackingForm) {
    trackingForm.addEventListener('submit', (event) => {
      event.preventDefault();
      showToast('El seguimiento real se habilitará cuando programemos pedidos y entregas.');
    });
  }

  const notificationButton = document.querySelector('[data-notifications]');
  if (notificationButton) {
    notificationButton.addEventListener('click', () => {
      showToast('Las notificaciones se conectarán en el siguiente incremento.');
    });
  }

  document.querySelectorAll('[data-api-status]').forEach(async (status) => {
    try {
      const payload = await apiRequest('/api/v1/estado');
      status.dataset.state = payload.estado === 'OK' ? 'ok' : 'idle';
      status.textContent = payload.estado === 'OK'
        ? 'Servicio conectado' : 'Servicio en revisión';
    } catch {
      status.dataset.state = 'idle';
      status.textContent = 'Servicio no disponible';
    }
  });

  updateSessionUi();
  if (!enforceAccess()) return;
  initializeMunicipalities();
  initializeLogin();
  initializeRegistration();
  initializeFarmForm();
  initializeHarvestForm();
  initializeCatalog();
  initializeFarmerDashboard();
})();
