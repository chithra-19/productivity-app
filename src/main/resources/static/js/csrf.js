// Adds the CSRF token to every POST/PUT/DELETE request made with fetch() or axios
(function () {
  var t = document.querySelector('meta[name="_csrf"]');
  var h = document.querySelector('meta[name="_csrf_header"]');
  if (!t || !h) return;
  var token = t.content, header = h.content;

  var origFetch = window.fetch;
  window.fetch = function (input, init) {
    init = init || {};
    var method = (init.method || (input && input.method) || 'GET').toUpperCase();
    if (method !== 'GET' && method !== 'HEAD' && method !== 'OPTIONS') {
      var url = typeof input === 'string' ? input : ((input && input.url) || '');
      var sameSite = (url.startsWith('/') && !url.startsWith('//')) || url.startsWith(location.origin);
      if (sameSite) {
        var headers = new Headers(init.headers || (input instanceof Request ? input.headers : undefined));
        headers.set(header, token);
        init.headers = headers;
      }
    }
    return origFetch.call(this, input, init);
  };

  if (window.axios) {
    axios.defaults.headers.common[header] = token;
  }
})();