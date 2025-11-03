var capacitorGallerySaver = (function (exports, core) {
    'use strict';

    const GallerySaver = core.registerPlugin('GallerySaver', {
        web: () => Promise.resolve().then(function () { return web; }).then(m => new m.GallerySaverWeb()),
    });

    class GallerySaverWeb extends core.WebPlugin {
        async saveAuto() {
            console.warn('GallerySaver.saveAuto() не поддерживается в Web');
            return { saved: false };
        }
    }

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        GallerySaverWeb: GallerySaverWeb
    });

    exports.GallerySaver = GallerySaver;

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
