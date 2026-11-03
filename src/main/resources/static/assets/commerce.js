(() => {
  'use strict';
  const escape = (value = '') => String(value).replace(/[&<>"']/g,
    (char) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[char]));
  const session = () => {
    try { return JSON.parse(sessionStorage.getItem('agrovalle.session')) || {}; }
    catch { return {}; }
  };
  const requiredRole = document.body.dataset.requiresRole;
  if ((requiredRole || document.body.hasAttribute('data-requires-auth'))
      && (!session().accessToken || (requiredRole && session().user?.rol !== requiredRole))) return;
  const money = (value) => Number(value).toLocaleString('es-CO', {
    minimumFractionDigits: 2, maximumFractionDigits: 2
  }) + ' COP';
  const kg = (value) => Number(value).toLocaleString('es-CO', { maximumFractionDigits: 3 });
  const date = (value) => value ? new Date(value).toLocaleString('es-CO', {
    timeZone: 'America/Bogota', dateStyle: 'medium', timeStyle: 'short'
  }) + ' (Colombia)' : 'Sin programar';
  const labels = { PENDIENTE: 'Pendiente', LISTO_PARA_DESPACHO: 'Listo para despacho',
    EN_CAMINO: 'En camino', ENTREGADO: 'Entregado', CANCELADA: 'Cancelado' };
  const request = async (path, options = {}) => {
    const { public: isPublic, ...config } = options;
    const headers = { Accept: 'application/json', ...config.headers };
    if (!isPublic) {
      if (!session().accessToken) throw new Error('Inicia sesión para continuar.');
      headers.Authorization = 'Bearer ' + session().accessToken;
    }
    if (config.body) headers['Content-Type'] = 'application/json';
    const response = await fetch('/api/v1' + path, { ...config, headers });
    const data = await response.json().catch(() => null);
    if (!response.ok) {
      if (response.status === 401) sessionStorage.removeItem('agrovalle.session');
      throw new Error(data?.message || Object.values(data?.errors || {}).join('. ')
        || 'No se pudo completar la operación. Intenta de nuevo.');
    }
    return data;
  };
  const feedback = (form, message, ok = false) => {
    const output = form.querySelector('[data-feedback]');
    if (!output) return;
    output.textContent = message;
    output.dataset.state = ok ? 'success' : 'error';
  };
  const action = async (form, work) => {
    const button = form.querySelector('[type="submit"]');
    if (button.disabled) return;
    button.disabled = true;
    feedback(form, '');
    try { await work(); } catch (error) { feedback(form, error.message); }
    finally { button.disabled = false; }
  };
  const output = '<p class="form-message" data-feedback role="status" aria-live="polite"></p>';

  const reserve = document.querySelector('[data-reserve-form]');
  if (reserve) {
    reserve.addEventListener('submit', (event) => {
      event.preventDefault();
      if (!session().accessToken) {
        location.assign('login.html?next=' + encodeURIComponent(location.pathname.split('/').pop()
          + location.search));
        return;
      }
      action(reserve, async () => {
        const id = Number(new URLSearchParams(location.search).get('id'));
        if (!Number.isSafeInteger(id) || id <= 0) throw new Error('Selecciona una cosecha válida.');
        const cart = await request('/carrito/items', { method: 'POST',
          body: JSON.stringify({ cosechaId: id, cantidadKg: reserve.elements.cantidad.value }) });
        const item = cart.items.find((entry) => entry.cosechaId === id);
        feedback(reserve, 'Reserva guardada hasta ' + date(item.venceEn)
          + '. Revisa el carrito para confirmar el pedido.', true);
        const offer = await request('/productos/' + id, { public: true });
        const available = document.querySelector('[data-availability]');
        if (available) available.textContent = kg(offer.cantidadKg) + ' kg';
      });
    });
  }

  let cartTimer;
  const cartRoot = document.querySelector('[data-cart]');
  const loadCart = async () => {
    if (!cartRoot) return;
    clearInterval(cartTimer);
    try {
      const cart = await request('/carrito');
      if (!cart.items.length) {
        cartRoot.innerHTML = '<h2>Tu carrito está vacío</h2><p>Elige una cosecha para reservarla.'
          + '</p><a class="button" href="catalogo.html">Explorar cosechas</a>';
        return;
      }
      const offset = new Date(cart.fechaConsulta).getTime() - Date.now();
      cartRoot.innerHTML = cart.items.map((item) => '<article class="commerce-item">'
        + '<h2>' + escape(item.producto) + '</h2><p>' + money(item.precioCopKg)
        + '/kg · Subtotal: ' + money(item.subtotal) + '</p><p data-expiry="'
        + escape(item.venceEn) + '">Reserva hasta ' + date(item.venceEn) + '</p>'
        + '<form data-cart-item="' + item.cosechaId + '" class="commerce-form">'
        + '<label>Cantidad total (kg)<input name="cantidad" type="number" min="0.001"'
        + ' step="0.001" max="99999999999.999" required value="' + item.cantidadKg + '"></label>'
        + '<button class="button button--light" type="submit">Actualizar y renovar reserva</button>'
        + '<button class="button button--light" type="button" data-remove="' + item.cosechaId
        + '">Quitar</button>' + output + '</form></article>').join('')
        + '<form data-checkout><h2>Total de productos: ' + money(cart.total)
        + '</h2><p>Sin cobro en línea. Este importe no incluye transporte. Acuerda la entrega'
        + ' con el agricultor antes de confirmar. Solo un agricultor por pedido.</p>'
        + '<button class="button" type="submit">Confirmar pedido</button>' + output + '</form>';
      cartRoot.querySelectorAll('[data-cart-item]').forEach((form) => {
        form.addEventListener('submit', (event) => {
          event.preventDefault();
          action(form, async () => {
            await request('/carrito/items', { method: 'POST', body: JSON.stringify({
              cosechaId: Number(form.dataset.cartItem), cantidadKg: form.elements.cantidad.value
            }) });
            await loadCart();
          });
        });
        form.querySelector('[data-remove]').addEventListener('click', async (event) => {
          const button = event.currentTarget;
          button.disabled = true;
          try {
            await request('/carrito/items/' + form.dataset.cartItem, { method: 'DELETE' });
            await loadCart();
          } catch (error) { feedback(form, error.message); button.disabled = false; }
        });
      });
      const checkout = cartRoot.querySelector('[data-checkout]');
      const tick = () => {
        let expired = false;
        cartRoot.querySelectorAll('[data-expiry]').forEach((element) => {
          const remaining = new Date(element.dataset.expiry).getTime() - Date.now() - offset;
          if (remaining <= 0) {
            expired = true;
            element.textContent = 'Reserva vencida. Actualiza la cantidad para renovarla o quítala.';
          } else {
            element.textContent = 'Reserva vigente: quedan ' + Math.ceil(remaining / 60000)
              + ' min · Vence ' + date(element.dataset.expiry);
          }
        });
        if (!checkout.dataset.pending) checkout.querySelector('button').disabled = expired;
      };
      tick();
      cartTimer = setInterval(tick, 1000);
      checkout.addEventListener('submit', (event) => {
        event.preventDefault();
        action(checkout, async () => {
          checkout.dataset.pending = 'true';
          try {
            const storageKey = 'agrovalle.checkout.' + session().user.id + '.' + cart.id;
            let key = sessionStorage.getItem(storageKey);
            if (!key) {
              key = 'pedido-' + cart.id + '-' + crypto.randomUUID();
              sessionStorage.setItem(storageKey, key);
            }
            const result = await request('/ordenes', { method: 'POST',
              headers: { 'Idempotency-Key': key }, body: JSON.stringify({ carritoId: cart.id }) });
            location.assign('seguimiento.html?id=' + result.pedido.id);
          } finally { delete checkout.dataset.pending; }
        });
      });
    } catch (error) { cartRoot.textContent = error.message; }
  };

  const orderRoot = document.querySelector('[data-orders]');
  const loadOrders = async () => {
    if (!orderRoot) return;
    try {
      const orders = await request('/ordenes');
      const visibleOrders = orders.slice(0, Number(orderRoot.dataset.orderLimit) || orders.length);
      orderRoot.innerHTML = orders.length ? visibleOrders.map((order) =>
        '<article class="commerce-item"><p class="eyebrow">Pedido #' + order.id + '</p><h2>'
        + escape(labels[order.estado] || order.estado) + '</h2><p>' + money(order.total)
        + ' · ' + date(order.creadoEn) + '</p><a class="button button--light" href="seguimiento.html?id='
        + order.id + '">Ver pedido y seguimiento</a></article>').join('')
        : '<p>Aún no tienes pedidos registrados.</p>';
      const count = document.querySelector('[data-order-count]');
      if (count) count.textContent = String(orders.length);
    } catch (error) { orderRoot.textContent = error.message; }
  };

  const traceRoot = document.querySelector('[data-order-detail]');
  const tracking = document.querySelector('[data-order-search]');
  const loadTrace = async (id) => {
    if (!traceRoot) return;
    traceRoot.textContent = 'Consultando el pedido…';
    try {
      if (!/^[1-9]\d*$/.test(String(id))) throw new Error('Indica un número de pedido válido.');
      const detail = await request('/ordenes/' + id + '/trazabilidad');
      const order = detail.pedido;
      traceRoot.innerHTML = '<h2>Pedido #' + order.id + ' · ' + escape(labels[order.estado])
        + '</h2><p>Total de productos: ' + money(order.total) + '</p><p>Entrega programada: '
        + date(order.fechaProgramada) + '</p><p>Ruta: ' + escape(order.ruta || 'Por acordar')
        + '</p><ul>' + detail.items.map((item) => '<li>' + escape(item.producto) + ': '
          + kg(item.cantidadKg) + ' kg × ' + money(item.precioCopKg) + '/kg</li>').join('')
        + '</ul><h3>Historial del pedido</h3><ol class="order-events">'
        + detail.eventos.map((event) => '<li><strong>' + escape(labels[event.estado])
          + '</strong><p>' + date(event.fecha) + '</p><p>' + escape(event.descripcion)
          + '</p></li>').join('') + '</ol>';
      const farmer = session().user?.rol === 'AGRICULTOR';
      if (!farmer && order.estado === 'PENDIENTE') {
        traceRoot.insertAdjacentHTML('beforeend', '<form data-cancel><label class="commerce-confirm">'
          + '<input type="checkbox" required> Confirmo que deseo cancelar este pedido.</label>'
          + '<button class="button button--light" type="submit">Cancelar pedido</button>'
          + output + '</form>');
        const form = traceRoot.querySelector('[data-cancel]');
        form.addEventListener('submit', (event) => {
          event.preventDefault();
          action(form, async () => {
            await request('/ordenes/' + id + '/cancelacion', { method: 'POST' });
            await loadTrace(id);
          });
        });
      }
      const next = { PENDIENTE: 'LISTO_PARA_DESPACHO',
        LISTO_PARA_DESPACHO: 'EN_CAMINO', EN_CAMINO: 'ENTREGADO' }[order.estado];
      if (farmer && next) {
        const schedule = next === 'LISTO_PARA_DESPACHO';
        traceRoot.insertAdjacentHTML('beforeend', '<form data-dispatch class="commerce-form">'
          + (schedule ? '<label>Fecha y hora de entrega (Colombia)<input name="fecha"'
          + ' type="datetime-local" required></label><label>Ruta acordada con el comprador'
          + '<textarea name="ruta" maxlength="500" required rows="3"></textarea></label>' : '')
          + '<label class="commerce-confirm"><input type="checkbox" required> Confirmo que el pedido'
          + ' está listo para pasar a: ' + escape(labels[next]) + '.</label>'
          + '<button class="button" type="submit">Marcar: ' + escape(labels[next])
          + '</button>' + output + '</form>');
        const form = traceRoot.querySelector('[data-dispatch]');
        form.addEventListener('submit', (event) => {
          event.preventDefault();
          action(form, async () => {
            const data = { estado: next };
            if (schedule) {
              data.fechaProgramada = form.elements.fecha.value + ':00-05:00';
              data.ruta = form.elements.ruta.value.trim();
            }
            await request('/ordenes/' + id + '/estado', { method: 'PATCH',
              body: JSON.stringify(data) });
            await loadTrace(id);
          });
        });
      }
    } catch (error) { traceRoot.textContent = error.message; }
  };
  if (tracking) {
    tracking.addEventListener('submit', (event) => {
      event.preventDefault();
      loadTrace(tracking.elements.pedido.value);
    });
    const id = new URLSearchParams(location.search).get('id');
    if (id) { tracking.elements.pedido.value = id; loadTrace(id); }
  }

  const inventory = document.querySelector('[data-inventory]');
  const loadInventory = async () => {
    if (!inventory) return;
    try {
      const entries = await request('/mis-ofertas');
      inventory.innerHTML = entries.length ? entries.map((entry) => {
        const offer = entry.oferta;
        return '<article class="commerce-item"><h3>' + escape(offer.producto)
          + '</h3><p>Reservados: ' + kg(entry.cantidadReservadaKg) + ' kg · Disponibles: '
          + kg(entry.cantidadDisponibleKg) + ' kg</p><form class="commerce-form" data-edit-offer="'
          + offer.id + '"><label>Existencias físicas (kg)<input name="cantidad" type="number"'
          + ' min="0" max="99999999999.999" step="0.001" value="' + offer.cantidadFisicaKg
          + '" required></label><label>Precio COP/kg<input name="precio" type="number" min="0.01"'
          + ' max="999999999999.99" step="0.01" value="' + offer.precioCopKg + '" required></label>'
          + '<button class="button button--light" type="submit">Guardar cambios</button>'
          + output + '</form></article>';
      }).join('') : '<p>No has publicado cosechas.</p>';
      const count = document.querySelector('[data-product-count]');
      if (count) count.textContent = String(entries.length);
      const farmCount = document.querySelector('[data-farm-count]');
      if (farmCount) request('/fincas').then((farms) => {
        farmCount.textContent = String(farms.length);
      }).catch(() => { farmCount.textContent = 'No disponible'; });
      inventory.querySelectorAll('[data-edit-offer]').forEach((form) => {
        form.addEventListener('submit', (event) => {
          event.preventDefault();
          action(form, async () => {
            await request('/productos/' + form.dataset.editOffer, { method: 'PUT',
              body: JSON.stringify({ cantidadFisicaKg: form.elements.cantidad.value,
                precioCopKg: form.elements.precio.value }) });
            await loadInventory();
          });
        });
      });
    } catch (error) { inventory.textContent = error.message; }
  };

  const alerts = document.querySelector('[data-order-alerts]');
  if (alerts) request('/notificaciones/pedidos').then((items) => {
    alerts.innerHTML = items.length ? '<ul>' + items.map((item) => '<li><a href="seguimiento.html?id='
      + item.pedidoId + '">Pedido #' + item.pedidoId + '</a>: ' + escape(item.mensaje)
      + ' · ' + date(item.fecha) + '</li>').join('') + '</ul>' : '<p>No tienes avisos de pedidos.</p>';
  }).catch((error) => { alerts.textContent = error.message; });

  const admin = document.querySelector('[data-admin-summary]');
  if (admin) request('/administracion/resumen').then((data) => {
    const names = { usuarios: 'Usuarios', fincas: 'Fincas', ofertas: 'Ofertas', pedidos: 'Pedidos',
      pendientes: 'Pendientes', entregados: 'Entregados', cancelados: 'Cancelados' };
    admin.innerHTML = Object.entries(names).map(([key, label]) => '<article class="metric-card">'
      + '<h2>' + label + '</h2><strong>' + Number(data[key]) + '</strong></article>').join('');
  }).catch((error) => { admin.textContent = error.message; });

  const prices = document.querySelector('[data-prices-form]');
  if (prices) prices.addEventListener('submit', (event) => {
    event.preventDefault();
    action(prices, async () => {
      const data = await request('/precios/promedio?producto='
        + encodeURIComponent(prices.elements.producto.value.trim()), { public: true });
      feedback(prices, data.promedio === null ? 'No hay ventas completadas en las últimas 24 horas.'
        : 'Promedio: ' + money(data.promedio) + '/kg · ' + data.cantidadTransacciones
          + ' ventas completadas en las últimas 24 horas.', true);
    });
  });
  loadCart();
  loadOrders();
  loadInventory();
})();
