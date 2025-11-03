'use strict';

var core = require('@capacitor/core');

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
//# sourceMappingURL=plugin.cjs.js.map
