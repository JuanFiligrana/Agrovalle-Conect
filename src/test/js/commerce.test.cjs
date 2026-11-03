const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');

const source = fs.readFileSync('src/main/resources/static/assets/commerce.js', 'utf8');
const element = (extra = {}) => ({
  dataset: {}, textContent: '', innerHTML: '', disabled: false, handlers: {},
  addEventListener(name, handler) { this.handlers[name] = handler; },
  querySelector() { return null; },
  querySelectorAll() { return []; },
  ...extra
});
const form = (elements = {}) => {
  const button = element();
  const feedback = element();
  return element({ elements, button, feedback,
    querySelector(selector) { return selector === '[data-feedback]' ? feedback : button; }
  });
};
const flush = async () => { for (let i = 0; i < 6; i++) await new Promise(setImmediate); };
const submit = async (target) => { target.handlers.submit({ preventDefault() {} }); await flush(); };
function start(nodes, responses, role = 'COMPRADOR', requiredRole = undefined) {
  const storage = new Map();
  if (role) storage.set('agrovalle.session', JSON.stringify({
    accessToken: 'token-solo-pruebas', user: { id: 7, rol: role }
  }));
  const calls = [];
  const navigations = [];
  const timers = [];
  const body = { dataset: { requiresRole: requiredRole }, hasAttribute: () => false };
  const sandbox = {
    document: { body, querySelector: (selector) => nodes[selector] || null },
    sessionStorage: { getItem: (key) => storage.get(key) || null,
      setItem: (key, value) => storage.set(key, value), removeItem: (key) => storage.delete(key) },
    location: { search: '?id=12', pathname: '/cosecha.html',
      assign: (value) => navigations.push(value) },
    crypto: { randomUUID: () => '11111111-2222-3333-4444-555555555555' },
    URLSearchParams, Date, Number, String, Object, JSON, Math, Error,
    setInterval: (callback) => { timers.push(callback); return timers.length; },
    clearInterval() {},
    fetch: async (path, config) => {
      calls.push({ path, config });
      const answer = await responses(path, config, calls.length);
      if (answer instanceof Error) throw answer;
      return { ok: answer.status ? answer.status < 400 : true, status: answer.status || 200,
        json: async () => answer.data };
    }
  };
  vm.runInNewContext(source, sandbox, { filename: 'commerce.js' });
  return { calls, navigations, storage, timers, sandbox };
}

test('no consulta rutas privadas sin sesión o con un rol de pantalla incorrecto', async () => {
  for (const role of [null, 'AGRICULTOR']) {
    const app = start({ '[data-cart]': element() }, () => { throw Error('No debe llamar'); },
      role, 'COMPRADOR');
    await flush();
    assert.equal(app.calls.length, 0);
  }
});

test('consulta precios públicos y explica cuando no hay ventas', async () => {
  const prices = form({ producto: { value: ' Tomate ' } });
  const app = start({ '[data-prices-form]': prices }, () => ({
    data: { promedio: null, cantidadTransacciones: 0 }
  }), null);
  await submit(prices);
  assert.equal(app.calls[0].path, '/api/v1/precios/promedio?producto=Tomate');
  assert.equal(app.calls[0].config.headers.Authorization, undefined);
  assert.match(prices.feedback.textContent, /No hay ventas completadas/);
  assert.equal(prices.button.disabled, false);
});

test('reserva envía cantidad y oferta autenticadas y muestra el vencimiento', async () => {
  const reserve = form({ cantidad: { value: '2.500' } });
  const availability = element();
  const app = start({ '[data-reserve-form]': reserve, '[data-availability]': availability },
    (path) => path.endsWith('/carrito/items') ? {
      data: { items: [{ cosechaId: 12, venceEn: '2030-01-01T12:00:00Z' }] }
    } : { data: { cantidadKg: 7.5 } });
  await submit(reserve);
  assert.deepEqual(JSON.parse(app.calls[0].config.body), { cosechaId: 12, cantidadKg: '2.500' });
  assert.equal(app.calls[0].config.headers.Authorization, 'Bearer token-solo-pruebas');
  assert.match(reserve.feedback.textContent, /Reserva guardada/);
  assert.match(availability.textContent, /7,5 kg/);
});

test('el invitado vuelve al detalle después de iniciar sesión para reservar', async () => {
  const reserve = form({ cantidad: { value: '1' } });
  const app = start({ '[data-reserve-form]': reserve }, () => ({}), null);
  await submit(reserve);
  assert.equal(app.calls.length, 0);
  assert.equal(app.navigations[0], 'login.html?next=cosecha.html%3Fid%3D12');
});

test('el carrito vacío invita a explorar cosechas sin inventar productos', async () => {
  const cart = element();
  start({ '[data-cart]': cart }, () => ({ data: { id: 4, items: [] } }));
  await flush();
  assert.match(cart.innerHTML, /Tu carrito está vacío/);
  assert.doesNotMatch(cart.innerHTML, /data-checkout/);
});

test('confirmar tras un fallo de red reutiliza la clave y el mismo carrito', async () => {
  const checkout = form();
  const expiry = element({ dataset: { expiry: new Date(Date.now() + 600000).toISOString() } });
  const cart = element({
    querySelector: () => checkout,
    querySelectorAll: (selector) => selector === '[data-expiry]' ? [expiry] : []
  });
  const app = start({ '[data-cart]': cart }, (path, config, count) => {
    if (path.endsWith('/carrito')) return { data: {
      id: 9, total: 1000, fechaConsulta: new Date().toISOString(),
      items: [{ cosechaId: 12, producto: '<script>alert(1)</script>', cantidadKg: 1,
        precioCopKg: 1000, subtotal: 1000, venceEn: expiry.dataset.expiry }]
    } };
    return count === 2 ? Error('Conexión interrumpida') : { data: { pedido: { id: 25 } } };
  });
  await flush();
  assert.match(cart.innerHTML, /&lt;script&gt;/);
  assert.doesNotMatch(cart.innerHTML, /<script>/);
  await submit(checkout);
  assert.match(checkout.feedback.textContent, /Conexión interrumpida/);
  assert.equal(app.navigations.length, 0);
  await submit(checkout);
  const confirms = app.calls.filter((call) => call.path.endsWith('/ordenes'));
  assert.equal(confirms.length, 2);
  assert.equal(confirms[0].config.headers['Idempotency-Key'],
    confirms[1].config.headers['Idempotency-Key']);
  assert.deepEqual(JSON.parse(confirms[1].config.body), { carritoId: 9 });
  assert.equal(app.navigations[0], 'seguimiento.html?id=25');
});

test('una reserva vencida deshabilita la confirmación y ofrece renovarla', async () => {
  const checkout = form();
  const expiry = element({ dataset: { expiry: '2020-01-01T12:00:00Z' } });
  const cart = element({
    querySelector: () => checkout,
    querySelectorAll: (selector) => selector === '[data-expiry]' ? [expiry] : []
  });
  start({ '[data-cart]': cart }, () => ({ data: {
    id: 2, fechaConsulta: new Date().toISOString(), total: 1000,
    items: [{ cosechaId: 12, producto: 'Tomate', cantidadKg: 1, precioCopKg: 1000,
      subtotal: 1000, venceEn: expiry.dataset.expiry }]
  } }));
  await flush();
  assert.equal(checkout.button.disabled, true);
  assert.match(expiry.textContent, /Reserva vencida/);
});

test('el seguimiento muestra errores de autorización sin datos de ejemplo', async () => {
  const tracking = form({ pedido: { value: '12' } });
  const detail = element();
  const app = start({ '[data-order-search]': tracking, '[data-order-detail]': detail },
    () => ({ status: 403, data: { message: 'No tienes permiso para este pedido' } }));
  await flush();
  assert.equal(app.calls[0].path, '/api/v1/ordenes/12/trazabilidad');
  assert.equal(detail.textContent, 'No tienes permiso para este pedido');
  assert.equal(detail.innerHTML, '');
});

test('administración no muestra cifras ficticias cuando se rechaza el permiso', async () => {
  const admin = element();
  start({ '[data-admin-summary]': admin },
    () => ({ status: 403, data: { message: 'Acceso denegado' } }), 'ADMINISTRADOR');
  await flush();
  assert.equal(admin.textContent, 'Acceso denegado');
  assert.equal(admin.innerHTML, '');
});

test('un token vencido elimina la sesión y muestra un mensaje de error', async () => {
  const cart = element();
  const app = start({ '[data-cart]': cart },
    () => ({ status: 401, data: { message: 'Sesión vencida' } }));
  await flush();
  assert.equal(app.storage.has('agrovalle.session'), false);
  assert.equal(cart.textContent, 'Sesión vencida');
});
