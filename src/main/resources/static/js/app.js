/*
 * Historial de operaciones: mismo comportamiento que la pantalla de osi-front (Administración).
 * Sin frameworks ni librerías externas. Los textos se insertan con textContent (nunca innerHTML)
 * porque el detalle lo escriben los usuarios.
 */
(function () {
  'use strict';

  var PAGE_SIZE = 10;

  // La app vive en un context root de un nivel (/osi-reporte), en WebLogic y en local.
  var API = '/' + location.pathname.split('/')[1] + '/api/auditoria';

  var OPERACION = {
    REGISTRO: { label: 'Registro', tone: 'info' },
    ACTUALIZACION: { label: 'Actualización', tone: 'warning' },
    ESTIMACION: { label: 'Estimación', tone: 'success' },
    APROBACION: { label: 'Aprobación', tone: 'success' },
    RECHAZO: { label: 'Rechazo', tone: 'danger' },
  };

  var CHEVRON = {
    left: 'm15 18-6-6 6-6',
    right: 'm9 18 6-6-6-6',
  };

  var $ = function (id) {
    return document.getElementById(id);
  };
  var form = $('filtros');
  var desde = $('aud-desde');
  var hasta = $('aud-hasta');
  var usuario = $('aud-usuario');
  var error = $('error');
  var filas = $('filas');
  var tarjetas = $('tarjetas');
  var resumen = $('resumen');
  var paginas = $('paginas');
  var boton = form.querySelector('button[type="submit"]');

  var eventos = [];
  var page = 1;
  var cargando = false;

  // dd/MM/yyyy HH:mm en la hora local del navegador (igual que el DatePipe de Angular).
  function formatearFecha(iso) {
    var d = new Date(iso);
    var dos = function (n) {
      return String(n).padStart(2, '0');
    };
    return dos(d.getDate()) + '/' + dos(d.getMonth() + 1) + '/' + d.getFullYear() + ' ' + dos(d.getHours()) + ':' + dos(d.getMinutes());
  }

  function el(tag, className, text) {
    var node = document.createElement(tag);
    if (className) node.className = className;
    if (text != null) node.textContent = text;
    return node;
  }

  function badge(operacion) {
    var op = OPERACION[operacion] || { label: operacion, tone: 'neutral' };
    return el('span', 'badge badge--' + op.tone, op.label);
  }

  function mostrarError(mensaje) {
    error.textContent = mensaje;
    error.hidden = !mensaje;
  }

  function render() {
    var start = (page - 1) * PAGE_SIZE;
    var visibles = eventos.slice(start, start + PAGE_SIZE);
    var vacio = cargando ? 'Cargando...' : 'No hay operaciones para los filtros seleccionados.';

    filas.replaceChildren();
    tarjetas.replaceChildren();

    if (visibles.length === 0) {
      var tr = el('tr');
      var td = el('td', 'empty', vacio);
      td.colSpan = 5;
      tr.appendChild(td);
      filas.appendChild(tr);
      tarjetas.appendChild(el('li', 'empty', vacio));
    }

    visibles.forEach(function (e) {
      var fecha = formatearFecha(e.fecha);

      var tr = el('tr');
      tr.appendChild(el('td', 'nowrap tabular', fecha));
      tr.appendChild(el('td', 'nowrap', e.usuario));
      var tdOp = el('td');
      tdOp.appendChild(badge(e.operacion));
      tr.appendChild(tdOp);
      tr.appendChild(el('td', 'nowrap', e.requerimientoCodigo));
      tr.appendChild(el('td', null, e.detalle || ''));
      filas.appendChild(tr);

      var li = el('li');
      var top = el('div', 'card-item__top');
      var info = el('div');
      info.appendChild(el('p', 'card-item__meta tabular', fecha + ' · ' + e.usuario));
      info.appendChild(el('p', 'card-item__codigo', e.requerimientoCodigo));
      top.appendChild(info);
      top.appendChild(badge(e.operacion));
      li.appendChild(top);
      li.appendChild(el('p', 'card-item__detalle', e.detalle || ''));
      tarjetas.appendChild(li);
    });

    renderPaginacion();
  }

  function flecha(direccion, label, destino, disabled) {
    var b = el('button', 'page-btn page-btn--arrow');
    b.type = 'button';
    b.disabled = disabled;
    b.setAttribute('aria-label', label);
    b.innerHTML =
      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="' +
      CHEVRON[direccion] +
      '"/></svg>';
    b.addEventListener('click', function () {
      irA(destino);
    });
    return b;
  }

  function renderPaginacion() {
    var total = eventos.length;
    var totalPages = Math.max(1, Math.ceil(total / PAGE_SIZE));
    var from = total === 0 ? 0 : (page - 1) * PAGE_SIZE + 1;
    var to = Math.min(page * PAGE_SIZE, total);
    resumen.textContent = 'Mostrando ' + from + ' a ' + to + ' de ' + total + ' registros';

    paginas.replaceChildren();
    paginas.appendChild(flecha('left', 'Página anterior', page - 1, page === 1));
    for (var p = 1; p <= totalPages; p++) {
      var b = el('button', 'page-btn' + (p === page ? ' page-btn--active' : ''), String(p));
      b.type = 'button';
      if (p === page) b.setAttribute('aria-current', 'page');
      b.addEventListener('click', irA.bind(null, p));
      paginas.appendChild(b);
    }
    paginas.appendChild(flecha('right', 'Página siguiente', page + 1, page === totalPages));
  }

  function irA(p) {
    var totalPages = Math.max(1, Math.ceil(eventos.length / PAGE_SIZE));
    if (p >= 1 && p <= totalPages) {
      page = p;
      render();
    }
  }

  function mensajeDeError(respuesta, cuerpo, porDefecto) {
    return (cuerpo && cuerpo.message) || (respuesta ? porDefecto + ' (HTTP ' + respuesta.status + ')' : porDefecto);
  }

  function consultar() {
    if (desde.value && hasta.value && desde.value > hasta.value) {
      mostrarError('La fecha "Desde" no puede ser posterior a "Hasta".');
      return;
    }
    mostrarError('');

    var params = new URLSearchParams();
    if (desde.value) params.set('desde', desde.value);
    if (hasta.value) params.set('hasta', hasta.value);
    if (usuario.value) params.set('usuario', usuario.value);

    cargando = true;
    boton.disabled = true;
    render();

    fetch(API + (params.toString() ? '?' + params : ''), { headers: { Accept: 'application/json' } })
      .then(function (res) {
        return res
          .json()
          .catch(function () {
            return null;
          })
          .then(function (cuerpo) {
            if (!res.ok) throw new Error(mensajeDeError(res, cuerpo, 'No se pudo consultar el historial.'));
            return cuerpo;
          });
      })
      .then(function (lista) {
        eventos = lista || [];
        page = 1;
      })
      .catch(function (err) {
        eventos = [];
        mostrarError(err instanceof TypeError ? 'No se pudo conectar con el servidor. Intente nuevamente.' : err.message);
      })
      .finally(function () {
        cargando = false;
        boton.disabled = false;
        render();
      });
  }

  function cargarUsuarios() {
    fetch(API + '/usuarios', { headers: { Accept: 'application/json' } })
      .then(function (res) {
        return res.ok ? res.json() : [];
      })
      .then(function (usuarios) {
        usuarios.forEach(function (u) {
          var option = el('option', null, u);
          option.value = u;
          usuario.appendChild(option);
        });
      })
      .catch(function () {
        // El filtro queda solo con "Todos"; la consulta principal informa el error de conexión.
      });
  }

  form.addEventListener('submit', function (e) {
    e.preventDefault();
    consultar();
  });

  cargarUsuarios();
  consultar();
})();
